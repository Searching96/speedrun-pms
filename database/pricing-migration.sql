-- =============================================
-- Pricing System Migration
-- =============================================

-- Create pricing_zones table
CREATE TABLE IF NOT EXISTS pricing_zones (
    id CHAR(36) PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Create ward_zone_mappings table
CREATE TABLE IF NOT EXISTS ward_zone_mappings (
    id CHAR(36) PRIMARY KEY,
    ward_code VARCHAR(20) NOT NULL,
    zone_id CHAR(36) NOT NULL,
    FOREIGN KEY (ward_code) REFERENCES wards(code) ON DELETE CASCADE,
    FOREIGN KEY (zone_id) REFERENCES pricing_zones(id) ON DELETE CASCADE,
    UNIQUE KEY unique_ward_zone (ward_code)
);

-- Create shipping_rates table
CREATE TABLE IF NOT EXISTS shipping_rates (
    id CHAR(36) PRIMARY KEY,
    from_zone_id CHAR(36) NOT NULL,
    to_zone_id CHAR(36) NOT NULL,
    base_price DECIMAL(15, 2) NOT NULL,
    price_per_kg DECIMAL(15, 2) NOT NULL,
    valid_from TIMESTAMP NULL,
    valid_to TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (from_zone_id) REFERENCES pricing_zones(id) ON DELETE CASCADE,
    FOREIGN KEY (to_zone_id) REFERENCES pricing_zones(id) ON DELETE CASCADE,
    INDEX idx_active_rates (from_zone_id, to_zone_id, is_active)
);

-- =============================================
-- Seed Pricing Zones
-- =============================================

INSERT INTO pricing_zones (id, code, name, description) VALUES
(UUID(), 'NOI_THANH_HCM', 'Nội thành TP.HCM', 'Các quận nội thành TP. Hồ Chí Minh'),
(UUID(), 'NGOAI_THANH_HCM', 'Ngoại thành TP.HCM', 'Các huyện ngoại thành TP. Hồ Chí Minh'),
(UUID(), 'NOI_THANH_HN', 'Nội thành Hà Nội', 'Các quận nội thành Hà Nội'),
(UUID(), 'NGOAI_THANH_HN', 'Ngoại thành Hà Nội', 'Các huyện ngoại thành Hà Nội'),
(UUID(), 'LIEN_TINH', 'Liên tỉnh', 'Các tỉnh/thành phố khác');

-- =============================================
-- Seed Sample Shipping Rates
-- =============================================

-- HCM Nội thành -> HCM Nội thành
INSERT INTO shipping_rates (id, from_zone_id, to_zone_id, base_price, price_per_kg, is_active) 
SELECT UUID(), 
    (SELECT id FROM pricing_zones WHERE code='NOI_THANH_HCM'),
    (SELECT id FROM pricing_zones WHERE code='NOI_THANH_HCM'),
    15000, 5000, TRUE;

-- HCM Nội thành -> HCM Ngoại thành
INSERT INTO shipping_rates (id, from_zone_id, to_zone_id, base_price, price_per_kg, is_active) 
SELECT UUID(), 
    (SELECT id FROM pricing_zones WHERE code='NOI_THANH_HCM'),
    (SELECT id FROM pricing_zones WHERE code='NGOAI_THANH_HCM'),
    20000, 7000, TRUE;

-- HCM Nội thành -> Liên tỉnh
INSERT INTO shipping_rates (id, from_zone_id, to_zone_id, base_price, price_per_kg, is_active) 
SELECT UUID(), 
    (SELECT id FROM pricing_zones WHERE code='NOI_THANH_HCM'),
    (SELECT id FROM pricing_zones WHERE code='LIEN_TINH'),
    30000, 12000, TRUE;

-- HN Nội thành -> HN Nội thành
INSERT INTO shipping_rates (id, from_zone_id, to_zone_id, base_price, price_per_kg, is_active) 
SELECT UUID(), 
    (SELECT id FROM pricing_zones WHERE code='NOI_THANH_HN'),
    (SELECT id FROM pricing_zones WHERE code='NOI_THANH_HN'),
    15000, 5000, TRUE;

-- HN Nội thành -> Liên tỉnh
INSERT INTO shipping_rates (id, from_zone_id, to_zone_id, base_price, price_per_kg, is_active) 
SELECT UUID(), 
    (SELECT id FROM pricing_zones WHERE code='NOI_THANH_HN'),
    (SELECT id FROM pricing_zones WHERE code='LIEN_TINH'),
    30000, 12000, TRUE;

-- Liên tỉnh -> Liên tỉnh
INSERT INTO shipping_rates (id, from_zone_id, to_zone_id, base_price, price_per_kg, is_active) 
SELECT UUID(), 
    (SELECT id FROM pricing_zones WHERE code='LIEN_TINH'),
    (SELECT id FROM pricing_zones WHERE code='LIEN_TINH'),
    35000, 15000, TRUE;

-- =============================================
-- Seed Sample Ward-Zone Mappings (HCM)
-- =============================================

-- Map some HCM inner city wards to NOI_THANH_HCM
INSERT INTO ward_zone_mappings (id, ward_code, zone_id)
SELECT UUID(), code, (SELECT id FROM pricing_zones WHERE code='NOI_THANH_HCM')
FROM wards 
WHERE province_code = '79' 
AND administrative_unit_id = 3  -- Phường (inner city wards)
LIMIT 100;

-- Map remaining HCM wards to NGOAI_THANH_HCM
INSERT INTO ward_zone_mappings (id, ward_code, zone_id)
SELECT UUID(), code, (SELECT id FROM pricing_zones WHERE code='NGOAI_THANH_HCM')
FROM wards 
WHERE province_code = '79' 
AND administrative_unit_id = 4  -- Xã (suburban wards)
AND code NOT IN (SELECT ward_code FROM ward_zone_mappings);

-- =============================================
-- Seed Sample Ward-Zone Mappings (Hanoi)
-- =============================================

-- Map some Hanoi inner city wards to NOI_THANH_HN
INSERT INTO ward_zone_mappings (id, ward_code, zone_id)
SELECT UUID(), code, (SELECT id FROM pricing_zones WHERE code='NOI_THANH_HN')
FROM wards 
WHERE province_code = '01' 
AND administrative_unit_id = 3  -- Phường
LIMIT 100;

-- Map remaining Hanoi wards to NGOAI_THANH_HN
INSERT INTO ward_zone_mappings (id, ward_code, zone_id)
SELECT UUID(), code, (SELECT id FROM pricing_zones WHERE code='NGOAI_THANH_HN')
FROM wards 
WHERE province_code = '01' 
AND administrative_unit_id = 4  -- Xã
AND code NOT IN (SELECT ward_code FROM ward_zone_mappings);

-- =============================================
-- Map all other provinces to LIEN_TINH
-- =============================================

INSERT INTO ward_zone_mappings (id, ward_code, zone_id)
SELECT UUID(), code, (SELECT id FROM pricing_zones WHERE code='LIEN_TINH')
FROM wards 
WHERE province_code NOT IN ('01', '79')  -- Not Hanoi or HCM
AND code NOT IN (SELECT ward_code FROM ward_zone_mappings);
