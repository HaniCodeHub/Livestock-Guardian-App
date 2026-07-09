# Backup & Disaster Recovery

## Backup Strategy

### Data Classification

**Critical Data** (RPO: 1 hour, RTO: 1 hour)
- User accounts and authentication
- Animal registrations
- Ownership records
- Muzzle biometrics

**Important Data** (RPO: 24 hours, RTO: 4 hours)
- User photos/images
- Health records
- Identification history
- Audit logs

**Non-critical Data** (RPO: 7 days, RTO: 24 hours)
- Cached data
- Analytics
- Logs older than 90 days

### Backup Methods

#### Database Backups
```bash
# Full backup
pg_dump -U postgres livestock_guardian > full_backup.sql

# Incremental backup (WAL archiving)
wal_level = replica
archive_mode = on
archive_command = 'cp %p /backups/wal/%f'

# Automated daily backups
0 2 * * * pg_dump -U postgres livestock_guardian | gzip > /backups/db_$(date +\%Y\%m\%d).sql.gz
```

#### File Backups
```bash
# Backup images directory
rsync -avz --delete /data/images/ backup:/data/images_backup/

# Backup configuration files
tar -czf config_backup.tar.gz /etc/livestock-guardian/
```

#### Cloud Backups
```bash
# Backup to AWS S3
aws s3 sync /data/images/ s3://livestock-guardian-backups/images/

# Backup to GCS
gsutil -m cp -r /data/images/* gs://livestock-guardian-backups/images/
```

### Backup Schedule

| Data Type | Frequency | Retention | Location |
|-----------|-----------|-----------|----------|
| Database | Hourly | 7 days | Local + Cloud |
| Images | Daily | 30 days | Cloud + CDN |
| Config | Daily | 1 year | Cloud |
| Logs | Weekly | 90 days | Log Storage |
| Full System | Weekly | 3 months | Cold Storage |

---

## Recovery Procedures

### Database Recovery

#### Point-in-Time Recovery
```bash
# Recovery to specific timestamp
pg_basebackup -h localhost -U postgres -D /pg_backup

# PITR from WAL archives
recovery_target_timeline = 'latest'
recovery_target_time = '2024-01-15 10:00:00'
```

#### Full Database Restore
```bash
# Stop database
sudo systemctl stop postgresql

# Restore from backup
psql -U postgres < full_backup.sql

# Start database
sudo systemctl start postgresql

# Verify restore
psql -U postgres -c "SELECT COUNT(*) FROM animals;"
```

### Application Recovery

#### Failover to Backup Server
```bash
# DNS update to backup server IP
dig livestock-guardian-api.com

# Verify service health
curl -I https://livestock-guardian-api.com/health

# Redirect users via load balancer configuration
```

#### Restore from Cloud Backup
```bash
# Download from cloud
aws s3 cp s3://livestock-guardian-backups/db_20240115.sql.gz .
gunzip db_20240115.sql.gz

# Restore database
psql -U postgres < db_20240115.sql
```

---

## Disaster Recovery Plan

### Recovery Time Objectives (RTO)

| Scenario | RTO | RTA (Recovery Time Allowance) |
|----------|-----|------|
| Single server failure | 30 min | 15 min |
| Database corruption | 1 hour | 30 min |
| Data center outage | 4 hours | 2 hours |
| Regional outage | 24 hours | 12 hours |

### Recovery Point Objectives (RPO)

| Scenario | RPO |
|----------|-----|
| Transaction data | 1 hour |
| User files | 4 hours |
| Logs | 24 hours |
| Analytics | 7 days |

### Disaster Scenarios

#### Scenario 1: Database Corruption
```
Detection: Automated alerts on data integrity checks
Response:
1. Isolate corrupted database
2. Restore from hourly backup
3. Replay transaction logs to PITR
4. Verify data integrity
5. Resume service
Timeline: 45 minutes
```

