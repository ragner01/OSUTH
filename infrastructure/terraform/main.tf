terraform {
  required_version = ">= 1.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
  
  backend "s3" {
    bucket = "osun-his-terraform-state"
    key    = "prod/terraform.tfstate"
    region = "us-east-1"
  }
}

# Provider configuration
provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Project     = "osun-his"
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}

provider "docker" {
  host = "unix:///var/run/docker.sock"
}

# Variables
variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "production"
}

variable "facilities" {
  description = "List of healthcare facilities"
  type = list(object({
    id   = string
    name = string
    lga  = string
  }))
  default = [
    {
      id   = "FAC001"
      name = "Osun State Teaching Hospital"
      lga  = "Osogbo"
    },
    {
      id   = "FAC002"
      name = "Ejigbo Primary Health Center"
      lga  = "Ejigbo"
    },
  ]
}

# VPC
resource "aws_vpc" "main" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames  = true
  enable_dns_support    = true
  
  tags = {
    Name = "osun-his-vpc"
  }
}

# Subnets
resource "aws_subnet" "private" {
  count = 2
  
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.${count.index + 1}.0/24"
  availability_zone = data.aws_availability_zones.available.names[count.index]
  
  tags = {
    Name = "osun-his-private-subnet-${count.index + 1}"
    Type = "private"
  }
}

resource "aws_subnet" "public" {
  count = 2
  
  vpc_id            = aws_vpc.main.id
  cidr_block        = "10.0.${count.index + 10}.0/24"
  availability_zone = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true
  
  tags = {
    Name = "osun-his-public-subnet-${count.index + 1}"
    Type = "public"
  }
}

# RDS Instance for PostgreSQL
resource "aws_db_instance" "postgres" {
  identifier     = "osun-his-postgres"
  engine         = "postgres"
  engine_version = "16"
  instance_class = "db.t3.medium"
  
  allocated_storage       = 100
  max_allocated_storage   = 500
  storage_encrypted       = true
  storage_type            = "gp3"
  
  db_name  = "osun_his"
  username = "postgres"
  password = var.db_password
  
  vpc_security_group_ids      = [aws_security_group.rds.id]
  db_subnet_group_name         = aws_db_subnet_group.main.name
  publicly_accessible           = false
  backup_retention_period       = 30
  backup_window                 = "03:00-04:00"
  maintenance_window            = "mon:04:00-mon:05:00"
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]
  
  # Automated backups
  automated_backups_enabled    = true
  skip_final_snapshot         = false
  final_snapshot_identifier    = "osun-his-final-snapshot-${formatdate("YYYY-MM-DD-hhmm", timestamp())}"
  deletion_protection          = true
  
  # Multi-AZ for high availability
  multi_az = true
  
  tags = {
    Name = "osun-his-postgres"
  }
}

# RDS Read Replicas
resource "aws_db_instance" "postgres_replica" {
  count = 2
  
  identifier     = "osun-his-postgres-replica-${count.index + 1}"
  replicate_source_db = aws_db_instance.postgres.identifier
  
  instance_class = "db.t3.medium"
  publicly_accessible = false
  
  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name
  
  tags = {
    Name = "osun-his-postgres-replica-${count.index + 1}"
    Type = "read-replica"
  }
}

# RDS Subnet Group
resource "aws_db_subnet_group" "main" {
  name       = "osun-his-db-subnet-group"
  subnet_ids = aws_subnet.private[*].id
  
  tags = {
    Name = "osun-his-db-subnet-group"
  }
}

# Security Groups
resource "aws_security_group" "rds" {
  name        = "osun-his-rds-sg"
  description = "Security group for RDS PostgreSQL"
  vpc_id      = aws_vpc.main.id
  
  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.main.cidr_block]
    description = "PostgreSQL from VPC"
  }
  
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = {
    Name = "osun-his-rds-sg"
  }
}

# Elasticache for Redis
resource "aws_elasticache_subnet_group" "main" {
  name       = "osun-his-cache-subnet"
  subnet_ids = aws_subnet.private[*].id
}

resource "aws_elasticache_cluster" "redis" {
  cluster_id           = "osun-his-redis"
  engine               = "redis"
  node_type            = "cache.t3.medium"
  num_cache_nodes      = 1
  parameter_group_name = "default.redis7"
  port                 = 6379
  subnet_group_name    = aws_elasticache_subnet_group.main.name
  security_group_ids    = [aws_security_group.redis.id]
  
  tags = {
    Name = "osun-his-redis"
  }
}

resource "aws_security_group" "redis" {
  name        = "osun-his-redis-sg"
  description = "Security group for Redis"
  vpc_id      = aws_vpc.main.id
  
  ingress {
    from_port   = 6379
    to_port     = 6379
    protocol    = "tcp"
    cidr_blocks = [aws_vpc.main.cidr_block]
    description = "Redis from VPC"
  }
  
  tags = {
    Name = "osun-his-redis-sg"
  }
}

# MSK for Kafka
resource "aws_msk_cluster" "kafka" {
  cluster_name           = "osun-his-kafka"
  kafka_version          = "3.6.0"
  number_of_broker_nodes = 3
  
  broker_node_group_info {
    instance_type  = "kafka.t3.small"
    client_subnets = aws_subnet.private[*].id
    storage_info {
      ebs_storage_info {
        provisioned_throughput {
          enabled           = true
          volume_throughput = 500
        }
        volume_size = 100
      }
    }
  }
  
  encryption_info {
    encryption_at_rest_kms_key_id = aws_kms_key.kafka.key_id
  }
  
  logging_info {
    broker_logs {
      cloudwatch_logs {
        enabled   = true
        log_group = aws_cloudwatch_log_group.kafka.name
      }
    }
  }
  
  tags = {
    Name = "osun-his-kafka"
  }
}

resource "aws_kms_key" "kafka" {
  description = "KMS key for Kafka encryption"
  
  tags = {
    Name = "osun-his-kafka-kms"
  }
}

resource "aws_cloudwatch_log_group" "kafka" {
  name              = "/aws/msk/osun-his-kafka"
  retention_in_days = 7
}

# Data sources
data "aws_availability_zones" "available" {
  state = "available"
}

# Outputs
output "vpc_id" {
  value = aws_vpc.main.id
}

output "rds_endpoint" {
  value = aws_db_instance.postgres.endpoint
}

output "rds_read_replicas" {
  value = [for replica in aws_db_instance.postgres_replica : replica.endpoint]
}

output "redis_endpoint" {
  value = aws_elasticache_cluster.redis.cache_nodes[0].address
}

output "kafka_bootstrap_brokers" {
  value = aws_msk_cluster.kafka.bootstrap_brokers
}

