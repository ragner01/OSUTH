terraform {
  required_version = ">= 1.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  
  backend "s3" {
    bucket = "osun-his-terraform-state"  # Create this bucket first
    key    = "prod/terraform.tfstate"
    region = "us-east-1"
    encrypt = true
    
    dynamodb_table = "terraform-state-lock"  # Create this table for state locking
  }
}

provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = var.tags
  }
}

