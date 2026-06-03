DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_db;

CREATE TABLE role (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_role_name CHECK (role_name IN ('ROLE_ADMIN', 'ROLE_STUDENT'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user` (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    role_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(role_id),
    CONSTRAINT chk_user_active CHECK (is_active IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE book (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(100) NOT NULL,
    publisher VARCHAR(100) NOT NULL,
    publish_date DATE,
    category VARCHAR(50),
    price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    description TEXT,
    is_available TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_book_stock CHECK (stock >= 0),
    CONSTRAINT chk_book_price CHECK (price >= 0),
    CONSTRAINT chk_book_available CHECK (is_available IN (0, 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE borrow_record (
    record_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date DATE,
    return_date DATETIME,
    status VARCHAR(20) NOT NULL DEFAULT 'BORROWING',
    notes VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_borrow_user FOREIGN KEY (user_id) REFERENCES `user`(user_id),
    CONSTRAINT fk_borrow_book FOREIGN KEY (book_id) REFERENCES book(book_id),
    CONSTRAINT chk_borrow_status CHECK (status IN ('BORROWING', 'RETURNED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_user_role_id ON `user`(role_id);
CREATE INDEX idx_book_title ON book(title);
CREATE INDEX idx_book_is_available ON book(is_available);
CREATE INDEX idx_borrow_user_book ON borrow_record(user_id, book_id);

INSERT INTO role (role_name, description) VALUES
('ROLE_ADMIN', 'Administrator with full permissions'),
('ROLE_STUDENT', 'Student with read-only permissions');

INSERT INTO `user` (username, password, name, email, role_id) VALUES
('admin', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy71386', 'Admin', 'admin@library.com', 1),
('student1', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy71386', 'Student One', 'student1@library.com', 2),
('student2', '$2a$10$slYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy71386', 'Student Two', 'student2@library.com', 2);

INSERT INTO book (isbn, title, author, publisher, publish_date, category, price, stock, description, is_available) VALUES
('978-7-101-00771-9', 'Java Core Volume I', 'Horstmann', 'Electronic Industry Press', '2022-03-01', 'Programming', 89.00, 10, 'Classic Java programming textbook.', 1),
('978-7-121-02012-6', 'Spring in Action', 'Craig Walls', 'Electronic Industry Press', '2021-10-01', 'Framework', 79.00, 8, 'A practical guide to Spring.', 1),
('978-7-111-46245-0', 'Computer Networks', 'Andrew S. Tanenbaum', 'Electronic Industry Press', '2020-02-01', 'Computer Science', 99.00, 12, 'Classic networking textbook.', 1),
('978-7-111-34700-9', 'Operating System Concepts', 'William Stallings', 'Electronic Industry Press', '2019-08-01', 'Computer Science', 85.00, 6, 'Core concepts of operating systems.', 1),
('978-7-121-25753-1', 'MySQL Must Know', 'Benjamin Forta', 'Electronic Industry Press', '2020-05-01', 'Database', 59.00, 15, 'Quick start guide for MySQL.', 1);
