-- Create contracts table
-- Follows PostgreSQL conventions with JSONB for flexible data storage

CREATE TABLE contracts (
    contract_id VARCHAR(50) PRIMARY KEY,
    purchase_request_id VARCHAR(100) NOT NULL,
    deal_id VARCHAR(100) NOT NULL,
    customer_details JSONB NOT NULL,
    finance_details JSONB NOT NULL,
    mass_orders JSONB NOT NULL,
    pdf_storage_location VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX idx_contracts_purchase_request_id ON contracts(purchase_request_id);
CREATE INDEX idx_contracts_deal_id ON contracts(deal_id);
CREATE INDEX idx_contracts_created_at ON contracts(created_at);

-- Create GIN indexes for JSONB columns for efficient querying
CREATE INDEX idx_contracts_customer_details_gin ON contracts USING GIN (customer_details);
CREATE INDEX idx_contracts_finance_details_gin ON contracts USING GIN (finance_details);
CREATE INDEX idx_contracts_mass_orders_gin ON contracts USING GIN (mass_orders);

-- Add constraints
ALTER TABLE contracts ADD CONSTRAINT uk_contracts_purchase_request_id UNIQUE (purchase_request_id);
ALTER TABLE contracts ADD CONSTRAINT uk_contracts_deal_id UNIQUE (deal_id);

-- Add check constraints
ALTER TABLE contracts ADD CONSTRAINT chk_contract_id_format 
    CHECK (contract_id ~ '^CONTRACT-[A-Z0-9]{8}$');

-- Comments for documentation
COMMENT ON TABLE contracts IS 'Stores contract data and metadata for the Mass Car Ordering platform';
COMMENT ON COLUMN contracts.contract_id IS 'Unique contract identifier in format CONTRACT-XXXXXXXX';
COMMENT ON COLUMN contracts.purchase_request_id IS 'Reference to the originating purchase request';
COMMENT ON COLUMN contracts.deal_id IS 'Reference to the deal that generated this contract';
COMMENT ON COLUMN contracts.customer_details IS 'JSONB storage for complete customer information';
COMMENT ON COLUMN contracts.finance_details IS 'JSONB storage for customer finance details';
COMMENT ON COLUMN contracts.mass_orders IS 'JSONB storage for mass orders array with vehicle configurations';
COMMENT ON COLUMN contracts.pdf_storage_location IS 'File path or S3 URI for the generated PDF contract';
COMMENT ON COLUMN contracts.created_at IS 'Timestamp when the contract was created';
COMMENT ON COLUMN contracts.updated_at IS 'Timestamp when the contract was last updated';
