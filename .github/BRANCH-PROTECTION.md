# Branch Protection Setup

## Manual Setup via GitHub Web Interface

Since automated setup requires GitHub CLI, here's how to do it manually:

### 1. Go to Branch Protection Settings
Visit: https://github.com/ragner01/OSUTH/settings/branches

### 2. Configure main Branch Protection

Click "Add rule" and configure:

**Rule name:** `main`

**Protect matching branches:**

✅ **Required pull request reviews before merging**
- Require approvals: `1`
- Dismiss stale pull request approvals when new commits are pushed: ✅
- Require review from Code Owners: Optional

✅ **Require status checks to pass before merging**
- Pending/degraded checks apply: ✅
- Required status checks: Add `ci` check

✅ **Require branches to be up to date before merging**: ✅

✅ **Require conversation resolution before merging**: ✅

✅ **Do not allow bypassing the above settings**: ✅

✅ **Do not allow force pushes**: ✅

✅ **Do not allow deletions**: ✅

✅ **Require linear history**: Optional

✅ **Include administrators**: ✅

Click "Create" to save the rule.

---

## Setting Up GitHub Secrets

Visit: https://github.com/ragner01/OSUTH/settings/secrets/actions

Click "New repository secret" and add:

### Required Secrets

1. **DATABASE_URL**
   ```
   postgresql://postgres:password@localhost:5432/osun_his
   ```

2. **KEYCLOAK_URL**
   ```
   http://localhost:8080/auth
   ```

3. **AWS_ACCESS_KEY_ID** (if deploying to AWS)
   ```
   Your AWS access key
   ```

4. **AWS_SECRET_ACCESS_KEY** (if deploying to AWS)
   ```
   Your AWS secret key
   ```

5. **AWS_REGION** (if deploying to AWS)
   ```
   us-east-1
   ```

---

## Enable Dependabot

Dependabot is already configured in `.github/dependabot.yml`.

It will:
- ✅ Monitor Maven dependencies (Java)
- ✅ Monitor npm dependencies (React)
- ✅ Create PRs automatically for updates
- ✅ Run weekly

No additional setup needed!

---

## Enable Discussions

Visit: https://github.com/ragner01/OSUTH/settings

Scroll to "Features" section:
- ✅ Enable Discussions
- ✅ Enable Projects

---

## Adding Repository Description

### Option 1: Via Web Interface
1. Go to https://github.com/ragner01/OSUTH
2. Click the gear icon (⚙️) next to "About"
3. Click "Edit repository details"
4. Paste the description from `REPOSITORY_DESCRIPTION.md`
5. Click "Save changes"

### Option 2: Via GitHub CLI
```bash
gh repo edit ragner01/OSUTH --description "A production-ready hospital information system for Nigeria, featuring multi-facility support, FHIR interoperability, and enterprise-grade security. Built with Spring Boot, React, PostgreSQL, and deployed on AWS."
```

---

## Adding Topics

### Option 1: Via Web Interface
1. Go to https://github.com/ragner01/OSUTH
2. Click the gear icon (⚙️) next to "About"
3. Click "Edit repository details"
4. In "Topics" field, add:
   ```
   health-information-system hospital-management healthcare spring-boot react typescript java fhir microservices nigeria
   ```
5. Click "Save changes"

### Option 2: Via GitHub CLI
```bash
gh repo edit ragner01/OSUTH --add-topic health-information-system --add-topic spring-boot --add-topic react --add-topic fhir --add-topic java --add-topic typescript --add-topic nigeria --add-topic hospital-management --add-topic healthcare --add-topic microservices
```

---

## Setting Up GitHub Pages (Optional)

If you want to host documentation:

1. Go to: https://github.com/ragner01/OSUTH/settings/pages
2. Source: Deploy from a branch
3. Branch: `main` / `docs`
4. Folder: `/docs`
5. Click "Save"

---

## Code Scanning (Security)

1. Go to: https://github.com/ragner01/OSUTH/security
2. Enable "Code scanning"
3. Click "Set up this workflow"
4. Commit the generated workflow

---

**All done! Your repository is fully configured!** 🎉

