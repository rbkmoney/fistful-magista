CREATE SCHEMA IF NOT EXISTS mst;

CREATE TABLE mst.identity_data (
  id                   BIGSERIAL         NOT NULL,
  party_id             UUID              NOT NULL,
  party_contract_id    CHARACTER VARYING,
  identity_id          CHARACTER VARYING NOT NULL,
  identity_provider_id CHARACTER VARYING NOT NULL,
  identity_class_id    CHARACTER VARYING NOT NULL,
  CONSTRAINT identity_data_pkey PRIMARY KEY (id),
  CONSTRAINT identity_data_ukey UNIQUE (identity_id)
);

CREATE TYPE mst.identity_event_type AS ENUM ('IDENTITY_CREATED', 'IDENTITY_LEVEL_CHANGED', 'IDENTITY_CHALLENGE_CREATED', 'IDENTITY_CHALLENGE_STATUS_CHANGED', 'IDENTITY_EFFECTIVE_CHALLENGE_CHANGED');

CREATE TABLE mst.identity_event (
  id                             BIGSERIAL                   NOT NULL,
  event_id                       BIGINT                      NOT NULL,
  event_type                     mst.identity_event_type     NOT NULL,
  event_created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  identity_id                    CHARACTER VARYING           NOT NULL,
  sequence_id                    INT                         NOT NULL,
  identity_effective_chalenge_id CHARACTER VARYING,
  identity_level_id              CHARACTER VARYING,
  CONSTRAINT identity_event_pkey PRIMARY KEY (id),
  CONSTRAINT identity_event_ukey UNIQUE (event_id, sequence_id)
);

CREATE TABLE mst.withdrawal_data (
  id             BIGSERIAL         NOT NULL,
  party_id       UUID,
  identity_id    CHARACTER VARYING,
  withdrawal_id  CHARACTER VARYING NOT NULL,
  wallet_id      CHARACTER VARYING,
  destination_id CHARACTER VARYING,
  amount         BIGINT            NOT NULL,
  currency_code  CHARACTER VARYING NOT NULL,
  CONSTRAINT withdrawal_data_pkey PRIMARY KEY (id),
  CONSTRAINT withdrawal_data_ukey UNIQUE (withdrawal_id)
);

CREATE TYPE mst.withdrawal_status AS ENUM ('pending', 'succeeded', 'failed');

CREATE TYPE mst.withdrawal_event_type AS ENUM ('WITHDRAWAL_CREATED', 'WITHDRAWAL_STATUS_CHANGED');

CREATE TABLE mst.withdrawal_event (
  id                BIGSERIAL                   NOT NULL,
  event_id          BIGINT                      NOT NULL,
  event_type        mst.withdrawal_event_type   NOT NULL,
  event_created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id       INT                         NOT NULL,
  withdrawal_id     CHARACTER VARYING           NOT NULL,
  withdrawal_status mst.withdrawal_status       NOT NULL,
  fee               BIGINT,
  CONSTRAINT withdrawal_event_pkey PRIMARY KEY (id),
  CONSTRAINT withdrawal_event_ukey UNIQUE (event_id, sequence_id)
);

CREATE TABLE mst.challenge_data (
  id                 BIGSERIAL         NOT NULL,
  identity_id        CHARACTER VARYING NOT NULL,
  challenge_id       CHARACTER VARYING NOT NULL,
  challenge_class_id CHARACTER VARYING NOT NULL,
  CONSTRAINT challenge_data_pkey PRIMARY KEY (id),
  CONSTRAINT challenge_data_ukey UNIQUE (identity_id, challenge_id)
);

CREATE TYPE mst.challenge_event_type AS ENUM ('CHALLENGE_CREATED', 'CHALLENGE_STATUS_CHANGED');
CREATE TYPE mst.challenge_status AS ENUM ('pending', 'cancelled', 'completed', 'failed');
CREATE TYPE mst.challenge_resolution AS ENUM ('approved', 'denied');

CREATE TABLE mst.challenge_event (
  id                    BIGSERIAL                   NOT NULL,
  event_id              BIGINT                      NOT NULL,
  event_type            mst.challenge_event_type    NOT NULL,
  event_created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id           INT                         NOT NULL,
  identity_id           CHARACTER VARYING           NOT NULL,
  challenge_id          CHARACTER VARYING           NOT NULL,
  challenge_status      mst.challenge_status        NOT NULL,
  challenge_resolution  mst.challenge_resolution,
  challenge_valid_until TIMESTAMP WITHOUT TIME ZONE,
  CONSTRAINT challenge_event_pkey PRIMARY KEY (id),
  CONSTRAINT challenge_event_ukey UNIQUE (event_id, sequence_id)
);

CREATE TABLE mst.wallet_data (
  id          BIGSERIAL         NOT NULL,
  party_id    UUID,
  identity_id CHARACTER VARYING,
  wallet_id   CHARACTER VARYING NOT NULL,
  wallet_name CHARACTER VARYING NOT NULL,
  CONSTRAINT wallet_data_pkey PRIMARY KEY (id),
  CONSTRAINT wallet_data_ukey UNIQUE (wallet_id)
);

CREATE TYPE mst.wallet_event_type AS ENUM ('WALLET_CREATED', 'WALLET_ACCOUNT_CREATED');

CREATE TABLE mst.wallet_event (
  id               BIGSERIAL                   NOT NULL,
  event_id         BIGINT                      NOT NULL,
  event_type       mst.wallet_event_type       NOT NULL,
  event_created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_occured_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  sequence_id      INT                         NOT NULL,
  wallet_id        CHARACTER VARYING           NOT NULL,
  identity_id      CHARACTER VARYING,
  currency_code    CHARACTER VARYING,
  CONSTRAINT wallet_event_pkey PRIMARY KEY (id),
  CONSTRAINT wallet_event_ukey UNIQUE (event_id, sequence_id)
);
