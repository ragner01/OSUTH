#!/bin/bash
set -e

# Build and Push Docker Images to Azure Container Registry
# Usage: ./build-and-push-images.sh <environment>

ENVIRONMENT=${1:-dev}
ACR_NAME="osuth${ENVIRONMENT}acr"

echo "🏗️  Building and pushing OSUTH images to ACR: $ACR_NAME"
echo ""

# Login to ACR
echo "🔐 Logging in to ACR..."
az acr login --name "$ACR_NAME"

# Get ACR login server
ACR_SERVER=$(az acr show --name "$ACR_NAME" --query loginServer -o tsv)
echo "📦 ACR Server: $ACR_SERVER"
echo ""

# Build parent project first
echo "📦 Building parent project..."
cd ../..
mvn clean install -DskipTests

# Services to build
SERVICES=(
    "identity:ng.osun.his.identity.IdentityServiceApplication"
    "api-gateway:ng.osun.his.gateway.GatewayApplication"
    "core-emr:ng.osun.his.coreemr.CoreEmrApplication"
    "appointments:ng.osun.his.appointments.AppointmentsApplication"
    "orders-lab-rad:ng.osun.his.orderslabrad.OrdersLabRadApplication"
    "pharmacy:ng.osun.his.pharmacy.PharmacyApplication"
    "billing:ng.osun.his.billing.BillingApplication"
    "staff-rota:ng.osun.his.staffrota.StaffRotaApplication"
)

for service_info in "${SERVICES[@]}"; do
    IFS=':' read -r service_name main_class <<< "$service_info"
    
    echo ""
    echo "🏗️  Building $service_name..."
    echo "─────────────────────────────────────────────"
    
    # Build JAR
    cd "$service_name"
    mvn clean package -DskipTests
    
    # Create Dockerfile for this service
    cat > Dockerfile <<EOF
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

COPY target/${service_name}-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
    
    # Build and push image
    IMAGE_TAG="${ACR_SERVER}/osuth-${service_name}:latest"
    
    echo "📦 Building Docker image..."
    docker build -t "$IMAGE_TAG" .
    
    echo "🚀 Pushing to ACR..."
    docker push "$IMAGE_TAG"
    
    echo "✅ $service_name deployed: $IMAGE_TAG"
    
    cd ..
done

echo ""
echo "✅ All images built and pushed successfully!"
echo "📋 Images available at: $ACR_SERVER"
echo ""
echo "📝 Next: Update Container App images to use 'latest' tag"

