ALTER TABLE channel_join_requests
DROP COLUMN reviewed_by;

ALTER TABLE channel_join_requests
ADD COLUMN reviewed_at TIMESTAMPTZ;