ALTER TABLE cloud_accounts
    ADD CONSTRAINT uq_cloud_account_tenant_id UNIQUE (id, tenant_id);

ALTER TABLE cloud_resources
    DROP CONSTRAINT cloud_resources_cloud_account_id_fkey,
    ADD CONSTRAINT fk_cloud_resources_account_tenant
        FOREIGN KEY (cloud_account_id, tenant_id)
        REFERENCES cloud_accounts(id, tenant_id);

ALTER TABLE waste_findings
    DROP CONSTRAINT waste_findings_cloud_account_id_fkey,
    ADD CONSTRAINT fk_waste_findings_account_tenant
        FOREIGN KEY (cloud_account_id, tenant_id)
        REFERENCES cloud_accounts(id, tenant_id);
