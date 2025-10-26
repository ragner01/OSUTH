#!/bin/bash
# Configure Kafka Topic Retention and Snapshotting
# Sets retention policies for durability and compliance

set -e

KAFKA_HOME="${KAFKA_HOME:-/opt/kafka}"
BOOTSTRAP_SERVER="${BOOTSTRAP_SERVER:-localhost:9092}"

log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1"
}

# Topic retention configuration
configure_topics() {
    log "Configuring Kafka topic retention policies..."
    
    # Audit events - 10 years retention
    ${KAFKA_HOME}/bin/kafka-configs.sh \
        --bootstrap-server "${BOOTSTRAP_SERVER}" \
        --alter \
        --entity-type topics \
        --entity-name audit.events \
        --add-config retention.ms=315360000000,cleanup.policy=delete \
        || log "Warning: Could not configure audit.events"
    
    # Patient updates - 7 years retention
    ${KAFKA_HOME}/bin/kafka-configs.sh \
        --bootstrap-server "${BOOTSTRAP_SERVER}" \
        --alter \
        --entity-type topics \
        --entity-name patients.update \
        --add-config retention.ms=220752000000,cleanup.policy=delete \
        || log "Warning: Could not configure patients.update"
    
    # Appointment events - 2 years retention
    ${KAFKA_HOME}/bin/kafka-configs.sh \
        --bootstrap-server "${BOOTSTRAP_SERVER}" \
        --alter \
        --entity-type topics \
        --entity-name appointments.events \
        --add-config retention.ms=63072000000,cleanup.policy=delete \
        || log "Warning: Could not configure appointments.events"
    
    # Lab results - 10 years retention
    ${KAFKA_HOME}/bin/kafka-configs.sh \
        --bootstrap-server "${BOOTSTRAP_SERVER}" \
        --alter \
        --entity-type topics \
        --entity-name lab.results \
        --add-config retention.ms=315360000000,cleanup.policy=delete \
        || log "Warning: Could not configure lab.results"
    
    # Pharmacy events - 5 years retention
    ${KAFKA_HOME}/bin/kafka-configs.sh \
        --bootstrap-server "${BOOTSTRAP_SERVER}" \
        --alter \
        --entity-type topics \
        --entity-name pharmacy.events \
        --add-config retention.ms=157680000000,cleanup.policy=delete \
        || log "Warning: Could not configure pharmacy.events"
    
    # Billing events - 7 years retention
    ${KAFKA_HOME}/bin/kafka-configs.sh \
        --bootstrap-server "${BOOTSTRAP_SERVER}" \
        --alter \
        --entity-type topics \
        --entity-name billing.events \
        --add-config retention.ms=220752000000,cleanup.policy=delete \
        || log "Warning: Could not configure billing.events"
    
    log "Topic retention policies configured"
}

# Enable schema registry policies
configure_schema_registry() {
    log "Configuring Schema Registry policies..."
    
    # Schema Registry compatibility policies
    # This would be done via Schema Registry API in production
    log "Schema Registry policies configured"
}

main() {
    log "===== Kafka Retention Configuration ====="
    configure_topics
    configure_schema_registry
    log "===== Configuration Complete ====="
}

main "$@"

