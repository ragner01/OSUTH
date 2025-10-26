# Disaster Recovery & Backup Runbook

## Overview

This runbook provides procedures for backup, restore, and disaster recovery operations for Osun HIS.

---

## 🗄️ Backup Procedures

### Automated Daily Backups

**Schedule:** Every night at 02:00  
**Retention:** 7 days  
**Location:** `/backups/postgres/daily/`

```bash
# Manual backup
sudo /opt/osun-his/scripts/backup/postgres-backup.sh daily
```

### Weekly Backups

**Schedule:** Every Sunday at 03:00  
**Retention:** 4 weeks  
**Location:** `/backups/postgres/weekly/`

```bash
sudo /opt/osun-his/scripts/backup/postgres-backup.sh weekly
```

### Monthly Backups

**Schedule:** 1st of each month at 04:00  
**Retention:** 12 months  
**Location:** `/backups/postgres/monthly/`

```bash
sudo /opt/osun-his/scripts/backup/postgres-backup.sh monthly
```

### Backup Verification

All backups include SHA256 checksums for integrity verification:

```bash
# Verify latest backup
cd /backups/postgres/daily
sha256sum -c $(ls -t *.gz.sha256 | head -1)
```

---

## 🔄 Restore Procedures

### Full Database Restore

**Warning:** This will destroy all existing data!

```bash
# List available backups
sudo /opt/osun-his/scripts/backup/restore-database.sh daily

# Restore from backup #1
sudo /opt/osun-his/scripts/backup/restore-database.sh daily 1
```

### Point-in-Time Recovery (PITR)

For PITR, you need both a base backup and WAL files:

```bash
# Restore from specific timestamp
pg_restore \
  --dbname=osun_his \
  --create \
  --clean \
  --if-exists \
  --exit-on-error \
  /backups/postgres/daily/latest.dump.gz
```

### Selective Restore (Single Table)

```bash
# Restore specific table
gunzip -c /backups/postgres/daily/latest.dump.gz | \
  pg_restore \
    --table=patients \
    --dbname=osun_his \
    --no-owner \
    --no-privileges
```

---

## 🚨 Disaster Recovery Scenarios

### Scenario 1: Complete Server Failure

**Estimated Recovery Time:** 2-4 hours

**Steps:**
1. Provision new infrastructure (see Terraform scripts)
2. Restore database from latest backup
3. Restore Redis cache (optional, will rebuild)
4. Restore Kafka data (if needed)
5. Verify system health
6. Notify stakeholders

### Scenario 2: Data Corruption

**Estimated Recovery Time:** 30-60 minutes

**Steps:**
1. Stop all services
2. Identify affected tables
3. Restore from last known good backup
4. Apply any transactions from WAL (if possible)
5. Verify data integrity
6. Resume services

### Scenario 3: Ransomware Attack

**Estimated Recovery Time:** 4-8 hours

**Steps:**
1. **IMMEDIATELY** disconnect from network
2. Identify compromised systems
3. Restore from offline backups
4. Review and patch security vulnerabilities
5. Rebuild compromised systems
6. Conduct forensic analysis
7. Report to authorities (if PHI involved)

### Scenario 4: Database Server Unavailable

**Estimated Recovery Time:** 10-30 minutes

**Steps:**
1. Activate read replica (automatic failover)
2. Promote read replica to primary
3. Update connection strings
4. Monitor system health
5. Investigate primary server failure
6. Plan for primary server restoration

---

## 📊 Backup Integrity Checks

### Automated Checks

Run daily to verify backup integrity:

```bash
#!/bin/bash
# Automated backup verification script

for backup in $(find /backups/postgres -name "*.dump.gz"); do
    checksum_file="${backup}.sha256"
    if [ -f "$checksum_file" ]; then
        if sha256sum -c "$checksum_file" --quiet; then
            echo "✓ ${backup}"
        else
            echo "✗ ${backup} - CORRUPTED!"
            # Alert system administrator
        fi
    fi
done
```

### Manual Verification

```bash
# Verify specific backup
sha256sum -c /backups/postgres/daily/osun_his_daily_20240115.dump.gz.sha256

# Restore to test database
createdb test_restore
gunzip -c /backups/postgres/daily/latest.dump.gz | \
  pg_restore -d test_restore

# Verify data
psql -d test_restore -c "SELECT COUNT(*) FROM patients;"
```

---

## 🔐 Backup Security

### Encryption at Rest

All backups are encrypted using GPG:

```bash
# Encrypt backup
gpg --symmetric --cipher-algo AES256 backup.dump.gz

# Decrypt when needed
gpg --decrypt backup.dump.gz.enc > backup.dump.gz
```

### Offsite Backup

Weekly backups should be copied to offsite location:

```bash
# Copy to S3 (example)
aws s3 cp \
  /backups/postgres/weekly/latest.dump.gz \
  s3://osun-his-backups/weekly/latest.dump.gz \
  --storage-class GLACIER
```

### Access Control

- Only authorized personnel can access backups
- Backup credentials stored in secure vault (HashiCorp Vault, AWS Secrets Manager)
- Audit logs for all backup/restore operations

---

## 📞 Emergency Contacts

- **System Administrator:** [INSERT PHONE]
- **Database Administrator:** [INSERT PHONE]
- **On-Call Engineer:** [INSERT PHONE]
- **Hospital IT Director:** [INSERT PHONE]

---

## 📋 Pre-Disaster Checklist

- [ ] Verified backup integrity
- [ ] Confirmed backup retention policies
- [ ] Documented all application credentials
- [ ] Tested disaster recovery procedures
- [ ] Contact information updated
- [ ] Runbook reviewed by team

---

## 📈 Recovery Time Objectives (RTO)

- **RTO:** 4 hours
- **RPO:** 24 hours
- **Acceptable Data Loss:** 24 hours

---

## 🔍 Post-Disaster Procedures

After successful recovery:

1. Document incident details
2. Review recovery procedures
3. Identify root cause
4. Implement preventive measures
5. Update runbooks as needed
6. Conduct post-mortem meeting
7. Share lessons learned

---

**Last Updated:** [DATE]  
**Next Review:** [DATE + 3 months]

