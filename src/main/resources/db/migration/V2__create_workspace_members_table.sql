CREATE TABLE workspace_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL REFERENCES workspaces(id),
    user_id UUID NOT NULL REFERENCES users(id),
    role VARCHAR(225) NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uk_workspace_user UNIQUE (workspace_id, user_id)
);