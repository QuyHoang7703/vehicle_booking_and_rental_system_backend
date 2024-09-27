create table if not exists account
(
    id int NOT NULL,
    username varchar(50),
    password varchar(50),
    phone_number varchar(12),
    gender varchar(6),
    email varchar(50),
    image_id int,
    is_active boolean,
    reason varchar(255)
)