delete from car_rental_service;
delete from vehicle_register;
delete from car_rental_partner;
delete from vehicle_type;
delete from business_partner;
delete from conversation_account;
delete from message;
delete from conversation;
SET GLOBAL time_zone = '+07:00';
use vehicle_booking_rental
-- dữ liệu bảng business_partner
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
INSERT INTO car_rental_partner (business_partner_id) values (5),(6);


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
    (15, 500.00, '2023-10-01 14:30:00', 'Xe ô tô 4 chỗ đời mới', 10.0, 'Toyota', 'Bảo hiểm toàn diện', 5, 4.5, 100.00, 'available', 'WiFi, GPS', 4, 7),
    (10, 600.00, '2023-09-25 09:00:00', 'Xe ô tô 7 chỗ, không khói thuốc', 15.0, 'Ford', 'Bảo hành 2 năm', 3, 4.7, 150.00, 'available', 'Điều hòa, Bluetooth', 4, 8),
    (20, 400.00, '2023-10-05 17:45:00', 'Xe máy tay ga', 5.0, 'Honda', 'Bảo trì định kỳ', 10, 4.3, 50.00, 'available', 'Mũ bảo hiểm, Áo mưa', 5, 9),
    (10, 550.00, '2023-09-30 12:00:00', 'Xe ô tô 4 chỗ, đời 2020', 12.5, 'Hyundai', 'Bảo hiểm trách nhiệm dân sự', 7, 4.6, 120.00, 'available', 'GPS, Sạc điện thoại', 4, 7),
    (5, 700.00, '2023-09-28 10:15:00', 'Xe ô tô sang trọng', 20.0, 'BMW', 'Bảo hành 3 năm', 2, 4.8, 200.00, 'unavailable', 'Điều hòa, Ghế da', 5, 10);


--  dữ liệu mẫu  bảng car_rental_service
INSERT INTO car_rental_service (price, type, vehicle_register_id)
VALUES
    (5000.00, 1, 17),  
    (15000.00, 0, 16), 
    (20000.00, 0, 18), -- Dịch vụ xe du lich : 1
    (30000.00, 0, 19), -- Dịch vụ thuê xe :0
    (10000.00, 0, 20);
    
-- dữ liệu 
INSERT INTO conversation (create_at) VALUES
                                         ('2024-10-29 08:15:30'),
                                         ('2024-10-29 08:45:12'),
                                         ('2024-10-29 09:10:05'),
                                         ('2024-10-29 09:25:47'),
                                         ('2024-10-29 10:02:11');
INSERT INTO conversation_account (conversation_id,account_id,role_account) VALUES
                                         (1,1,'USER'),
                                         (1,2,'USER'),
                                         (2,3,'USER'),
                                         (2,2,'CAR_RENTAL_PARTNER'),
                                         (3,3,'USER'),
                                         (3,2,'USER');
INSERT INTO message
    (content, is_seen, seen_at, send_at, sender_id,sender_type ,recipient_id,recipient_type,conversation_id) VALUES
     ('Hello, how are you?', 1, '2024-10-29 08:10:00.000000', '2024-10-29 08:05:00.000000', 1,'USER',2,'USER', 1),
     ('I am doing well, thanks!', 1, '2024-10-29 08:12:00.000000', '2024-10-29 08:06:00.000000', 2,'USER',1,'USER', 1),
     ('What about you?', 0, NULL, '2024-10-29 08:07:00.000000', 1,'USER',2,'USER', 1),
     ('I am great too!', 1, '2024-10-29 08:15:00.000000', '2024-10-29 08:10:00.000000', 2,'USER',1,'USER', 1),
     ('Are we meeting later?', 0, NULL, '2024-10-29 08:20:00.000000', 1,'USER',2,'USER', 1);

INSERT INTO notification (create_at, message, title, type,is_seen)
VALUES
    ('2024-11-01 10:00:00', 'Bạn có một đơn đặt xe mới', 'Đơn thuê xe mới', 0,0), 
    ('2024-11-01 08:00:00', 'Yêu cầu thuê xe của bạn đã được chấp nhận.', 'Xác nhận thuê xe', 1,0),
    ('2024-11-02 09:15:00', 'Xe bạn thuê đã được trả thành công.', 'Xác nhận trả xe', 1,0);

--    ADMIN : 0
--     USER : 1
--     BUS_PARTNER: 2
--     CAR_RENTAL_PARTNER: 3
--     DRIVER:4
INSERT INTO account_notification ( account_type, account_id, notification_id)
VALUES
    ( 1, 3, 3), 
    ( 1, 3, 2), 
    (3, 2, 1); 



