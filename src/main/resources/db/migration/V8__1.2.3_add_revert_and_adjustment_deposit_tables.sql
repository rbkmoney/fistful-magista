CREATE TYPE mst.deposit_adjustment_data_status AS ENUM ('pending', 'succeeded');
CREATE TYPE mst.deposit_adjustment_data_event_type AS ENUM (
    'DEPOSIT_ADJUSTMENT_CREATED', 'DEPOSIT_ADJUSTMENT_STATUS_CHANGED', 'DEPOSIT_ADJUSTMENT_TRANSFER_CREATED',
    'DEPOSIT_ADJUSTMENT_TRANSFER_STATUS_CHANGED'
    );

CREATE TABLE mst.deposit_adjustment_data
(
    id               BIGSERIAL                                      NOT NULL,
    event_id         BIGINT                                         NOT NULL,
    event_created_at TIMESTAMP WITHOUT TIME ZONE                    NOT NULL,
    event_occured_at TIMESTAMP WITHOUT TIME ZONE                    NOT NULL,
    event_type       mst.deposit_adjustment_data_event_type         NOT NULL,
    source_id        CHARACTER VARYING                              NOT NULL,
    wallet_id        CHARACTER VARYING                              NOT NULL,
    deposit_id       CHARACTER VARYING                              NOT NULL,
    adjustment_id    CHARACTER VARYING                              NOT NULL,
    amount           BIGINT,
    fee              BIGINT,
    provider_fee     BIGINT,
    currency_code    CHARACTER VARYING,
    status           mst.deposit_adjustment_data_status             NOT NULL,
    transfer_status  mst.deposit_transfer_status,
    deposit_status   mst.deposit_status,
    external_id      CHARACTER VARYING,
    party_id         UUID,
    identity_id      CHARACTER VARYING,
    party_revision   BIGINT                                         NOT NULL,
    domain_revision  BIGINT                                         NOT NULL,
    wtime            TIMESTAMP DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT deposit_adjustment_data_pkey PRIMARY KEY (id)
);

CREATE INDEX deposit_adjustment_data_event_id_idx
    ON mst.deposit_adjustment_data (event_id);
CREATE INDEX deposit_adjustment_data_event_created_at_idx
    ON mst.deposit_adjustment_data (event_created_at);
CREATE INDEX deposit_adjustment_data_event_occured_at_idx
    ON mst.deposit_adjustment_data (event_occured_at);
CREATE INDEX deposit_adjustment_data_wallet_id_idx
    ON mst.deposit_adjustment_data (wallet_id);
CREATE INDEX deposit_adjustment_data_deposit_id_idx
    ON mst.deposit_adjustment_data (deposit_id);
CREATE INDEX deposit_adjustment_data_adjustment_id_idx
    ON mst.deposit_adjustment_data (adjustment_id);
CREATE INDEX deposit_adjustment_data_party_id_idx
    ON mst.deposit_adjustment_data (party_id);
CREATE INDEX deposit_adjustment_data_identity_id_idx
    ON mst.deposit_adjustment_data (identity_id);
ALTER TABLE mst.deposit_adjustment_data
    ADD CONSTRAINT deposit_adjustment_data_uniq UNIQUE (deposit_id, adjustment_id);

CREATE TYPE mst.deposit_revert_data_status AS ENUM ('pending', 'succeeded', 'failed');
CREATE TYPE mst.deposit_revert_data_event_type AS ENUM (
    'DEPOSIT_REVERT_CREATED', 'DEPOSIT_REVERT_STATUS_CHANGED', 'DEPOSIT_REVERT_TRANSFER_CREATED',
    'DEPOSIT_REVERT_TRANSFER_STATUS_CHANGED'
    );

CREATE TABLE mst.deposit_revert_data
(
    id               BIGSERIAL                                      NOT NULL,
    event_id         BIGINT                                         NOT NULL,
    event_created_at TIMESTAMP WITHOUT TIME ZONE                    NOT NULL,
    event_occured_at TIMESTAMP WITHOUT TIME ZONE                    NOT NULL,
    event_type       mst.deposit_revert_data_event_type             NOT NULL,
    source_id        CHARACTER VARYING                              NOT NULL,
    wallet_id        CHARACTER VARYING                              NOT NULL,
    deposit_id       CHARACTER VARYING                              NOT NULL,
    revert_id        CHARACTER VARYING                              NOT NULL,
    amount           BIGINT                                         NOT NULL,
    fee              BIGINT,
    provider_fee     BIGINT,
    currency_code    CHARACTER VARYING                              NOT NULL,
    status           mst.deposit_revert_data_status                 NOT NULL,
    transfer_status  mst.deposit_transfer_status,
    reason           CHARACTER VARYING,
    external_id      CHARACTER VARYING,
    party_id         UUID,
    identity_id      CHARACTER VARYING,
    party_revision   BIGINT                                         NOT NULL,
    domain_revision  BIGINT                                         NOT NULL,
    wtime            TIMESTAMP DEFAULT timezone('utc'::text, now()) NOT NULL,
    CONSTRAINT deposit_revert_data_pkey PRIMARY KEY (id)
);

CREATE INDEX deposit_revert_data_event_id_idx
    ON mst.deposit_revert_data (event_id);
CREATE INDEX deposit_revert_data_event_created_at_idx
    ON mst.deposit_revert_data (event_created_at);
CREATE INDEX deposit_revert_data_event_occured_at_idx
    ON mst.deposit_revert_data (event_occured_at);
CREATE INDEX deposit_revert_data_wallet_id_idx
    ON mst.deposit_revert_data (wallet_id);
CREATE INDEX deposit_revert_data_deposit_id_idx
    ON mst.deposit_revert_data (deposit_id);
CREATE INDEX deposit_revert_data_revert_id_idx
    ON mst.deposit_revert_data (revert_id);
CREATE INDEX deposit_revert_data_party_id_idx
    ON mst.deposit_revert_data (party_id);
CREATE INDEX deposit_revert_data_identity_id_idx
    ON mst.deposit_revert_data (identity_id);
ALTER TABLE mst.deposit_revert_data
    ADD CONSTRAINT deposit_revert_data_uniq UNIQUE (deposit_id, revert_id);
