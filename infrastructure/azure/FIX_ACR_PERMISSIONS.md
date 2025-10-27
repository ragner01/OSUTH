# Fixing Azure ACR Permissions Error

## Problem

When deploying to Azure, you may encounter this error:

```
ACR Tasks requests for the front albumapijavaacr251027140602 and d5ba43c0-ee4f-412d-87e1-f047878f6bbe are not permitted.
```

This is an Azure permissions issue with the Azure Container Registry (ACR).

## Solutions

### Option 1: Portal (Easiest) ✅

1. **Go to Azure Portal**
   - Navigate to https://portal.azure.com
   - Login with your account

2. **Navigate to Subscriptions**
   - Search for "Subscriptions" in the top search bar
   - Click on your subscription (the one with ID `d5ba43c0-ee4f-412d-87e1-f047878f6bbe`)

3. **Access Control (IAM)**
   - Click "Access control (IAM)" from the left sidebar
   - Click "+ Add" → "Add role assignment"

4. **Add Role Assignment**
   - Role: Select "Owner" or "Contributor"
   - Assign access to: "User, group, or service principal"
   - Select: Your email address
   - Click "Save"

5. **Wait for Propagation**
   - Wait 5-10 minutes for permissions to propagate

6. **Retry Deployment**
   - Go back to your deployment
   - Click "Retry" button

### Option 2: Azure CLI (Faster)

Run this command in your terminal:

```bash
# Login to Azure
az login

# Grant Owner role to yourself on the subscription
az role assignment create \
  --role "Owner" \
  --assignee $(az account show --query user.name -o tsv) \
  --scope /subscriptions-facing-10-ee4f-412d-87e1-f047878f6bbe

# Wait for propagation
echo "Waiting 30 seconds for permissions to propagate..."
sleep 30

# Verify the assignment
az role assignment list \
  --scope /subscriptions/d5ba43c0-ee4f-412d-87e1-f047878f6bbe \
  --query "[?roleDefinitionName=='Owner']"
```

Then retry your deployment.

### Option 3: Request Additional Access

If you don't have permission to grant yourself roles:

1. **Contact Subscription Administrator**
   - Ask them to grant you "Owner" or "Contributor" role
   - Provide them this document

2. **File Azure Support Request**
   - Go to https://aka.ms/azuresupport
   - Explain you need ACR permissions for deployment
   - Include your subscription ID: `d5ba43c0-ee4f-412d-87e1-f047878f6bbe`

### Option 4: Use Different Account

If you have access to another Azure account:

1. Login with the account that has proper permissions
2. Retry the deployment
3. Or create a Service Principal with proper roles

## Verify Permissions

Check your current roles on the subscription:

```bash
az role assignment list \
  --scope /subscriptionsروا-ee4f-412d-87e1-f047878f6bbe \
  --assignee $(az account show --query user.name -o tsv) \
  --output table
```

You should see roles like:
- Owner
- Contributor
- User Access Administrator

## Common Roles for ACR Deployment

- **Owner** - Full access (recommended)
- **Contributor** - Can deploy everything except permissions
- **AcrPush** - Can push images to ACR
- **AcrTaskRun** - Can run ACR tasks

## For OSUTH HIS Deployment

Once permissions are fixed:

```bash
# Deploy OSUTH to Azure
cd infrastructure/azure
./deploy.sh dev <subscription-id>

# Build and push images
./build-and-push-images.sh dev
```

## Still Having Issues?

1. Check Azure Status: https://status.azure.com
2. Clear browser cache and cookies
3. Try incognito/private browsing
4. Use a different browser
5. Contact Azure Support

## Prevention

To avoid this in the future:

1. Ensure proper roles are assigned before starting deployment
2. Use a dedicated Service Principal for deployments
3. Store credentials securely in Azure Key Vault
4. Document access requirements upfront

