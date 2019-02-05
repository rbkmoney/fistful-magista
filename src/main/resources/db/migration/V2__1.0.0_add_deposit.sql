-- deposit

CREATE TYPE mst.deposit_event_type AS ENUM (
  'DEPOSIT_CREATED', 'DEPOSIT_STATUS_CHANGED', 'DEPOSIT_TRANSFER_CREATED',
  'DEPOSIT_TRANSFER_STATUS_CHANGED'
);

CREATE TYPE mst.deposit_status AS ENUM ('pending', 'succeeded', 'failed');

CREATE TYPE mst.deposit_transfer_status AS ENUM ('created', 'prepared', 'committed', 'cancelled');

CREATE TABLE mst.deposit (
  id                      BIGSERIAL                   NOT NULL,
  event_id                BIGINT                      NOT NULL,
  event_created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  deposit_id              CHARACTER VARYING           NOT NULL,
  sequence_id             INT                         NOT NULL,
  event_occured_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_type              mst.deposit_event_type      NOT NULL,
  wallet_id               CHARACTER VARYING           NOT NULL,
  source_id               CHARACTER VARYING           NOT NULL,
  amount                  BIGINT                      NOT NULL,
  currency_code           CHARACTER VARYING           NOT NULL,
  deposit_status          mst.deposit_status          NOT NULL,
  deposit_transfer_status mst.deposit_transfer_status,
  fee                     BIGINT,
  provider_fee            BIGINT,
  wtime                   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
  current                 BOOLEAN                     NOT NULL DEFAULT TRUE,
  CONSTRAINT deposit_pkey PRIMARY KEY (id)
);

CREATE INDEX deposit_event_id_idx
  on mst.deposit (event_id);
CREATE INDEX deposit_event_created_at_idx
  on mst.deposit (event_created_at);
CREATE INDEX deposit_id_idx
  on mst.deposit (deposit_id);
CREATE INDEX deposit_event_occured_at_idx
  on mst.deposit (event_occured_at);
CREATE INDEX deposit_wallet_id_idx
  on mst.deposit (wallet_id);
