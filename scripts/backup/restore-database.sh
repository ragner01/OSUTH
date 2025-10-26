#!/bin/bash
# PostgreSQL Restore Script
# Restore database from PITR backup

set -e

# Configuration
BACKUP_DIR="${BACKUP_DIR:-/backups/postgres}"
PGHOST="${PGHOST:-localhost}"
PGPORT="${PGPORT:-5432}"
PGUSER="${PGUSER:-postgres}"
PGDATABASE="${PGDATABASE:-osun_his}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" >&2
}

warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Prompt for confirmation
confirm_restore() {
    warning "This will DESTROY all data in database ${PGDATABASE}"
    warning "Are you sure you want to continue? (yes/no)"
    read -r confirmation
    
    if [ "$confirmation" != "yes" ]; then
        log "Restore cancelled by user"
        exit 0
    fi
}

# List available backups
list_backups() {
    local backup_type=${1:-daily}
    log "Available ${backup_type} backups:"
    echo ""
    
    local backups=$(ls -1t "${BACKUP_DIR}/${backup_type}"/*.dump.gz 2>/dev/null || true)
    
    if [ -z "$backups" ]; then
        error "No backups found in ${BACKUP_DIR}/${backup_type}/"
        exit 1
    fi
    
    local count=1
    for backup in $backups; do
        local filename=$(basename "$backup")
        local size=$(du -h "$backup" | cut -f1)
        local date=$(stat -f "%Sm" "$backup" 2>/dev/null || stat -c "%y" "$backup")
        echo "${count}. ${filename}"
        echo "   Size: ${size} | Date: ${date}"
        echo ""
        ((count++))
    done
}

# Restore from backup
restore_backup() {
    local backup_file=$1
    
    if [ ! -f "$backup_file" ]; then
        error "Backup file not found: ${backup_file}"
        exit 1
    fi
    
    # Verify checksum
    local checksum_file="${backup_file}.sha256"
    if [ -f "$checksum_file" ]; then
        log "Verifying backup integrity..."
        if ! sha256sum -c "$checksum_file" --quiet; then
            error "Backup integrity check FAILED - backup may be corrupted"
            exit 1
        fi
        log "Backup integrity verified: OK"
    else
        warning "No checksum file found - skipping integrity check"
    fi
    
    # Confirm restore
    confirm_restore
    
    log "Starting restore from: ${backup_file}"
    
    # Decompress if needed
    local temp_backup="/tmp/restore.dump"
    if [[ "$backup_file" == *.gz ]]; then
        log "Decompressing backup..."
        gunzip -c "$backup_file" > "$temp_backup"
    else
        cp "$backup_file" "$temp_backup"
    fi
    
    # Drop and recreate database
    log "Dropping existing database..."
    dropdb -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" --if-exists "$PGDATABASE" || true
    
    log "Creating new database..."
    createdb -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" "$PGDATABASE"
    
    # Restore from backup
    log "Restoring data from backup..."
    if pg_restore -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" \
        -d "$PGDATABASE" \
        -v \
        --no-owner \
        --no-privileges \
        "$temp_backup"; then
        
        log "Restore completed successfully"
    else
        error "Restore failed"
        rm -f "$temp_backup"
        exit 1
    fi
    
    # Cleanup temp file
    rm -f "$temp_backup"
    
    log "Database ${PGDATABASE} has been restored successfully"
}

# Main execution
main() {
    local backup_type=${1:-daily}
    local backup_num=${2:-1}
    
    log "===== Osun HIS PostgreSQL Restore Process ====="
    
    # List available backups
    list_backups "$backup_type"
    
    # Get the backup file
    local backup_file=$(ls -1t "${BACKUP_DIR}/${backup_type}"/*.dump.gz 2>/dev/null | sed -n "${backup_num}p")
    
    if [ -z "$backup_file" ]; then
        error "Backup not found"
        exit 1
    fi
    
    # Show selected backup
    log "Selected backup: ${backup_file}"
    
    # Restore
    restore_backup "$backup_file"
    
    log "===== Restore Process Completed Successfully ====="
}

# Run main function with arguments
main "$@"

