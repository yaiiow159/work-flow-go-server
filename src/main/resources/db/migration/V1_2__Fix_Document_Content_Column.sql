-- Change content column type from oid to bytea
-- First drop the existing column and then add a new one with the correct type
ALTER TABLE documents DROP COLUMN content;
ALTER TABLE documents ADD COLUMN content bytea;
