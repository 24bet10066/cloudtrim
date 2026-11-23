CREATE TABLE tenants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL CHECK (role IN ('ADMIN', 'VIEWER')),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, email)
);

CREATE INDEX idx_users_tenant_id ON users(tenant_id);

CREATE TABLE cloud_accounts (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    provider VARCHAR(32) NOT NULL,
    external_account_id VARCHAR(255) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    credentials_encrypted TEXT,
    last_scan_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, provider, external_account_id)
);

CREATE INDEX idx_cloud_accounts_tenant_id ON cloud_accounts(tenant_id);

CREATE TABLE cloud_resources (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    cloud_account_id UUID NOT NULL REFERENCES cloud_accounts(id),
    provider_resource_id VARCHAR(255) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    region VARCHAR(100),
    environment VARCHAR(64),
    status VARCHAR(32) NOT NULL,
    monthly_cost_usd NUMERIC(12, 2) NOT NULL DEFAULT 0,
    tags JSONB NOT NULL DEFAULT '{}'::jsonb,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    discovered_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_seen_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, cloud_account_id, provider_resource_id)
);

CREATE INDEX idx_cloud_resources_tenant_account
    ON cloud_resources(tenant_id, cloud_account_id);
CREATE INDEX idx_cloud_resources_tenant_type
    ON cloud_resources(tenant_id, resource_type);

CREATE TABLE waste_findings (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    cloud_account_id UUID NOT NULL REFERENCES cloud_accounts(id),
    cloud_resource_id UUID REFERENCES cloud_resources(id),
    rule_code VARCHAR(64) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN'
        CHECK (status IN ('OPEN', 'RESOLVED', 'IGNORED')),
    severity VARCHAR(32) NOT NULL,
    monthly_waste_usd NUMERIC(12, 2) NOT NULL,
    annual_waste_usd NUMERIC(12, 2) NOT NULL,
    detected_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_open_waste_finding
    ON waste_findings(tenant_id, cloud_account_id, cloud_resource_id, rule_code)
    WHERE status = 'OPEN';
CREATE INDEX idx_waste_findings_tenant_status
    ON waste_findings(tenant_id, status);

CREATE TABLE savings_snapshots (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    snapshot_date DATE NOT NULL,
    total_monthly_spend_usd NUMERIC(12, 2) NOT NULL DEFAULT 0,
    detected_monthly_waste_usd NUMERIC(12, 2) NOT NULL DEFAULT 0,
    realized_savings_usd NUMERIC(12, 2) NOT NULL DEFAULT 0,
    potential_savings_usd NUMERIC(12, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (tenant_id, snapshot_date)
);

CREATE INDEX idx_savings_snapshots_tenant_date
    ON savings_snapshots(tenant_id, snapshot_date DESC);
