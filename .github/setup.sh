#!/bin/bash
# GitHub Repository Setup Script
# Run this to configure your GitHub repository

echo "🚀 Setting up GitHub repository..."

# Check if GitHub CLI is installed
if ! command -v gh &> /dev/null; then
    echo "⚠️  GitHub CLI not installed. Please install it first:"
    echo "   brew install gh"
    echo "   gh auth login"
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo "⚠️  Please authenticate with GitHub first:"
    echo "   gh auth login"
    exit 1
fi

# Update repository description
echo "📝 Updating repository description..."
gh repo edit ragner01/OSUTH --description "A production-ready hospital information system for Nigeria, featuring multi-facility support, FHIR interoperability, and enterprise-grade security. Built with Spring Boot, React, PostgreSQL, and deployed on AWS."

# Add topics
echo "🏷️  Adding repository topics..."
gh repo edit ragner01/OSUTH --add-topic health-information-system --add-topic spring-boot --add-topic react --add-topic fhir --add-topic java --add-topic typescript --add-topic nigeria --add-topic hospital-management --add-topic healthcare --add-topic microservices

# Enable Dependabot
echo "🔧 Enabling Dependabot..."
gh api repos/ragner01/OSUTH/vulnerability-alerts -X PUT

# Set up branch protection
echo "🛡️  Setting up branch protection..."
gh api repos/ragner01/OSUTH/branches/main/protection \
  --method PUT \
  -f required_status_checks='{"strict":true,"contexts":["ci"]}' \
  -f enforce_admins=true \
  -f required_pull_request_reviews='{"required_approving_review_count":1}' \
  -f restrictions=null \
  -f required_linear_history=false \
  -f allow_force_pushes=false \
  -f allow_deletions=false

# Enable GitHub Actions
echo "⚙️  Enabling GitHub Actions..."
gh api repos/ragner01/OSUTH/actions/permissions \
  -X PUT \
  -f enabled=true \
  -f allowed_actions="all"

echo ""
echo "✅ Repository setup complete!"
echo ""
echo "📋 Summary:"
echo "   - Description updated"
echo "   - Topics added"
echo "   - Dependabot enabled"
echo "   - Branch protection enabled"
echo "   - GitHub Actions enabled"
echo ""
echo "🔗 View your repository:"
echo "   https://github.com/ragner01/OSUTH"
