DELETE FROM mst.deposit_data;
ALTER TABLE mst.deposit_data DROP COLUMN sequence_id;

DELETE FROM mst.wallet_data;
ALTER TABLE mst.wallet_data DROP COLUMN sequence_id;

DELETE FROM mst.identity_data;
ALTER TABLE mst.identity_data DROP COLUMN sequence_id;

DELETE FROM mst.withdrawal_data;
ALTER TABLE mst.withdrawal_data DROP COLUMN sequence_id;
