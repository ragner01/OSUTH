#!/bin/bash
set -e

# Azure Deployment Script for OSUTH HIS
# Usage: ./deploy.sh <environment> <subscription-id>

ENVIRONMENT=${1:-dev}
SUBSCRIPTION_ID=${2}

if [ -z "$SUBSCRIPTION_ID" ]; then
    echo "❌ Error: Subscription ID required"
    echo "Usage: ./deploy.sh <environment> <subscription-id>"
    exit 1
fi

echo "🏥 Deploying Osun State Teaching Hospital HIS to Azure"
echo "Environment: $ENVIRONMENT"
echo "Subscription: $SUBSCRIPTION_ID"
echo ""

# Check if Azure CLI is installed
if ! command -v az &> /dev/null; then
    echo "❌ Azure CLI not installed. Please install from: https://aka.ms/InstallAzureCLI"
    exit 1
fi

# Login to Azure
echo "🔐 Logging in to Azure..."
az login

# Set subscription
echo "📋 Setting subscription..."
az account set --subscription "$SUBSCRIPTION_ID"

# Get current user
CURRENT_USER=$(az account show --query user.name -o tsv)
echo "👤 Deploying as: $CURRENT_USER"

# Create resource group
RESOURCE_GROUP="osuth-his-${ENVIRONMENT}-rg"
LOCATION="eastus"

echo "📦 Creating resource group: $RESOURCE_GROUP"
az group create \
    --name "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --tags Environment="$ENVIRONMENT" Project="OSUTH"

# Grant permissions (optional if already Owner)
echo "🔑 Granting required permissions..."
az role assignment create \
    --role "Owner" \
    --assignee "$CURRENT_USER" \
    --scope "/subscriptions/$SUBSCRIPTION_ID" \
    --output none 2>/dev/null || echo "⚠️  Permission grant skipped (already Owner or permission denied)"

echo "⏳ Waiting for permissions to propagate (10s)..."
sleep 10

# Deploy infrastructure
echo "🏗️  Deploying infrastructure with Bicep..."
az deployment group create \
    --resource-group "$RESOURCE_GROUP" \
    --template-file main.bicep \
    --parameters environment="$ENVIRONMENT" \
    --output table

# Get outputs
echo ""
echo "📋 Deployment Outputs:"
az deployment group show \
    --resource-group "$RESOURCE_GROUP" \
    --name "main" \
    --query "properties.outputs" \
    --output table

echo ""
echo "✅ Infrastructure deployed successfully!"
echo "📝 Next steps:"
echo "   1. Build and push Docker images to ACR"
echo "   2. Update container app images"
echo "   3. Configure Key Vault secrets"
echo ""
echo "📖 See docs/AZURE_DEPLOYMENT.md for details"

