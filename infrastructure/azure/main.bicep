@description('Name of the resource group')
param resourceGroupName string = 'osuth-his'

@description('Azure region for deployment')
param location string = resourceGroup().location

@description('Environment name (dev, staging, prod)')
@allowed(['dev', 'staging', 'prod'])
param environment string = 'dev'

@description('Container image tag')
param imageTag string = 'latest'

// Variables
var appServicePlanSku = environment == 'prod' ? 'P1V3' : 'B1'
var appServicePlanTier = environment == 'prod' ? 'PremiumV3' : 'Basic'
var appInstanceCount = environment == 'prod' ? 3 : 1

// Use existing Container Apps Environment
// Note: Azure allows only 1 Container App Environment per subscription
// Modify this name to use your existing environment
param existingContainerEnvName string = 'albumapi-java-env-251027140602'
param existingContainerEnvResourceGroup string = 'albumapi-java-rg-251027140602'

resource containerAppsEnv 'Microsoft.App/managedEnvironments@2023-05-01' existing = {
  name: existingContainerEnvName
  scope: resourceGroup(existingContainerEnvResourceGroup)
}

// Log Analytics Workspace
resource logAnalyticsWorkspace 'Microsoft.OperationalInsights/workspaces@2022-10-01' = {
  name: 'osuth-${environment}-logs'
  location: location
  properties: {
    sku: {
      name: 'PerGB2018'
    }
    retentionInDays: 90
  }
}

// Azure Container Registry
resource acr 'Microsoft.ContainerRegistry/registries@2023-01-01-preview' = {
  name: 'osuth${environment}acr'
  location: location
  sku: {
    name: environment == 'prod' ? 'Premium' : 'Standard'
  }
  properties: {
    adminUserEnabled: true
    publicNetworkAccess: 'Enabled'
  }
}

// PostgreSQL Flexible Server
resource postgresServer 'Microsoft.DBforPostgreSQL/flexibleServers@2023-06-01-preview' = {
  name: 'osuth-${environment}-postgres'
  location: location
  sku: {
    name: environment == 'prod' ? 'Standard_D4s_v3' : 'Standard_B1ms'
    tier: environment == 'prod' ? 'GeneralPurpose' : 'Burstable'
  }
  properties: {
    administratorLogin: 'osuthadmin'
    administratorLoginPassword: '@Microsoft.KeyVault(SecretUri=https://osuth-${environment}-kv.vault.azure.net/secrets/postgres-password/)'
    version: '15'
    storage: {
      storageSizeGB: environment == 'prod' ? 256 : 32
    }
    backup: {
      backupRetentionDays: 7
      geoRedundantBackup: 'Enabled'
    }
    highAvailability: {
      mode: environment == 'prod' ? 'ZoneRedundant' : 'Disabled'
    }
  }
}

// Redis Cache
resource redisCache 'Microsoft.Cache/redis@2023-08-01' = {
  name: 'osuth-${environment}-redis'
  location: location
  properties: {
    sku: {
      name: environment == 'prod' ? 'Premium' : 'Standard'
      family: 'C'
      capacity: 1
    }
    enableNonSslPort: false
    minimumTlsVersion: '1.2'
  }
}

// Key Vault
resource keyVault 'Microsoft.KeyVault/vaults@2023-07-01' = {
  name: 'osuth-${environment}-kv'
  location: location
  properties: {
    sku: {
      family: 'A'
      name: 'standard'
    }
    tenantId: subscription().tenantId
    enabledForDeployment: true
    enabledForTemplateDeployment: true
    enabledForDiskEncryption: true
    accessPolicies: []
  }
}

// Application Insights
resource appInsights 'Microsoft.Insights/components@2020-02-02' = {
  name: 'osuth-${environment}-insights'
  location: location
  kind: 'web'
  properties: {
    Application_Type: 'web'
    WorkspaceResourceId: logAnalyticsWorkspace.id
    publicNetworkAccessForIngestion: 'Enabled'
    publicNetworkAccessForQuery: 'Enabled'
  }
}

