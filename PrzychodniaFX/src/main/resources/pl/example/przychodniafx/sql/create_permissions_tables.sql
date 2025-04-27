-- Create permissions table if not exists
CREATE TABLE IF NOT EXISTS permissions (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL,
    UNIQUE KEY unique_permission_name (permission_name)
);

-- Create roles table if not exists
CREATE TABLE IF NOT EXISTS roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    UNIQUE KEY unique_role_name (role_name)
);

-- Create role_permissions table if not exists
CREATE TABLE IF NOT EXISTS role_permissions (
    role_permission_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id),
    UNIQUE KEY unique_role_permission (role_id, permission_id)
);

-- Create user_roles table if not exists
CREATE TABLE IF NOT EXISTS user_roles (
    user_role_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    UNIQUE KEY unique_user_role (user_id, role_id)
);

-- Insert default permissions if they don't exist
INSERT IGNORE INTO permissions (permission_name) VALUES 
('Zarządzanie pacjentem'),
('Wyszukiwanie pacjenta'),
('Zarządzanie uprawnieniami'),
('Dodawanie pracownika'),
('Zarządzanie hasłami'),
('Zarządzanie wizytami');

-- Insert default roles if they don't exist
INSERT IGNORE INTO roles (role_name) VALUES 
('Administrator'),
('Lekarz'),
('Pielęgniarka'),
('Recepcjonista'),
('Pacjent');

-- Insert default role permissions if they don't exist
-- Administrator has all permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM roles r, permissions p
WHERE r.role_name = 'Administrator';

-- Lekarz permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM roles r, permissions p
WHERE r.role_name = 'Lekarz' 
AND p.permission_name IN ('Zarządzanie pacjentem', 'Wyszukiwanie pacjenta', 'Zarządzanie wizytami');

-- Pielęgniarka permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM roles r, permissions p
WHERE r.role_name = 'Pielęgniarka' 
AND p.permission_name IN ('Wyszukiwanie pacjenta', 'Zarządzanie wizytami');

-- Recepcjonista permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id 
FROM roles r, permissions p
WHERE r.role_name = 'Recepcjonista' 
AND p.permission_name IN ('Wyszukiwanie pacjenta', 'Zarządzanie wizytami', 'Dodawanie pracownika'); 