# Setting Up AWS for Osun HIS

## 📥 Installing AWS CLI on macOS

### Option 1: Homebrew (Recommended)

```bash
# Install Homebrew if you don't have it
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install AWS CLI
brew install awscli

# Verify
aws --version
```

### Option 2: Official Installer

```bash
# Download installer
curl "https://awscli.amazonaws.com/AWSCLIV2.pkg" -o "AWSCLIV2.pkg"

# Install
sudo installer -pkg AWSCLIV2.pkg -target /

# Verify
aws --version
```

---

## 🔑 Setting Up AWS Credentials

### Step 1: Get AWS Access Keys

1. Go to [AWS Console](https://console.aws.amazon.com)
2. Sign in to your account
3. Go to **IAM** → **Users** → **Add users**
4. User name: `osun-his-deployer`
5. Access type: Select "Programmatic access"
6. Attach permissions: **PowerUserAccess** (or AdministratorAccess for testing)
7. Click **Create user**
8. **IMPORTANT**: Copy and save:
   - Access Key ID
   - Secret Access Key
   - (You won't see the secret again!)

### Step 2: Configure AWS CLI

```bash
aws configure
```

You'll be prompted for:

```
AWS Access Key ID [None]: YOUR_ACCESS_KEY_ID
AWS Secret Access Key [None]: YOUR_SECRET_ACCESS_KEY
Default region name [None]: us-east-1
Default output format [None]: json
```

### Step 3: Verify Configuration

```bash
# Test your credentials
aws sts get-caller-identity
```

You should see your AWS account details.

---

## 🚀 Installing Other Prerequisites

### Terraform

```bash
# Install via Homebrew
brew install terraform

# Or download manually
wget https://releases.hashicorp.com/terraform/1.6.0/terraform_1.6.0_darwin_amd64.zip
unzip terraform_1.6.0_darwin_amd64.zip
sudo mv terraform /usr/local/bin/

# Verify
terraform version
```

### Docker

```bash
# Download Docker Desktop for Mac
# Visit: https://www.docker.com/products/docker-desktop
# Or install via Homebrew
brew install --cask docker

# Verify
docker --version
```

---

## ✅ Final Verification

Run these commands to verify everything is installed:

```bash
# Check AWS CLI
aws --version

# Check Terraform
terraform version

# Check Docker
docker --version
```

All should show version numbers.

---

## 🎯 Ready to Deploy!

Once everything is installed:

```bash
cd /Users/omotolaalimi/Desktop/OSUTH/scripts/aws

# Set your database password
export DB_PASSWORD="YourSecurePassword123!"

# Run the deployment
./deploy.sh
```

That's it! The script will:
- Create all necessary AWS resources
- Build Docker images
- Deploy your application
- Show you the access URLs

---

## 💰 AWS Free Tier

If you're new to AWS, you get:

- ✅ 750 hours/month of EC2
- ✅ 20 GB database storage
- ✅ 750 hours of RDS
- ✅ 5 GB S3 storage

This covers small deployments!

---

## 📞 Need Help?

- AWS Documentation: https://docs.aws.amazon.com
- AWS Support: https://console.aws.amazon.com/support
- GitHub Issues: https://github.com/ragner01/OSUTH/issues

---

**Ready to deploy!** 🚀

