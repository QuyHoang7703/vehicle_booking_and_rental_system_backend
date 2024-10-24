USE vehicle_booking_rental;

--  dữ liệu bảng business_partner
INSERT INTO business_partner 
(address, approval_status, avatar, business_name, email_of_representative, name_of_representative, partner_type, phone_of_representative, account_id) 
VALUES 
('102 Nguyễn Văn Linh, Đà Nẵng', 'PENDING_APPROVAL', 'https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/avatar1.jpg', 'Công ty TNHH Vận Tải Minh Tâm', 'minhtamvt@gmail.com', 'Phạm Minh Tâm', 1, '0987654321', 2);
INSERT INTO business_partner 
(address, approval_status, avatar, business_name, email_of_representative, name_of_representative, partner_type, phone_of_representative, account_id) 
VALUES 
('58 Hoàng Quốc Việt, Hà Nội', 'APPROVED', 'https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/avatar2.jpg', 'Nhà xe Hoàng Long', 'hoanglongvtdn@gmail.com', 'Trần Hoàng Long', 0, '0901234567', 1);
INSERT INTO business_partner 
(address, approval_status, avatar, business_name, email_of_representative, name_of_representative, partner_type, phone_of_representative, account_id) 
VALUES 
('20 Lê Lợi, Huế', 'PENDING_APPROVAL', 'https://vehiclerentalbookingsystem.s3.ap-southeast-2.amazonaws.com/avatar3.jpg', 'Công ty Vận Tải Hưng Thịnh', 'hungthinhvt@gmail.com', 'Nguyễn Văn Hưng', 1, '0934567890', 2);


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
INSERT INTO car_rental_service (price, type, vehicle_register_id)
VALUES
    (5000.00, 1, 2),  
    (15000.00, 0, 1), 
    (20000.00, 0, 3), -- Dịch vụ xe du lich : 1
    (30000.00, 0, 4), -- Dịch vụ thuê xe :0
    (10000.00, 0, 5);
    
-- dữ liệu 
