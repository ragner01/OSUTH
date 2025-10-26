# Incident Response Runbook

## Quick Reference

### Emergency Contacts
- On-call: +234 XXX XXX XXXX
- Security team: security@osun-his.ng
- Database admin: dba@osun-his.ng

### Escalation Path
1. **P0 (Critical)**: All services down → On-call immediately
2. **P1 (High)**: Single service failing → On-call within 15 min
3. **P2 (Medium)**: Performance degradation → Triage within 1 hour
4. **P3 (Low)**: Non-impacting issues → Next business day

---

## P0 - All Services Down

### Symptoms
- 503 errors from all endpoints
- Prometheus unable to scrape
- Database unreachable

### Immediate Actions
1. Check Docker containers: `docker ps`
2. Check logs: `docker logs <service>`
3. Restart services: `make restart`
4. Check network: `docker network ls`
5. If data corruption suspected:
   - Stop all services
   - Check PostgreSQL: `pg_isready`
   - Review replication lag

### Resolution
```bash
# Restart all services
make down && make up

# Verify health
curl http://localhost:8080/actuator/health
```

### Root Cause Examples
- Memory exhaustion
- Database connection pool exhaustion
- Network partition
- Disk full

---

## P1 - Single Service Failing

### Symptoms
- One module returning 5xx errors
- Timeouts for specific endpoints
- Degraded response times

### Immediate Actions
1. Check service health: `curl http://localhost:8080/actuator/health/{service}`
2. Review logs: `docker logs {service} --tail 100`
3. Check metrics for that service
4. Check dependencies (DB, Redis, Kafka)

### Service-Specific Checks

#### Core-EMR Down
- Check: `psql -c "SELECT count(*) FROM patients;"`
- Look for: Table lock, missing FK indexes
- Restart: `cd core-emr && mvn spring-boot:run`

#### Appointments Down
- Check: Queue service, clinic schedules
- Validate: Provider availability
- Restart: `cd appointments && mvn spring-boot:run`

#### Billing Down
- Check: Price book tables
- Validate: Payment processing
- Impact: No payments can be processed

---

## P2 - Performance Degradation

### Symptoms
- p95 latency > 500ms
- Error rate > 1%
- Throughput dropping
- Queue building up

### Immediate Actions
1. Check golden dashboard in Grafana
2. Identify slow endpoints
3. Check JVM metrics (GC, memory)
4. Review database query performance
5. Check Kafka lag

### Common Causes
- N+1 queries
- Missing database indexes
- Memory pressure
- GC overhead
- External service degradation

### Resolution
```bash
# Add index for slow query
psql -d osun_his -c "CREATE INDEX CONCURRENTLY idx_patients_created ON patients(created_at);"

# Enable JVM flight recorder
jcmd <pid> JFR.start filename=/tmp/recording.jfr

# Scale horizontally
docker-compose scale core-emr=2
```

---

## P3 - Non-Critical Issues

### Symptoms
- Intermittent errors
- Minor UI glitches
- Non-blocking feature issues

### Actions
- Log issue in backlog
- Assign to sprint
- Monitor trends

---

## Security Incidents

### Unauthorized Access
1. **Immediate**: Revoke access tokens
2. **Investigate**: Check audit logs
3. **Notify**: Security team
4. **Document**: Incident report

### Data Breach
1. **Stop**: All affected services
2. **Isolate**: Compromised systems
3. **Assess**: Scope of breach
4. **Notify**: Compliance officer
5. **Mitigate**: Patch vulnerabilities
6. **Document**: Complete incident report

### PHI Audit
```bash
# Query audit logs for suspicious access
psql -d osun_his -c "
  SELECT user_id, resource_type, resource_id, action_type, event_timestamp
  FROM audit_events
  WHERE event_timestamp > NOW() - INTERVAL '1 hour'
    AND resource_type = 'Patient'
    AND user_role != 'DOCTOR'
  ORDER BY event_timestamp DESC;
"
```

---

## Tabletop Exercise Scenarios

### Scenario 1: Database Corruption
**Simulation**: Patient table has orphaned records  
**Detection**: Foreign key violations  
**Response**: 
1. Identify affected rows
2. Run data integrity checks
3. Restore from backup if needed
4. Update data model if necessary

### Scenario 2: DDoS Attack
**Simulation**: 10,000 req/sec hitting gateway  
**Detection**: Rate limit triggers, 429 responses  
**Response**:
1. Enable WAF rules
2. Block attacker IPs
3. Scale up gateway instances
4. Monitor for ongoing attack

### Scenario 3: Lab Results Lost
**Simulation**: Specimens collected but results not showing  
**Detection**: Empty results for active orders  
**Response**:
1. Check Kafka message lag
2. Verify result entry workflow
3. Manually re-enter results
4. Investigate order service

---

## Prevention & Monitoring

### Daily Checks
- [ ] All services healthy
- [ ] No critical alerts
- [ ] p95 latency < 500ms
- [ ] Error rate < 0.1%
- [ ] Disk space > 20%

### Weekly Reviews
- [ ] SLO compliance report
- [ ] Audit log review
- [ ] Capacity planning
- [ ] Security scan results
- [ ] Backend test coverage

### Monthly Tasks
- [ ] Disaster recovery drill
- [ ] Capacity analysis
- [ ] Security audit
- [ ] Compliance review
- [ ] Team training

---

## Recovery Procedures

### Database Recovery
```bash
# Restore from backup
pg_restore -d osun_his_restore /backups/osun_his_$(date +%Y%m%d).dump

# Verify integrity
psql -d osun_his -c "SELECT pg_check_consistency();"
```

### Service Rollback
```bash
# Git revert
git revert <commit>

# Rebuild
mvn clean install -DskipTests

# Redeploy
docker-compose up -d
```

---

## Post-Incident

### 1. Immediate (0-1 hour)
- [ ] Document timeline
- [ ] Capture logs
- [ ] Take screenshots
- [ ] Save metrics snapshots

### 2. Short-term (1-24 hours)
- [ ] Write incident report
- [ ] Root cause analysis
- [ ] Stakeholder notification
- [ ] Post-mortem scheduled

### 3. Long-term (1 week)
- [ ] Post-mortem meeting
- [ ] Action items tracked
- [ ] Process improvements
- [ ] Knowledge base updated

---

## Appendix: Useful Commands

### Check Service Health
```bash
curl http://localhost:8081/actuator/health
```

### View Metrics
```bash
curl http://localhost:8081/actuator/prometheus
```

### Check Kafka
```bash
docker exec -it kafka kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

### Database Queries
```bash
# Active patients
psql -d osun_his -c "SELECT count(*) FROM patients WHERE deleted = false AND active = true;"

# Recent audit events
psql -d osun_his -c "SELECT * FROM audit_events ORDER BY event_timestamp DESC LIMIT 100;"

# Slow queries
psql -d osun_his -c "SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;"
```

### Restart Services
```bash
make restart           # All services
docker-compose restart core-emr    # Single service
```

### View Logs
```bash
docker logs -f core-emr
docker logs -f gateway
docker logs -f postgres
```

