
-- Permission account
CREATE TABLE IF NOT EXISTS account
(
    id INT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone_number VARCHAR(12) DEFAULT NULL,
    gender VARCHAR(6) DEFAULT NULL,
    email VARCHAR(50) DEFAULT NULL,
    image_id INT DEFAULT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    reason VARCHAR(255) DEFAULT NULL,

    PRIMARY KEY (id)
    );
CREATE TABLE IF NOT EXISTS  role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    create_at TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS permission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    api_path VARCHAR(255),
    method VARCHAR(20)
    );
CREATE TABLE IF NOT EXISTS role_permission (
   id INT PRIMARY KEY AUTO_INCREMENT,
   role_id INT,
   permission_id INT,
   FOREIGN KEY (role_id) REFERENCES role(id),
   FOREIGN KEY (permission_id) REFERENCES permission(id)

    );



CREATE TABLE IF NOT EXISTS account_role (
   id INT PRIMARY KEY AUTO_INCREMENT,
   account_id INT,
   role_id INT,
   time_cancel TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   time_register TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   lock_reason TEXT,
   FOREIGN KEY (account_id) REFERENCES account(id),
   FOREIGN KEY (role_id) REFERENCES role(id)
    );
-- Notification
CREATE TABLE IF NOT EXISTS notification (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    message TEXT,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS account_notification (
   id INT PRIMARY KEY AUTO_INCREMENT,
   account_id INT,
   notification_id INT,
   is_seen BOOLEAN DEFAULT FALSE,
   FOREIGN KEY (account_id) REFERENCES account(id),
   FOREIGN KEY (notification_id) REFERENCES notification(id)
    );
-- Conservation
CREATE TABLE IF NOT EXISTS conservation (
   id INT PRIMARY KEY AUTO_INCREMENT,
   create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS message (
    id INT PRIMARY KEY AUTO_INCREMENT,
    content TEXT,
    send_at DATETIME,
    conservation_id INT,
    sender_id INT,
    is_seen BOOLEAN DEFAULT FALSE,
    seen_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conservation_id) REFERENCES conservation(id)
    );

CREATE TABLE IF NOT EXISTS conversation_account (
    id INT PRIMARY KEY AUTO_INCREMENT,
    account_id INT,
    conservation_id INT,
    role_account VARCHAR(50),
    FOREIGN KEY (account_id) REFERENCES account(id),
    FOREIGN KEY (conservation_id) REFERENCES conservation(id)
    );
-- Voucher Account
CREATE TABLE IF NOT EXISTS voucher (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    description TEXT,
    start_date DATE,
    end_date DATE,
    voucher_percentage DECIMAL(5,2),
    voucher_amount DECIMAL(10,2),
    status VARCHAR(20),
    number INT
    );
CREATE TABLE IF NOT EXISTS VoucherAccount (
    id INT PRIMARY KEY AUTO_INCREMENT,
    voucher_id INT ,
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES account(id),
    FOREIGN KEY (voucher_id) REFERENCES voucher(id)

    );
-- Rating & Payment & BankAccount
CREATE TABLE IF NOT EXISTS orders (
    id INT PRIMARY KEY AUTO_INCREMENT ,
    create_at TIMESTAMP,
    order_type VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS payment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    status VARCHAR(50),
    paid_at TIMESTAMP,
    order_id INT,
    payment_type VARCHAR(50),
    order_type VARCHAR(50),
    FOREIGN KEY (order_id) REFERENCES orders(id)
    )                      ;


CREATE TABLE IF NOT EXISTS rating (
    id INT PRIMARY KEY,
    create_at TIMESTAMP,
    ratable_id INT,
    ratable_value VARCHAR(255),
    content TEXT,
    account_id INT,
    order_type VARCHAR(50),
    FOREIGN KEY (ratable_id) REFERENCES orders(id),
    FOREIGN KEY (account_id) REFERENCES account(id)
    );
CREATE TABLE IF NOT EXISTS bank_account (
    id INT PRIMARY KEY,
    account_number VARCHAR(20),
    account_holder_name VARCHAR(100),
    bank_name VARCHAR(100),
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES account(id)
    );
-- images
CREATE TABLE IF NOT EXISTS images (
    id INT PRIMARY KEY AUTO_INCREMENT,
    image_path VARCHAR(255),
    image_type VARCHAR(255),
    owner_id INT
    );
CREATE TABLE IF NOT EXISTS business_partner (
    id INT PRIMARY KEY AUTO_INCREMENT,
    business_license VARCHAR(50),
    business_name VARCHAR(100),
    email_of_representative VARCHAR(100),
    nam_of_representative VARCHAR(100),
    phone_of_representative VARCHAR(20),
    address VARCHAR(255),
    partner_type VARCHAR(50),
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES account(id)
    );
-- Sql Car Rental Partner
CREATE TABLE IF NOT EXISTS vehicle_type
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    description VARCHAR(255),
    price DECIMAL(10,2)
    );
CREATE TABLE IF NOT EXISTS car_rental_partner
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    business_partner_id INT,
    FOREIGN KEY (business_partner_id) REFERENCES business_partner(id)    );

