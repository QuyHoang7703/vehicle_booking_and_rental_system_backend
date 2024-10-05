USE vehicle_booking_rental;
INSERT INTO account (is_active, birth_day, email, reason, gender, name, password, phone_number, username,  active, avatar, lock_reason, male, refresh_token)
VALUES
    (1, '1990-01-15', 'john.doe@example.com', NULL, 'Male', 'John Doe', '$2a$12$ZNeYKBeLZKXHlf6w90xztOFvf1yJgjruTxrgb3vpsUUxjv/PZubye', '123456789', 'john_doe',  1, 'avatar1.jpg', NULL, 1, 'sample_refresh_token_1'),
    (1, '1985-06-20', 'jane.doe@example.com', 'Forgot Password', 'Female', 'Jane Doe', '$2a$12$bkWl3l7PxZ0cflPW9y/Ydeaa1p/tMXNDmcm8PhPsiTf3SiPyXT6/6', '987654321', 'jane_doe',  1, 'avatar2.jpg', 'Account Locked', 0, 'sample_refresh_token_2'),
    (0, '1995-12-25', 'alex.smith@example.com', 'Inactive Account', 'Male', 'Alex Smith', '$2a$12$87h9GkescNPfbiCRD9HDSOGMEp5RfQYkGWr5sZia8rKgXOW0lhK32', '564738291', 'alex_smith', 0, 'avatar3.jpg', 'Inactive for long', 1, 'sample_refresh_token_3'),
    (1, '2000-03-05', 'emily.johnson@example.com', NULL, 'Female', 'Emily Johnson', '$2a$12$87h9GkescNPfbiCRD9HDSOGMEp5RfQYkGWr5sZia8rKgXOW0lhK32', '182736455', 'emily_j', 1, 'avatar4.jpg', NULL, 0, 'sample_refresh_token_4'),
    (0, '1988-11-11', 'michael.brown@example.com', 'Manual Deactivation', 'Male', 'Michael Brown', '$2a$12$87h9GkescNPfbiCRD9HDSOGMEp5RfQYkGWr5sZia8rKgXOW0lhK32', '495867234', 'michael_b',  0, 'avatar5.jpg', 'User Request', 1, 'sample_refresh_token_5');


--  dữ liệu bảng business_partner
INSERT INTO business_partner (address, business_license, business_name, email_of_representative, nam_of_representative, partner_type, phone_of_representative, account_id)
VALUES
    ('123 Main Street, City A', 'BL-123456', 'Tech Solutions Co.', 'john.tech@example.com', 'John Doe', 'Technology', '123-456-7890', 11),
    ('456 Elm Street, City B', 'BL-789012', 'Health Care Inc.', 'jane.health@example.com', 'Jane Smith', 'Healthcare', '987-654-3210', 13),
    
    ('789 Maple Avenue, City C', 'BL-345678', 'Edu Academy', 'mark.edu@example.com', 'Mark Johnson', 'Education', '555-123-4567', 14);


--  dữ liệu bảng car_rental_partner
INSERT INTO car_rental_partner (business_partner_id) values (1),(2);


-- dữ liệu bảng vehicle_type
INSERT INTO vehicle_type (description, name, price)
VALUES
    ('Xe máy phổ thông, tiết kiệm xăng', 'Xe máy', 5000.00),
    ('Xe ô tô 4 chỗ phù hợp gia đình nhỏ', 'Xe ô tô 4 chỗ', 15000.00),
    ('Xe ô tô 7 chỗ rộng rãi, phù hợp đi du lịch', 'Xe ô tô 7 chỗ', 20000.00),
    ('Xe máy tay ga cao cấp', 'Xe tay ga', 8000.00),
    ('Xe ô tô sang trọng với nhiều tiện nghi', 'Xe hạng sang', 30000.00);


--  dữ liệu bảng vehicle_register
INSERT INTO vehicle_register (amount, car_deposit, date_of_status, description, discount_percentage, manufacturer, policy, quantity, rating_total, reservation_fees, status, ulties, car_rental_partner_id, vehicle_type_id)
VALUES
    (15000.50, 500.00, '2023-10-01 14:30:00', 'Xe ô tô 4 chỗ đời mới', 10.0, 'Toyota', 'Bảo hiểm toàn diện', 5, 4.5, 100.00, 'available', 'WiFi, GPS', 1, 2),
    (20000.00, 600.00, '2023-09-25 09:00:00', 'Xe ô tô 7 chỗ, không khói thuốc', 15.0, 'Ford', 'Bảo hành 2 năm', 3, 4.7, 150.00, 'available', 'Điều hòa, Bluetooth', 1, 3),
    (12000.75, 400.00, '2023-10-05 17:45:00', 'Xe máy tay ga', 5.0, 'Honda', 'Bảo trì định kỳ', 10, 4.3, 50.00, 'available', 'Mũ bảo hiểm, Áo mưa', 2, 4),
    (18000.00, 550.00, '2023-09-30 12:00:00', 'Xe ô tô 4 chỗ, đời 2020', 12.5, 'Hyundai', 'Bảo hiểm trách nhiệm dân sự', 7, 4.6, 120.00, 'available', 'GPS, Sạc điện thoại', 1, 2),
    (25000.00, 700.00, '2023-09-28 10:15:00', 'Xe ô tô sang trọng', 20.0, 'BMW', 'Bảo hành 3 năm', 2, 4.8, 200.00, 'unavailable', 'Điều hòa, Ghế da', 2, 5);


--  dữ liệu mẫu  bảng car_rental_service
INSERT INTO car_rental_service (price, type, service_id)
VALUES
    (5000.00, 1, 7),  
    (15000.00, 0, 6), 
    (20000.00, 0, 8), -- Dịch vụ xe du lich : 1
    (30000.00, 0, 9), -- Dịch vụ thuê xe :0
    (10000.00, 0, 10);