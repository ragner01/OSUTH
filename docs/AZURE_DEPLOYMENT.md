# Azure Deployment Guide for OSUTH HIS

This guide covers deploying the Osun State Teaching Hospital HIS to Azure using Azure Container Apps.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Quick Start](#quick-start)
3. [Infrastructure Overview](#infrastructure-overview)
4. [Step-by-Step Deployment](#step-by-step-deployment)
5. [Post-Deployment Configuration](#post-deployment-configuration)
6. [Troubleshooting](#troubleshooting)

## Prerequisites

### Required Tools

- [Azure CLI](https://aka.ms/InstallAzureCLI) (v2.50.0+)
- Docker Desktop (for building images locally)
- Maven 3.8+
- Java 11

### Azure Subscription

- Active Azure subscription
- Owner or Contributor role on the subscription
- Sufficient quota for:
  - Container Apps
  - PostgreSQL Flexible Server
  - Redis Cache
  - Azure Container Registry

## Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/ragner01/OSUTH.git
cd OSUTH

# 2. Login to Azure
az login

# 3. Set your subscription
az account set --subscription <subscription-id>

# 4. Deploy infrastructure
cd infrastructure/azure
chmod +x deploy.sh
./deploy.sh dev <subscription-id>

# 5. Build and push images
chmod +x build-and-push-images.sh
./build-and-push-images.sh dev

# 6. Configure secrets in Key Vault
# (See Post-Deployment Configuration section)

# 7. Restart container apps to pull new images
az containerapp update --name osuth-dev-core-emr --resource-group osuth-his-dev-rg
```

## Infrastructure Overview

### Components

1. **Azure Container Apps** - Hosts microservices
2. **Azure Container Registry (ACR)** - Stores Docker images
3. **PostgreSQL Flexible Server** - Primary database
4. **Redis Cache** - Caching and session store
5. **Azure Key Vault** - Secrets management
6. **Application Insights** - Monitoring and logging
7. **Log Analytics** - Centralized logging

### Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Azure Cloud                           │
│                                                          │
│  ┌────────────────────────────────────────────────────┐ │
│  │          Container Apps Environment                │ │
│  │                                                    │ │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐         │ │
│  │  │   API    │  │  Core    │  │  Lab     │  ...   │ │
│  │  │ Gateway  │  │   EMR    │  │ Orders   │         │ │
│  │  └────┬─────┘  └────┬─────┘  └────┬─────┘         │ │
│  │       │             │             │                │ │
│  └───────┼─────────────┼─────────────┼────────────────┘ │
│          │             │             │                   │
│  ┌───────▼─────────────▼─────────────▼───────────────┐ │
│  │          PostgreSQL Flexible Server               │ │
│  │          Redis Cache                              │ │
│  │          Key Vault                                │ │
│  └───────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## Step-by-Step Deployment

### 1. Infrastructure Deployment

```bash
cd infrastructure/azure

# Deploy infrastructure
./deploy.sh dev <subscription-id>
```

This creates:
- Resource group: `osuth-his-dev-rg`
- Container Apps environment
- PostgreSQL database
- Redis cache
- Azure Container Registry
- Key Vault
- Application Insights

### 2. Build and Push Images

```bash
# Build all microservice images and push to ACR
./build-and-push-images.sh dev
```

This will:
1. Build each microservice JAR
2. Create Dockerfile
3. Build Docker image
4. Push to Azure Container Registry

### 3. Configure Secrets

Store sensitive configuration in Key Vault:

```bash
# Get Key Vault name
KV_NAME=$(az deployment group show \
    --resource-group osuth-his-dev-rg \
    --name main \
    --query "properties.outputs.keyVaultName.value" -o tsv)

# Store PostgreSQL password
az keyvault secret set \
    --vault-name "$KV_NAME" \
    --name "postgres-password" \
    --value "YourSecurePassword123!"

# Store database username
az keyvault secret set \
    --vault-name "$KV_NAME" \
    --name "db-username" \
    --value "osuthadmin"

# Store Keycloak URL
az keyvault secret set \
    --vault-name "$KV_NAME" \
    --name "keycloak-url" \
    --value "https://your-keycloak-instance.com"

# Store Redis password
REDIS_KEY=$(az redis list-keys \
    --resource-group osuth-his-dev-rg \
    --name osuth-dev-redis \
    --query primaryKey -o tsv)
    
az keyvault secret set \
    --vault-name "$KV_NAME" \
    --name "redis-password" \
    --value "$REDIS_KEY"
```

### 4. Update Container Apps

Configure secrets in each Container App:

```bash
RESOURCE_GROUP="osuth-his-dev-rg"

# Update each service
for service in core-emr appointments pharmacy billing; do
    az containerapp update \
        --name "osuth-dev-$service" \
        --resource-group "$RESOURCE_GROUP" \
        --set-env-vars \
            "SPRING_DATASOURCE_PASSWORD=@microsoft.keyvault(secrets/postgres-password)" \
            "SPRING_REDIS_PASSWORD=@microsoft.keyvault(secrets/redis-password)"
done
```

### 5. Run Database Migrations

Initialize the database schema:

```bash
# Get PostgreSQL connection details
PG_HOST=$(az postgres flexible-server show \
    --resource-group osuth-his-dev-rg \
    --name osuth-dev-postgres \
    --query fullyQualifiedDomainName -o tsv)

# Run migrations via kubectl exec or manual SQL
az postgres flexible-server db create \
    --resource-group osuth-his-dev-rg \
    --server-name osuth-dev-postgres \
    --database-name osuth
```

## Post-Deployment Configuration

### Environment Variables

Configure these for each Container App:

```bash
# Core EMR
az containerapp update \
    --name osuth-dev-core-emr \
    --resource-group osuth-his-dev-rg \
    --set-env-vars \
        "SPRING_PROFILES_ACTIVE=dev" \
        "SPRING_DATASOURCE_URL=jdbc:postgresql://osuth-dev-postgres.postgres.database.azure.com:5432/osuth"
```

### Health Checks

Verify all services are running:

```bash
# Get service URLs
az containerapp show \
    --name osuth-dev-core-emr \
    --resource-group osuth-his-dev-rg \
    --query "properties.configuration.ingress.fqdn" -o tsv

# Test health endpoint
curl https://<fqdn>/actuator/health
```

### Monitoring

View logs in Application Insights:

```bash
# Open Application Insights in browser
az monitor app-insights show \
    --resource-group osuth-his-dev-rg \
    --app osuth-dev-insights \
    --query appId -o tsv | xargs -I {} open "https://portal.azure.com/#resource/subscriptions/{subscription-id}/resourceGroups/osuth-his-dev-rg/providers/Microsoft.Insights/components/{}/logs"
```

## Troubleshooting

### Container App Not Starting

```bash
# View logs
az containerapp logs show \
    --name osuth-dev-core-emr \
    --resource-group osuth-his-dev-rg \
    --follow

# Check replica status
az containerapp revision list \
    --name osuth-dev-core-emr \
    --resource-group osuth-his-dev-rg
```

### ACR Push Errors

```bash
# Login to ACR again
az acr login --name osuthdevacr

# Check ACR credentials
az acr credential show --name osuthdevacr
```

### Database Connection Issues

```bash
# Check firewall rules
az postgres flexible-server firewall-rule list \
    --resource-group osuth-his-dev-rg \
    --name osuth-dev-postgres

# Allow Azure services
az postgres flexible-server firewall-rule create \
    --resource-group os beloved-his-dev-rg \
    --name osuth-dev-postgres \
    --rule-name AllowAzureServices \
    --start-ip-address 0.0.0.0 \
    --end-ip-address 0.0.0.0
```

### Permission Errors

If you see "ACR Tasks requests are not permitted":

```bash
# Add yourself as Owner on subscription
az role assignment create \
    --role "Owner" \
    --assignee $(az account show --query user.name -o tsv) \
    --scope /subscriptions/<subscription-id>

# Wait 5-10 minutes and retry
```

## Cost Management

### Estimated Monthly Costs (Dev)

- Container Apps (Basic): ~$50
- PostgreSQL (Burstable): ~$30
- Redis (Standard): ~$20
- ACR (Standard): ~$5
- Key Vault: ~$1
- Log Analytics: ~$10
- Application Insights: ~$5

**Total: ~$120/month**

### Cost Optimization

1. Stop Container Apps when not in use
2. Use burstable PostgreSQL for dev/staging
3. Delete unused resources
4. Enable auto-shutdown
5. Use spot instances for non-production

## Cleanup

To delete all resources:

```bash
# Delete resource group (removes everything)
az group delete --name osuth-his-dev-rg --yes --no-wait
```

## Support

For issues or questions:
- Azure Support: https://aka.ms/azuresupport
- GitHub Issues: https://github.com/ragner01/OSUTH/issues
- Documentation: See /docs folder

