delete
from mst.deposit;
alter table mst.deposit
    drop column current;
alter table mst.deposit
    add column created_at timestamp without time zone not null;
alter table mst.deposit
    add constraint deposit_ukey unique (deposit_id);
alter table mst.deposit rename to deposit_data;

drop table mst.identity_data;
drop table mst.identity_event;

create table mst.identity_data
(
    id                              BIGSERIAL                   NOT NULL,
    party_id                        UUID                        NOT NULL,
    party_contract_id               CHARACTER VARYING,
    identity_id                     CHARACTER VARYING           NOT NULL,
    identity_provider_id            CHARACTER VARYING           NOT NULL,
    identity_class_id               CHARACTER VARYING           NOT NULL,
    created_at                      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id                        BIGINT                      NOT NULL,
    event_type                      mst.identity_event_type     NOT NULL,
    event_created_at                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_occurred_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    sequence_id                     INT                         NOT NULL,
    identity_effective_challenge_id CHARACTER VARYING,
    identity_level_id               CHARACTER VARYING,
    CONSTRAINT identity_data_pkey PRIMARY KEY (id),
    CONSTRAINT identity_data_ukey UNIQUE (identity_id)
);

CREATE INDEX identity_event_created_at_idx
    on mst.identity_data (event_created_at);
CREATE INDEX identity_id_idx
    on mst.identity_data (identity_id);
CREATE INDEX identity_event_occurred_at_idx
    on mst.identity_data (event_occurred_at);
CREATE INDEX identity_party_id_idx
    on mst.identity_data (party_id);

drop table mst.withdrawal_data;
drop table mst.withdrawal_event;

create table mst.withdrawal_data
(
    id                BIGSERIAL                   NOT NULL,
    party_id          UUID,
    identity_id       CHARACTER VARYING,
    withdrawal_id     CHARACTER VARYING           NOT NULL,
    wallet_id         CHARACTER VARYING           NOT NULL,
    destination_id    CHARACTER VARYING           NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    amount            BIGINT                      NOT NULL,
    currency_code     CHARACTER VARYING           NOT NULL,
    event_id          BIGINT                      NOT NULL,
    event_type        mst.withdrawal_event_type   NOT NULL,
    event_created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_occurred_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    sequence_id       INT                         NOT NULL,
    withdrawal_status mst.withdrawal_status       NOT NULL,
    fee               BIGINT,
    CONSTRAINT withdrawal_data_pkey PRIMARY KEY (id),
    CONSTRAINT withdrawal_data_ukey UNIQUE (withdrawal_id)
);

CREATE INDEX withdrawal_event_created_at_idx
    on mst.withdrawal_data (event_created_at);
CREATE INDEX withdrawal_id_idx
    on mst.withdrawal_data (withdrawal_id);
CREATE INDEX withdrawal_event_occurred_at_idx
    on mst.withdrawal_data (event_occurred_at);
CREATE INDEX withdrawal_party_id_idx
    on mst.withdrawal_data (party_id);

drop table mst.challenge_data;
drop table mst.challenge_event;

create table mst.challenge_data
(
    id                    BIGSERIAL                   NOT NULL,
    identity_id           CHARACTER VARYING           NOT NULL,
    challenge_id          CHARACTER VARYING           NOT NULL,
    challenge_class_id    CHARACTER VARYING           NOT NULL,
    created_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id              BIGINT                      NOT NULL,
    event_type            mst.challenge_event_type    NOT NULL,
    event_created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_occurred_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    sequence_id           INT                         NOT NULL,
    challenge_status      mst.challenge_status        NOT NULL,
    challenge_resolution  mst.challenge_resolution,
    challenge_valid_until TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT challenge_data_pkey PRIMARY KEY (id),
    CONSTRAINT challenge_data_ukey UNIQUE (identity_id, challenge_id)
);


CREATE INDEX challenge_event_created_at_idx
    on mst.challenge_data (event_created_at);
CREATE INDEX identity_challenge_id_idx
    on mst.challenge_data (identity_id, challenge_id);
CREATE INDEX challenge_event_occurred_at_idx
    on mst.challenge_data (event_occurred_at);

drop table mst.wallet_data;
drop table mst.wallet_event;

create table mst.wallet_data
(
    id                BIGSERIAL                   NOT NULL,
    party_id          UUID,
    identity_id       CHARACTER VARYING,
    wallet_id         CHARACTER VARYING           NOT NULL,
    wallet_name       CHARACTER VARYING           NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id          BIGINT                      NOT NULL,
    event_type        mst.wallet_event_type       NOT NULL,
    event_created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_occurred_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    sequence_id       INT                         NOT NULL,
    currency_code     CHARACTER VARYING,
    CONSTRAINT wallet_data_pkey PRIMARY KEY (id),
    CONSTRAINT wallet_data_ukey UNIQUE (wallet_id)
);

CREATE INDEX wallet_event_created_at_idx
    on mst.wallet_data (event_created_at);
CREATE INDEX wallet_id_idx
    on mst.wallet_data (wallet_id);
CREATE INDEX wallet_event_occurred_at_idx
    on mst.wallet_data (event_occurred_at);


