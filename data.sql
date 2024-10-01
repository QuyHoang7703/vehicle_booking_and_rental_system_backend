INSERT INTO users (is_active, birth_day, email, reason, gender, name, password, phone_number, username, account_type, active, avatar, lock_reason, male, refresh_token)
VALUES
    (1, '1990-01-15', 'john.doe@example.com', NULL, 'Male', 'John Doe', '$2a$12$ZNeYKBeLZKXHlf6w90xztOFvf1yJgjruTxrgb3vpsUUxjv/PZubye', '123456789', 'john_doe', 1, 1, 'avatar1.jpg', NULL, 1, 'sample_refresh_token_1'),
    (1, '1985-06-20', 'jane.doe@example.com', 'Forgot Password', 'Female', 'Jane Doe', '$2a$12$bkWl3l7PxZ0cflPW9y/Ydeaa1p/tMXNDmcm8PhPsiTf3SiPyXT6/6', '987654321', 'jane_doe', 2, 1, 'avatar2.jpg', 'Account Locked', 0, 'sample_refresh_token_2'),
    (0, '1995-12-25', 'alex.smith@example.com', 'Inactive Account', 'Male', 'Alex Smith', '$2a$12$87h9GkescNPfbiCRD9HDSOGMEp5RfQYkGWr5sZia8rKgXOW0lhK32', '564738291', 'alex_smith', 3, 0, 'avatar3.jpg', 'Inactive for long', 1, 'sample_refresh_token_3'),
    (1, '2000-03-05', 'emily.johnson@example.com', NULL, 'Female', 'Emily Johnson', '$2a$12$87h9GkescNPfbiCRD9HDSOGMEp5RfQYkGWr5sZia8rKgXOW0lhK32', '182736455', 'emily_j', 1, 1, 'avatar4.jpg', NULL, 0, 'sample_refresh_token_4'),
    (0, '1988-11-11', 'michael.brown@example.com', 'Manual Deactivation', 'Male', 'Michael Brown', '$2a$12$87h9GkescNPfbiCRD9HDSOGMEp5RfQYkGWr5sZia8rKgXOW0lhK32', '495867234', 'michael_b', 2, 0, 'avatar5.jpg', 'User Request', 1, 'sample_refresh_token_5');