CREATE TABLE IF NOT EXISTS vehicle_register (
    id int PRIMARY KEY AUTO_INCREMENT,
    manufacturer VARCHAR(255) NOT NULL,
    description TEXT,
    quantity INT DEFAULT 0,
    status VARCHAR(50),
    dateofstatus DATE,
    discount_percentage DECIMAL(5, 2) DEFAULT 0.00,
    vehicle_type_id int,
    car_rental_partner_id int,
    car_deposit DECIMAL(10, 2) DEFAULT 0.00,
    reservation_fees DECIMAL(10, 2) DEFAULT 0.00,
    ulties TEXT,
    policy TEXT,
    rating_total float DEFAULT 0,
    amount DECIMAL(15, 2) DEFAULT 0.00,

    -- Ràng buộc khóa ngoại
    CONSTRAINT FK_VehicleType FOREIGN KEY (vehicle_type_id) REFERENCES vehicle_type(id),
    CONSTRAINT FK_CarRentalPartner FOREIGN KEY (car_rental_partner_id) REFERENCES car_rental_partner(id)
    );
CREATE TABLE IF NOT EXISTS car_rental_service (
    id int PRIMARY KEY AUTO_INCREMENT,
    vehicle_register_id int NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    type TINYINT NOT NULL CHECK (type IN (0, 1)),

    FOREIGN KEY (vehicle_register_id) REFERENCES vehicle_register(id)
    );
CREATE TABLE IF NOT EXISTS car_rental_orders (
    order_id INT PRIMARY KEY ,
    start_rental_time DATETIME,
    end_rental_time DATETIME,
    pickup_location VARCHAR(255) NOT NULL,
    total DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    voucher_value DECIMAL(10, 2) DEFAULT 0.00,
    voucher_percentage DECIMAL(5, 2) DEFAULT 0.00,
    amount DECIMAL(10, 2) NOT NULL,
    car_deposit DECIMAL(10, 2) DEFAULT 0.00,
    reservation_fee DECIMAL(10, 2) DEFAULT 0.00,
    price DECIMAL(10, 2) NOT NULL,
    account_id int NOT NULL,
    car_rental_service_id int NOT NULL,

    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (account_id) REFERENCES account(id),
    FOREIGN KEY (car_rental_service_id) REFERENCES car_rental_service(id)
    );


-- bus trip service
CREATE TABLE IF NOT EXISTS bus_partner (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description TEXT,
    url VARCHAR(255),
    policy TEXT,
    business_partner_id INT,

    FOREIGN KEY (business_partner_id) REFERENCES business_partner(id)
    );
CREATE TABLE IF NOT EXISTS bus_type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    number_of_seat INT,
    chair_type VARCHAR(50)
    );
CREATE TABLE IF NOT EXISTS bus (
    id INT PRIMARY KEY AUTO_INCREMENT,
    license_plate VARCHAR(20),
    bus_type_id INT,
    bus_partner_id INT,
    FOREIGN KEY (bus_type_id) REFERENCES bus_type(id),
    FOREIGN KEY (bus_partner_id) REFERENCES bus_partner(id)
    );
CREATE TABLE IF NOT EXISTS bus_trip (
    id INT PRIMARY KEY AUTO_INCREMENT,
    departure_location VARCHAR(255),
    departure_time DATETIME,
    destination VARCHAR(255),
    duration_journey float,
    price_ticket float,
    status VARCHAR(20),
    update_status_at DATETIME,
    discount_percentage float,
    available_seat INT,
    rating_total float,
    bus_id INT,
    bus_partner_id INT,
    FOREIGN KEY (bus_id) REFERENCES bus(id),
    FOREIGN KEY (bus_partner_id) REFERENCES bus_partner(id)
    );
CREATE TABLE IF NOT EXISTS utilities (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    image_id int,
    description TEXT,
    FOREIGN KEY (image_id) REFERENCES images(id)
    );
CREATE TABLE IF NOT EXISTS order_bus_trip (
    order_id INT PRIMARY KEY ,
    number_of_ticket INT,
    price_total float,
    status VARCHAR(20),
    create_at DATETIME,
    departure_location VARCHAR(255),
    departure_time DATETIME,
    voucher_value float,
    account_id INT,
    bus_trip_id INT,
    FOREIGN KEY (account_id) REFERENCES account(id),
    FOREIGN KEY (bus_trip_id) REFERENCES bus_trip(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
    );
CREATE TABLE IF NOT EXISTS bus_utilities (
    id INT PRIMARY KEY AUTO_INCREMENT,
    bus_id int,
    utilities_id int,
    FOREIGN KEY (bus_id) REFERENCES bus(id),
    FOREIGN KEY (utilities_id) REFERENCES utilities(id)
    );
-- Driver
CREATE TABLE IF NOT EXISTS driver (
    id INT PRIMARY KEY AUTO_INCREMENT,
    driver_license VARCHAR(20),
    images_id INT,
    citizen_id VARCHAR(20),
    vehicle_type_id INT,
    status VARCHAR(20),
    account_id INT,
    location VARCHAR(100),
    license_plate VARCHAR(20),
    car_insurance VARCHAR(100),
    rating_total DECIMAL(10,2),
    FOREIGN KEY (images_id) REFERENCES images(id),
    FOREIGN KEY (vehicle_type_id) REFERENCES vehicle_type(id),
    FOREIGN KEY (account_id) REFERENCES account(id)
    );
CREATE TABLE IF NOT EXISTS booking (
    order_id INT PRIMARY KEY ,
    create_at TIMESTAMP,
    starting_location VARCHAR(100),
    destination VARCHAR(100),
    status VARCHAR(50),
    total DECIMAL(10,2),
    distance DECIMAL(10,2),
    driver_id INT,
    voucher_percentage DECIMAL(5,2),
    voucher_amount DECIMAL(10,2),
    account_id INT,
    vehicles_type VARCHAR(50),
    FOREIGN KEY (driver_id) REFERENCES driver(id),
    FOREIGN KEY (account_id) REFERENCES account(id),
    FOREIGN KEY (order_id) REFERENCES orders(id)
    );
