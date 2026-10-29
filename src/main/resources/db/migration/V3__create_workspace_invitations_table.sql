CREATE TABLE workspace_invitations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL REFERENCES workspaces(id),
    email VARCHAR(225) NOT NULL,
    invited_by_id UUID NOT NULL REFERENCES users(id),
    invite_token_hash VARCHAR(225) NOT NULL UNIQUE,
    role VARCHAR(225) NOT NULL,
    status VARCHAR(225) NOT NULL DEFAULT 'PENDING',
    expires_at TIMESTAMPTZ NOT NULL,
    accepted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_invitation_status
    CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'EXPIRED'))
);