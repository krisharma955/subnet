ALTER TABLE messages
ADD COLUMN parent_message_id UUID REFERENCES messages(id);