# AWS Quick Start Guide

## 🚀 Deploy Osun HIS to AWS in 10 minutes

### Prerequisites

1. **Install Tools:**
   ```bash
   # AWS CLI
   curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
   
   # Terraform
   brew install terraform  # or download from hashicorp.com
   ```

2. **Configure AWS:**
   ```bash
   aws configure
   # Enter your Access Key ID
   # Enter your Secret Access Key
   # Region: us-east-1
   # Format: json
   ```

3. **Set Environment:**
   ```bash
   export AWS_REGION="us-east-1"
   export DB_PASSWORD="ChangeThisSecurePassword123!"
   ```

### Deploy!

```bash
cd /Users/omotolaalimi/Desktop/OSUTH/scripts/aws
chmod +x deploy.sh
./deploy.sh
```

That's it! The script will:
- ✅ Create S3 bucket for state
- ✅ Create DynamoDB for locking
- ✅ Build Docker images
- ✅ Initialize Terraform
- ✅ Deploy all resources

**Wait ~30 minutes** for deployment to complete.

### Get Your URLs

```bash
cd infrastructure/terraform
terraform output
```

You'll see:
- Load Balancer URL (your API)
- Keycloak URL
- Grafana URL
- RDS endpoint

### Access Your Application

- **API Gateway:** http://your-load-balancer-url
- **Admin Console:** Deploy to CloudFront (see docs)
- **Keycloak:** http://keycloak-url/auth
- **Grafana:** http://grafana-url:3000

---

## 📖 Full Documentation

See: [docs/AWS_DEPLOYMENT.md](docs/AWS_DEPLOYMENT.md)

## 💰 Costs

~$670/month for complete infrastructure

## 🔐 Security

All resources are encrypted and secure!

---

**That's it! Your Osun HIS is running on AWS!** 🎉
