-- Add is_forgotten column to Users table
ALTER TABLE Users
ADD COLUMN is_forgotten BOOLEAN DEFAULT FALSE; 