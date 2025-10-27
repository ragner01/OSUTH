#!/bin/bash
# AWS Deployment Script for Osun HIS

set -e

echo "🚀 Deploying Osun HIS to AWS..."

# Check prerequisites
command -v aws >/dev/null 2>&1 || { echo "AWS CLI not installed. Install it first." >&2; exit 1; }
command -v terraform >/dev/null 2>&1 || { echo "Terraform not installed. Install it first." >&2; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "Docker not installed. Install it first." >&2; exit 1; }

# Configuration
AWS_REGION="${AWS_REGION:-us-east-1}"
ENVIRONMENT="${ENVIRONMENT:-prod}"
TERRAFORM_DIR="infrastructure/terraform"

echo "📍 Region: ${AWS_REGION}"
echo "🌍 Environment: ${ENVIRONMENT}"

# Step 1: Create S3 bucket for Terraform state
echo ""
echo "📦 Step 1: Creating S3 bucket for Terraform state..."
aws s3api create-bucket \
  --bucket osun-his-terraform-state \
  --region ${AWS_REGION} \
  --create-bucket-configuration LocationConstraint=${AWS_REGION} || echo "Bucket may already exist"

# Step 2: Enable versioning
echo ""
echo "🔄 Enabling versioning..."
aws s3api put-bucket-versioning \
  --bucket osun-his-terraform-state \
  --versioning-configuration Status=Enabled

# Step 3: Create DynamoDB table for state locking
echo ""
echo "🔒 Creating DynamoDB table for state locking..."
aws dynamodb create-table \
  --table-name terraform-state-lock \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
  --region ${AWS_REGION} || echo "Table may already exist"

# Step 4: Build and push Docker images
echo ""
echo "🐳 Step 2: Building and pushing Docker images..."
cd "$(dirname "$0")/../.."

# Build images
docker build -t osun-his/gateway:latest -f gateway/Dockerfile .
docker build -t osun-his/core-emr:latest -f core-emr/Dockerfile .
docker build -t osun-his/appointments:latest -f appointments/Dockerfile .
docker build -t osun-his/billing:latest -f billing/Dockerfile .
docker build -t osun-his/pharmacy:latest -f pharmacy/Dockerfile .
docker build -t osun-his/orders-lab-rad:latest -f orders-lab-rad/Dockerfile .
docker build -t osun-his/admin-ui:latest -f admin-ui/Dockerfile .

# Get AWS account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REPO="public.ecr.aws"

# Login to ECR
aws ecr-public get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}

# Push images (you'll need to create repositories first)
echo "Pushing images to ECR..."

docker tag osun-his/gateway:latest ${ECR_REPO}/osun-his/gateway:latest
docker push ${ECR_REPO}/osun-his/gateway:latest

# Step 5: Initialize Terraform
echo ""
echo "🏗️  Step 3: Initializing Terraform..."
cd ${TERRAFORM_DIR}
terraform init

# Step 6: Create terraform.tfvars
echo ""
echo "📝 Creating terraform.tfvars..."
cat > terraform.tfvars <<EOF
aws_region = "${AWS_REGION}"
environment = "${ENVIRONMENT}"
db_password = "${DB_PASSWORD}"  # Set this in environment
allowed_cidrs = ["0.0.0.0/0"]  # Change to your IP ranges
EOF

# Step 7: Plan deployment
echo ""
echo "📋 Step 4: Planning deployment..."
terraform plan -out=tfplan

# Step 8: Apply deployment
echo ""
echo "🚀 Step 5: Applying deployment..."
echo "This will create all AWS resources. Continue? (yes/no)"
read -r confirmation

if [ "$confirmation" == "yes" ]; then
    terraform apply tfplan
    echo ""
    echo "✅ Deployment complete!"
    echo ""
    echo "📊 Outputs:"
    terraform output
else
    echo "Deployment cancelled."
    exit 0
fi

echo ""
echo "🎉 Osun HIS is now deployed to AWS!"
echo ""
echo "🔗 Access your resources:"
echo "   - RDS Endpoint: $(terraform output -raw rds_endpoint)"
echo "   - Load Balancer: $(terraform output -raw load_balancer_dns)"
echo "   - Keycloak: $(terraform output -raw keycloak_url)"
echo "   - Grafana: $(terraform output -raw grafana_url)"

