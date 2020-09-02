create type mst.blocking_type as enum ('unblocked', 'blocked');

alter table mst.identity_data
    add column name character varying;
alter table mst.identity_data
    add column blocking mst.blocking_type;
alter table mst.identity_data
    add column external_id character varying;
