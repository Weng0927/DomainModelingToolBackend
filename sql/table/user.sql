USE test;
DROP TABLE IF EXISTS user;
CREATE TABLE user (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_name VARCHAR(50),
  nick_name VARCHAR(50),
  avatar_url VARCHAR(255),
  password VARCHAR(255),
  email VARCHAR(50),
  phone_number VARCHAR(11),
  status BOOLEAN,
  login_ip VARCHAR(255),
  login_date DATETIME,
  CONSTRAINT unique_user_name UNIQUE (user_name)
);