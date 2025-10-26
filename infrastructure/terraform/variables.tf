variable "aws_region" {
  description = "AWS region for deployment"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environment name (dev, staging, prod)"
  type        = string
  default     = "prod"
}

variable "db_password" {
  description = "Database master password"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "Database instance class"
  type        = string
  default     = "db.t3.medium"
}

variable "redis_node_type" {
  description = "Redis node type"
  type        = string
  default     = "cache.t3.medium"
}

variable "kafka_instance_type" {
  description = "Kafka broker instance type"
  type        = string
  default     = "kafka.t3.small"
}

variable "allowed_cidrs" {
  description = "Allowed CIDRs for SSH/HTTP access"
  type        = list(string)
  default     = ["0.0.0.0/0"] # Change this to your IP ranges in production
}

variable "facilities" {
  description = "List of healthcare facilities"
  type = list(object({
    id   = string
    name = string
    lga  = string
    type = string
  }))
  default = [
    {
      id   = "FAC001"
      name = "Osun State Teaching Hospital"
      lga  = "Osogbo"
      type = "TERTIARY"
    },
    {
      id   = "FAC002"
      name = "Ejigbo Primary Health Center"
      lga  = "Ejigbo"
      type = "PRIMARY"
    },
    {
      id   = "FAC003"
      name = "Iwo General Hospital"
      lga  = "Iwo"
      type = "SECONDARY"
    }
  ]
}

variable "tags" {
  description = "Common tags for all resources"
  type        = map(string)
  default = {
    Project     = "osun-his"
    ManagedBy   = "terraform"
    Environment = "production"
  }
}

