.PHONY: help up down build test seed lint docs clean

help:
	@echo "Osun State Teaching Hospital HIS - Development Commands"
	@echo ""
	@echo "  make up        - Start all services (Docker Compose)"
	@echo "  make down      - Stop all services"
	@echo "  make build     - Build all modules"
	@echo "  make test      - Run all tests"
	@echo "  make seed      - Seed Keycloak with users and roles"
	@echo "  make lint      - Run static analysis tools"
	@echo "  make docs      - Generate API documentation"
	@echo "  make clean     - Clean build artifacts"

up:
	@echo "Starting infrastructure services..."
	docker-compose up -d postgres-core-emr postgres-appointments postgres-orders \
		postgres-pharmacy postgres-billing redis zookeeper kafka \
		postgres-keycloak keycloak prometheus grafana
	@echo "Waiting for services to be healthy..."
	@sleep 10

down:
	@echo "Stopping all services..."
	docker-compose down -v

build:
	@echo "Building all modules..."
	mvn clean install -DskipTests

test:
	@echo "Running tests..."
	mvn test

seed:
	@echo "Seeding Keycloak..."
	@echo "TODO: Implement Keycloak seeding script"
	@echo "Expected roles: DOCTOR, NURSE, ADMIN, CASHIER, PHARMACIST, LAB_SCIENTIST, RADIOLOGIST, MATRON, HMO_OFFICER"

lint:
	@echo "Running static analysis..."
	mvn spotbugs:check
	mvn checkstyle:check

docs:
	@echo "Generating API documentation..."
	@echo "TODO: Generate OpenAPI docs"

clean:
	@echo "Cleaning build artifacts..."
	mvn clean
	docker-compose down -v

