CREATE TABLE channel_join_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    channel_id UUID NOT NULL REFERENCES channels(id),
    user_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(225) NOT NULL DEFAULT 'PENDING',
    reviewed_by_id UUID REFERENCES users(id),
    requested_at TIMESTAMPTZ NOT NULL,
    reviewed_by TIMESTAMPTZ,

    CONSTRAINT uk_channel_id_user_id UNIQUE (channel_id, user_id)
);