// Container Apps for each microservice
var services = [
  'identity'
  'core-emr'
  'appointments'
  'orders-lab-rad'
  'pharmacy'
  'billing'
  'staff-rota'
]

resource serviceApp 'Microsoft.App/containerApps@2023-05-01' = [for service in services: {
  name: 'osuth-${environment}-${service}'
  location: location
  properties: {
    managedEnvironmentId: containerAppsEnv.id
    configuration: {
      ingress: {
        external: true
        targetPort: 8080
        transport: 'http'
        allowInsecure: false
      }
      registries: [
        {
          server: acr.properties.loginServer
          identity: ''
        }
      ]
      secrets: [
        {
          name: 'registry-${service}'
          value: acr.listCredentials().passwords[0].value
        }
      ]
    }
    template: {
      containers: [
        {
          name: service
          image: '${acr.properties.loginServer}/osuth-${service}:${imageTag}'
          env: [
            {
              name: 'SPRING_PROFILES_ACTIVE'
              value: environment
            }
            {
              name: 'SPRING_DATASOURCE_URL'
              value: 'jdbc:postgresql://${postgresServer.properties.fullyQualifiedDomainName}:5432/osuth'
            }
            {
              name: 'SPRING_DATASOURCE_USERNAME'
              value: 'osuthadmin'
            }
            {
              name: 'SPRING_DATASOURCE_PASSWORD'
              value: 'ChangeMe123!'
            }
            {
              name: 'SPRING_REDIS_HOST'
              value: redisCache.properties.hostName
            }
            {
              name: 'SPRING_REDIS_PASSWORD'
              value: ''
            }
            {
              name: 'KEYCLOAK_SERVER_URL'
              value: 'http://localhost:8080'
            }
            {
              name: 'APPLICATIONINSIGHTS_CONNECTION_STRING'
              value: appInsights.properties.ConnectionString
            }
          ]
          resources: {
            cpu: json('1.0')
            memory: '2Gi'
          }
        }
      ]
      scale: {
        minReplicas: environment == 'prod' ? 2 : 1
        maxReplicas: environment == 'prod' ? 10 : 3
      }
    }
  }
  identity: {
    type: 'SystemAssigned'
  }
}]

// API Gateway (Container App)
resource apiGateway 'Microsoft.App/containerApps@2023-05-01' = {
  name: 'osuth-${environment}-api-gateway'
  location: location
  properties: {
    managedEnvironmentId: containerAppsEnv.id
    configuration: {
      ingress: {
        external: true
        targetPort: 8080
        transport: 'http'
        allowInsecure: false
        corsPolicy: {
          allowedOrigins: [
            '*'
          ]
          allowedMethods: [
            'GET'
            'POST'
            'PUT'
            'DELETE'
            'OPTIONS'
          ]
        }
      }
    }
    template: {
      containers: [
        {
          name: 'api-gateway'
          image: '${acr.properties.loginServer}/osuth-gateway:${imageTag}'
          env: [
            {
              name: 'SPRING_PROFILES_ACTIVE'
              value: environment
            }
          ]
          resources: {
            cpu: json('1.0')
            memory: '2Gi'
          }
        }
      ]
      scale: {
        minReplicas: environment == 'prod' ? 2 : 1
        maxReplicas: environment == 'prod' ? 5 : 2
      }
    }
  }
}

// Outputs
output containerAppsEnvironmentId string = containerAppsEnv.id
output logAnalyticsWorkspaceId string = logAnalyticsWorkspace.id
output postgresServerFqdn string = postgresServer.properties.fullyQualifiedDomainName
output redisHostName string = redisCache.properties.hostName
output keyVaultName string = keyVault.name
output applicationInsightsConnectionString string = appInsights.properties.ConnectionString
output apiGatewayUrl string = apiGateway.properties.configuration.ingress.fqdn


output serviceAppUrls array = [for i in range(0, length(services)): {
  name: services[i]
  url: 'https://${serviceApp[i].properties.configuration.ingress.fqdn}'
}]
