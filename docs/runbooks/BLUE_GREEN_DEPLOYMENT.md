# Blue/Green Deployment Runbook

## Overview

This runbook describes the process for performing blue/green deployments of Osun HIS with zero-downtime.

---

## 🎯 Architecture

### Blue Environment (Production)
- Current live environment
- Active traffic
- Stable version

### Green Environment (Staging)
- New version to deploy
- Isolated from production
- Identical infrastructure

---

## 📋 Pre-Deployment Checklist

- [ ] All tests passing (unit, integration, E2E)
- [ ] Code review approved
- [ ] Security scan passed
- [ ] Performance benchmarks met
- [ ] Database migrations tested
- [ ] Rollback plan documented
- [ ] Stakeholders notified

---

## 🚀 Deployment Steps

### 1. Prepare Green Environment

```bash
# Checkout release branch
git checkout release/v1.2.0

# Build artifacts
mvn clean package -DskipTests

# Tag release
git tag -a v1.2.0 -m "Release v1.2.0"
git push origin v1.2.0
```

### 2. Deploy to Green

```bash
# Start Green environment
docker-compose -f docker-compose.green.yml up -d

# Wait for health checks
curl http://green.osun-his.ng:8081/actuator/health

# Verify metrics
curl http://green.osun-his.ng:8081/actuator/metrics
```

### 3. Run Smoke Tests

```bash
# Run smoke test suite
cd test/integration
./run-smoke-tests.sh --environment green

# Verify critical paths
- [ ] User login
- [ ] Patient search
- [ ] Appointment booking
- [ ] Lab results
- [ ] Pharmacy dispensing
- [ ] Billing checkout
```

### 4. Database Migration

```bash
# Backup current production database
./scripts/backup/postgres-backup.sh daily

# Run migrations in Green
cd green-environment
flyway migrate

# Verify migrations
psql -h green-db.osun-his.ng -U postgres -d osun_his -c "SELECT * FROM flyway_schema_history;"
```

### 5. Switch Traffic (Gradual Rollout)

**Option A: Canary Deployment (10% → 50% → 100%)**

```bash
# 10% traffic to Green
./scripts/traffic-split.sh --green 10

# Monitor for 15 minutes
watch curl http://green.osun-his.ng:8081/actuator/health

# Increase to 50%
./scripts/traffic-split.sh --green 50

# Monitor for 15 minutes

# Increase to 100% (full cutover)
./scripts/traffic-split.sh --green 100
```

**Option B: All-at-Once Cutover**

```bash
# Update DNS to point to Green
./scripts/switch-to-green.sh

# Monitor for issues
watch ./scripts/health-check.sh
```

### 6. Post-Deployment Verification

```bash
# Verify application metrics
curl http://green.osun-his.ng:8081/actuator/metrics

# Check error rates
curl http://green.osun-his.ng:8081/actuator/metrics/errors.total

# Verify API responses
curl -H "Authorization: Bearer $TOKEN" \
  http://green.osun-his.ng:8081/api/patients

# Monitor for 30 minutes
```

---

## 🔙 Rollback Procedures

### Automatic Rollback Triggers

- Error rate > 5%
- Response time > 2s (p95)
- Failed health checks
- Database connection errors
- Memory/CPU spikes

### Manual Rollback

```bash
# Immediate rollback to Blue
./scripts/rollback-to-blue.sh

# Verify Blue is receiving traffic
curl http://blue.osun-his.ng:8081/actuator/health

# Monitor for stability
watch ./scripts/health-check.sh
```

### Database Rollback

```bash
# If migrations caused issues
flyway repair

# Or restore from backup
./scripts/backup/restore-database.sh daily 1
```

---

## 🔍 Monitoring During Deployment

### Key Metrics to Watch

1. **Error Rate**
   ```bash
   watch -n 5 'curl -s http://metrics.osun-his.ng/api/v1/query?query=rate(errors_total[5m])'
   ```

2. **Response Time**
   ```bash
   watch -n 5 'curl -s http://metrics.osun-his.ng/api/v1/query?query=histogram_quantile(0.95,http_request_duration_seconds_bucket)'
   ```

3. **Active Connections**
   ```bash
   watch -n 5 'curl -s http://green.osun-his.ng:8081/actuator/metrics/executor.active'
   ```

4. **Database Connections**
   ```bash
   watch -n 5 'psql -h green-db.osun-his.ng -U postgres -d osun_his -c "SELECT count(*) FROM pg_stat_activity;"'
   ```

### Grafana Dashboards

Monitor these dashboards during deployment:
- **System Overview:** CPU, Memory, Disk
- **Application Health:** Error rates, Latency
- **Database Performance:** Connections, Query time
- **Business Metrics:** Patients, Appointments, Revenue

---

## 📊 Deployment Scripts

### `scripts/traffic-split.sh`

```bash
#!/bin/bash
# Traffic split management

set -e

GREEN_PERCENT=${1:-10}

# Update load balancer config
sed -i "s/split_percentage.*/split_percentage ${GREEN_PERCENT}/" /etc/nginx/upstream.conf

# Reload nginx
nginx -s reload

echo "Traffic split updated: ${GREEN_PERCENT}% to Green"
```

### `scripts/switch-to-green.sh`

```bash
#!/bin/bash
# Switch all traffic to Green

set -e

log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

log "Switching all traffic to Green..."

# Update DNS
# This is environment-specific
update_dns_to_green

# Update load balancer
sed -i "s/server blue.app.internal/server green.app.internal/g" /etc/nginx/upstream.conf
nginx -s reload

log "All traffic now routing to Green"
log "Monitoring for 5 minutes..."

# Monitor
for i in {1..60}; do
    if ! curl -f http://green.osun-his.ng:8081/actuator/health > /dev/null 2>&1; then
        log "ERROR: Green environment unhealthy!"
        ./scripts/rollback-to-blue.sh
        exit 1
    fi
    sleep 5
done

log "Deployment successful!"
```

---

## 🔐 Security Considerations

### Secrets Management

- Rotate secrets before Green deployment
- Verify secrets synchronization
- Test secret decryption in Green

### Network Security

- Verify firewall rules in Green
- Test VPN connectivity
- Verify TLS certificates

### Data Privacy

- Confirm PHI encryption in transit
- Verify audit logs
- Test data retention policies

---

## 📞 Escalation Procedures

### Deployment Issues

1. **Level 1:** Automatic rollback (30s detection)
2. **Level 2:** On-call engineer notified (1 min)
3. **Level 3:** DevOps lead alerted (5 min)
4. **Level 4:** IT Director involved (15 min)

### Emergency Contacts

- **On-Call Engineer:** [PHONE]
- **DevOps Lead:** [PHONE]
- **IT Director:** [PHONE]

---

## 📋 Post-Deployment Activities

### Immediate (First Hour)

- [ ] Monitor error rates
- [ ] Verify critical user flows
- [ ] Check database performance
- [ ] Review audit logs

### Short-term (First Day)

- [ ] Analyze performance metrics
- [ ] Review user feedback
- [ ] Check cost impacts
- [ ] Update documentation

### Long-term (First Week)

- [ ] Conduct post-mortem
- [ ] Document lessons learned
- [ ] Update deployment procedures
- [ ] Plan next release

---

**Last Updated:** [DATE]  
**Next Review:** [DATE + 1 month]