#### Scenario 2: Data Center Outage
```
Detection: Health checks fail for all services
Response:
1. Trigger failover to secondary data center
2. Update DNS records (5 min TTL)
3. Restore latest database snapshot
4. Verify all services operational
5. Notify users
Timeline: 2 hours
```

#### Scenario 3: Ransomware Attack
```
Detection: Unusual file modifications detected
Response:
1. Isolate affected systems immediately
2. Activate incident response team
3. Preserve forensic evidence
4. Restore from clean backups (before attack)
5. Rebuild systems from scratch
6. Reset all credentials
Timeline: 24 hours
```

---

## Testing & Validation

### Backup Verification
```bash
# Verify backup integrity
pg_dump -t animals livestock_guardian | head -20

# Test restore in sandbox
psql -d test_restore < backup.sql
psql -d test_restore -c "SELECT COUNT(*) FROM animals;"

# Check backup file size and timestamp
ls -lah /backups/
```

### Regular Disaster Recovery Drills

**Quarterly DR Drills**:
```
Month: Jan, Apr, Jul, Oct
Duration: 2 hours
Scope: Full system recovery test
Participants: DevOps, DBA, Support
Success Criteria:
- RTO met
- Data integrity verified
- Service functionality validated
- Communication plan executed
```

### Documentation

**Maintain**:
- Recovery runbooks
- Contact lists
- System architecture diagrams
- Credential locations (secure vault)
- Step-by-step procedures

---

## Monitoring & Alerting

### Backup Monitoring
```python
# Alert on backup failure
import monitoring

def check_backup_status():
    latest_backup = get_latest_backup()
    age_hours = (now() - latest_backup.timestamp).hours
    
    if age_hours > 2:
        send_alert("CRITICAL: No backup in 2 hours")
    elif age_hours > 1:
        send_alert("WARNING: No backup in 1 hour")
```

### Alerts

| Alert | Threshold | Action |
|-------|-----------|--------|
| Backup failed | N/A | Page on-call engineer |
| Backup old | > 2 hours | Email team |
| Storage full | > 90% | Scale storage |
| Restore test failed | Any | Review process |

---

## Off-site Backup Strategy

### Backup Locations
```
Primary: Local data center (RTO: 1 hour)
Secondary: AWS Region (RTO: 4 hours)
Tertiary: GCS Region (RTO: 24 hours)
```

### Replication Setup
```python
# Continuous replication to AWS
import boto3
s3 = boto3.client('s3')

def replicate_to_aws(file_path):
    s3.upload_file(
        file_path,
        'livestock-guardian-backups',
        os.path.basename(file_path)
    )
```

### Cost Optimization
- Use lifecycle policies to move old backups to cold storage
- Delete backups older than retention period
- Monitor storage costs monthly

---

## Compliance & Auditing

### Backup Compliance
- [x] GDPR: Encryption, retention policies
- [x] HIPAA: Audit logs, access controls (if health data)
- [x] SOC 2: Annual audits, testing
- [x] ISO 27001: Information security

### Audit Trail
```sql
-- Log all backup activities
CREATE TABLE backup_audit_log (
    id UUID PRIMARY KEY,
    backup_type VARCHAR(50),
    status VARCHAR(20),
    timestamp TIMESTAMP,
    size_bytes BIGINT,
    duration_seconds INT,
    verified_by VARCHAR(100),
    location VARCHAR(255)
);
```

---

## Backup Checklist

Before deploying to production:

- [ ] Daily backups configured and tested
- [ ] Hourly backups for critical data
- [ ] Cloud replication enabled
- [ ] Recovery procedures documented
- [ ] RTO/RPO requirements met
- [ ] Backup monitoring alerts configured
- [ ] Quarterly DR drills scheduled
- [ ] Compliance requirements addressed
- [ ] Off-site backups tested
- [ ] Backup encryption enabled
- [ ] Access control to backups
- [ ] Cost monitoring configured
