# AWS Deployment Guide for Osun HIS

## 🚀 Complete AWS Deployment Instructions

This guide will help you deploy Osun HIS to Amazon Web Services.

---

## 📋 Prerequisites

### 1. Install Required Tools

```bash
# AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Terraform
brew install terraform  # macOS
# or
wget https://releases.hashicorp.com/terraform/1.6.0/terraform_1.6.0_linux_amd64.zip
unzip terraform_1.6.0_linux_amd64.zip
sudo mv terraform /usr/local/bin/

# Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
```

### 2. Configure AWS Credentials

```bash
aws configure
# Enter your Access Key ID
# Enter your Secret Access Key
# Set default region: us-east-1
# Set output format: json

# Verify
aws sts get-caller-identity
```

### 3. Set Environment Variables

```bash
export AWS_REGION="us-east-1"
export AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
export DB_PASSWORD="your-secure-password"  # Change this!
```

---

## 🚀 Deployment Steps

### Step 1: Create Prerequisites

```bash
cd /path/to/OSUTH

# Create S3 bucket for Terraform state
aws s3 mb s3://osun-his-terraform-state --region us-east-1

# Enable versioning
aws s3api put-bucket-versioning \
  --bucket osun-his-terraform-state \
  --versioning-configuration Status=Enabled

# Create DynamoDB table for locking
aws dynamodb create-table \
  --table-name terraform-state-lock \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5
```

### Step 2: Build Docker Images

```bash
cd /path/to/OSUTH

# Build images for each service
docker build -t osun-his/gateway:latest .
docker build -t osun-his/core-emr:latest .
# ... repeat for all services
```

### Step 3: Initialize Terraform

```bash
cd infrastructure/terraform

# Initialize Terraform
terraform init

# Create terraform.tfvars
cat > terraform.tfvars <<EOF
aws_region = "us-east-1"
environment = "prod"
db_password = "your-secure-password"
allowed_cidrs = ["YOUR_IP/32"]
EOF
```

### Step 4: Plan Deployment

```bash
# Review the plan
terraform plan
```

### Step 5: Deploy

```bash
# Apply the deployment
terraform apply

# Enter "yes" when prompted
```

**This will create:**
- ✅ VPC with subnets
- ✅ RDS PostgreSQL (Multi-AZ + 2 read replicas)
- ✅ ElastiCache Redis
- ✅ MSK Kafka cluster
- ✅ ECS Cluster with Fargate
- ✅ Application Load Balancer
- ✅ Keycloak instance
- ✅ Monitoring stack (Prometheus + Grafana)
- ✅ Security groups
- ✅ CloudWatch logging

---

## 📊 Post-Deployment

### Access Your Resources

```bash
# Get outputs
terraform output

# Load Balancer URL
export LB_URL=$(terraform output -raw load_balancer_dns)
echo "API Gateway: http://${LB_URL}"

# Keycloak
export KEYCLOAK_URL=$(terraform output -raw keycloak_url)
echo "Keycloak: ${KEYCLOAK_URL}"

# Grafana
export GRAFANA_URL=$(terraform output -raw grafana_url)
echo "Grafana: ${GRAFANA_URL}"
```

### Configure DNS

```bash
# Get Load Balancer DNS
aws elbv2 describe-load-balancers --names osun-his-alb

# Update your DNS records:
# api.osun-his.ng -> Load Balancer DNS
# admin.osun-his.ng -> CloudFront distribution
```

### Set Up GitHub Secrets

Add these to your GitHub repository:
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION`
- `DATABASE_URL` (from Terraform output)
- `KEYCLOAK_URL` (from Terraform output)

---

## 🔍 Verification

### 1. Check ECS Services

```bash
aws ecs list-services --cluster osun-his-cluster
```

### 2. Check Database

```bash
# Connect to RDS
export DB_HOST=$(terraform output -raw rds_endpoint)
psql -h ${DB_HOST} -U postgres -d osun_his
```

### 3. Check Load Balancer

```bash
curl http://${LB_URL}/actuator/health
```

### 4. Check Keycloak

```bash
curl ${KEYCLOAK_URL}/health
```

### 5. Check Monitoring

```bash
curl ${GRAFANA_URL}:3000
curl http://${PROMETHEUS_IP}:9090
```

---

## 🛠️ Troubleshooting

### ECS Tasks Not Starting

```bash
# Check logs
aws logs tail /ecs/osun-his --follow

# Check task status
aws ecs describe-tasks --cluster osun-his-cluster --tasks <task-id>
```

### Database Connection Issues

```bash
# Check security group
aws ec2 describe-security-groups --group-ids sg-xxxxx

# Check RDS status
aws rds describe-db-instances --db-instance-identifier osun-his-postgres
```

### Load Balancer Health Checks Failing

```bash
# Check target health
aws elbv2 describe-target-health --target-group-arn <arn>
```

---

## 💰 Cost Estimation

**Estimated Monthly Costs:**

- RDS Multi-AZ: ~$200/month
- ElastiCache Redis: ~$50/month
- MSK Kafka: ~$100/month
- EC2 Instances: ~$150/month (Keycloak, Monitoring)
- ECS Fargate: ~$100/month
- Application Load Balancer: ~$20/month
- Data Transfer: ~$50/month

**Total: ~$670/month**

*Costs vary by region and usage*

---

## 🔐 Security Best Practices

1. **Restrict IP Ranges**
   - Update `allowed_cidrs` in terraform.tfvars
   - Only allow your organization's IPs

2. **Enable WAF**
   ```bash
   aws wafv2 create-web-acl --name osun-his-waf ...
   ```

3. **Use AWS Secrets Manager**
   - Store passwords securely
   - Rotate credentials regularly

4. **Enable CloudTrail**
   - Audit all API calls
   - Monitor for suspicious activity

5. **Enable GuardDuty**
   - Threat detection
   - Security monitoring

---

## 📈 Scaling

### Horizontal Scaling

```bash
# Update ECS service to 5 tasks
aws ecs update-service \
  --cluster osun-his-cluster \
  --service osun-his-app \
  --desired-count 5
```

### Vertical Scaling

```bash
# Upgrade RDS instance
aws rds modify-db-instance \
  --db-instance-identifier osun-his-postgres \
  --db-instance-class db.t3.large
```

### Auto Scaling

Auto-scaling is configured via ECS service:

```hcl
resource "aws_appautoscaling_target" "ecs_target" {
  max_capacity       = 10
  min_capacity       = 2
  resource_id        = "service/${aws_ecs_cluster.main.name}/${aws_ecs_service.app.name}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"
}
```

---

## 🗑️ Cleanup

To remove all resources:

```bash
cd infrastructure/terraform
terraform destroy

# Confirm with "yes"
```

**Warning:** This will delete ALL resources including data!

---

## 📞 Support

For issues or questions:
- GitHub Issues: https://github.com/ragner01/OSUTH/issues
- AWS Support: https://console.aws.amazon.com/support
- Documentation: See `/docs/` directory

---

**Your Osun HIS is now running on AWS!** 🎉

