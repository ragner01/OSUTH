#!/bin/bash
# PostgreSQL PITR Backup Script
# Automated Point-in-Time Recovery backups for Osun HIS

set -e

# Configuration
BACKUP_DIR="${BACKUP_DIR:-/backups/postgres}"
RETENTION_DAILY=${RETENTION_DAILY:-7}
RETENTION_WEEKLY=${RETENTION_WEEKLY:-4}
RETENTION_MONTHLY=${RETENTION_MONTHLY:-12}
PGHOST="${PGHOST:-localhost}"
PGPORT="${PGPORT:-5432}"
PGUSER="${PGUSER:-postgres}"
PGDATABASE="${PGDATABASE:-osun_his}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Logging
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" >&2
}

# Function to perform backup
perform_backup() {
    local backup_type=$1
    local timestamp=$(date +"%Y%m%d_%H%M%S")
    local backup_file="${BACKUP_DIR}/${backup_type}/${PGDATABASE}_${backup_type}_${timestamp}.dump"
    
    log "Starting ${backup_type} backup for ${PGDATABASE}..."
    
    # Create backup directory if it doesn't exist
    mkdir -p "${BACKUP_DIR}/${backup_type}"
    
    # Perform pg_dump with custom format for PITR
    if pg_dump -h "$PGHOST" -p "$PGPORT" -U "$PGUSER" \
        -F custom \
        -f "$backup_file" \
        -v \
        "$PGDATABASE" 2>&1 | tee "${backup_file}.log"; then
        
        # Compress backup
        gzip "$backup_file"
        backup_file="${backup_file}.gz"
        
        log "Backup completed successfully: ${backup_file}"
        
        # Get backup size
        local size=$(du -h "$backup_file" | cut -f1)
        echo "Backup size: ${size}"
        
        # Calculate and verify checksum
        local checksum_file="${backup_file}.sha256"
        sha256sum "$backup_file" > "$checksum_file"
        log "Checksum saved: ${checksum_file}"
        
        # Verify backup integrity
        if sha256sum -c "$checksum_file" --quiet 2>/dev/null; then
            log "Backup integrity verified: OK"
        else
            error "Backup integrity check FAILED"
            exit 1
        fi
        
        echo "$backup_file" > "${BACKUP_DIR}/latest_${backup_type}.txt"
        
        return 0
    else
        error "Backup failed"
        exit 1
    fi
}

# Function to clean old backups
clean_old_backups() {
    local backup_type=$1
    local retention_days=$2
    
    if [ -z "$retention_days" ]; then
        return 0
    fi
    
    log "Cleaning ${backup_type} backups older than ${retention_days} days..."
    
    find "${BACKUP_DIR}/${backup_type}" -name "*.dump.gz" -type f -mtime +${retention_days} -delete
    find "${BACKUP_DIR}/${backup_type}" -name "*.sha256" -type f -mtime +${retention_days} -delete
    find "${BACKUP_DIR}/${backup_type}" -name "*.log" -type f -mtime +${retention_days} -delete
    
    log "Old backups cleaned"
}

# Main execution
main() {
    local backup_type=${1:-daily}
    
    log "===== Osun HIS PostgreSQL Backup Process ====="
    log "Host: ${PGHOST}:${PGPORT}"
    log "Database: ${PGDATABASE}"
    log "Backup Type: ${backup_type}"
    log "Backup Directory: ${BACKUP_DIR}"
    
    # Perform backup
    perform_backup "$backup_type"
    
    # Clean old backups
    case "$backup_type" in
        daily)
            clean_old_backups "$backup_type" "$RETENTION_DAILY"
            ;;
        weekly)
            clean_old_backups "$backup_type" "$RETENTION_WEEKLY"
            ;;
        monthly)
            clean_old_backups "$backup_type" "$RETENTION_MONTHLY"
            ;;
    esac
    
    log "===== Backup Process Completed Successfully ====="
}

# Run main function with arguments
main "$@"

