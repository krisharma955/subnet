CREATE TABLE channels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL REFERENCES workspaces(id),
    name VARCHAR(225) NOT NULL,
    description VARCHAR(1000),
    type VARCHAR(225) NOT NULL,
    created_by_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_workspace_name UNIQUE (workspace_id, name)
);

CREATE TABLE channel_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    channel_id UUID NOT NULL REFERENCES channels(id),
    user_id UUID NOT NULL REFERENCES users(id),
    role VARCHAR(225) NOT NULL,
    added_by_id UUID REFERENCES users(id),
    joined_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_channel_user UNIQUE (channel_id, user_id)
);