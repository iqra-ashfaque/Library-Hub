CREATE TABLE Authors (
    author_id INT PRIMARY KEY AUTO_INCREMENT,
    author_name VARCHAR(100) NOT NULL
);
CREATE TABLE Books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    author_id INT,
    quantity INT DEFAULT 1,
    image_path varchar(255),
    FOREIGN KEY (author_id) REFERENCES Authors(author_id)
);

ALTER TABLE Books
ADD COLUMN image_path VARCHAR(255);
use librarydb; 
   DESCRIBE Books;
  ALTER TABLE Books ADD COLUMN cover_path VARCHAR(255);
  USE librarydb;
ALTER TABLE Books CHANGE COLUMN image_path cover_path VARCHAR(255);
DESCRIBE Books;
select*from books;
ALTER TABLE Books
DROP COLUMN category;
CREATE TABLE Students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20)
);
CREATE TABLE Issue_Return (
    issue_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    book_id INT,
    issue_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) DEFAULT 'Issued',

    FOREIGN KEY (student_id) REFERENCES Students(student_id),
    FOREIGN KEY (book_id) REFERENCES Books(book_id)
);
Show tables;
DESCRIBE Authors;
DESCRIBE Books;
DESCRIBE Students;
DESCRIBE Issue_Return;
INSERT INTO Authors (author_name)
VALUES
('J.K. Rowling'),
('George Orwell'),
('Jane Austen'),
('Mark Twain'),
('William Shakespeare');
select*from Authors;
INSERT INTO Books (title, author_id, quantity, image_path)
VALUES
('Harry Potter and the Philosopher''s Stone', 1, 5, 'harry_potter.jpg'),
('1984', 2, 3, '1984.jpg'),
('Pride and Prejudice', 3, 4, 'pride_prejudice.jpg'),
('The Adventures of Tom Sawyer', 4, 2, 'tom_sawyer.jpg'),
('Hamlet', 5, 3, 'hamlet.jpg'),
('Animal Farm', 2, 4, 'animal_farm.jpg'),
('Emma', 3, 2, 'emma.jpg'),
('Romeo and Juliet', 5, 5, 'romeo_juliet.jpg');
select * from Books;
INSERT INTO Students (name, email, phone)
VALUES
('Ayesha Khan', 'ayesha@gmail.com', '03001234567'),
('Ali Ahmed', 'ali@gmail.com', '03111234567'),
('Sara Malik', 'sara@gmail.com', '03221234567'),
('Hamza Raza', 'hamza@gmail.com', '03331234567'),
('Zainab Noor', 'zainab@gmail.com', '03441234567');
select * from Students;
INSERT INTO Issue_Return
(student_id, book_id, issue_date, return_date, status)
VALUES
(1, 1, '2026-08-01', NULL, 'Issued'),
(2, 2, '2026-08-02', '2026-08-05', 'Returned'),
(3, 3, '2026-08-03', NULL, 'Issued'),
(4, 5, '2026-08-04', NULL, 'Issued'),
(5, 4, '2026-08-05', '2026-08-07', 'Returned');
SELECT * FROM Issue_Return;
SELECT 
    Students.name,
    Books.title,
    Issue_Return.issue_date,
    Issue_Return.return_date,
    Issue_Return.status
FROM Issue_Return
JOIN Students 
    ON Issue_Return.student_id = Students.student_id
JOIN Books 
    ON Issue_Return.book_id = Books.book_id;
    SELECT 
    Students.name,
    Books.title,
    Issue_Return.issue_date,
    Issue_Return.return_date,
    Issue_Return.status
FROM Issue_Return
JOIN Students
    ON Issue_Return.student_id = Students.student_id
JOIN Books
    ON Issue_Return.book_id = Books.book_id
WHERE Students.student_id = 2;
SELECT
Books.title,
Authors.author_name,
Books.quantity,
Books.image_path
FROM Books
JOIN Authors
ON Books.author_id = Authors.author_id;
    SELECT  #issued books
    Students.name,
    Books.title,
    Issue_Return.issue_date,
    Issue_Return.status
FROM Issue_Return
JOIN Students
    ON Issue_Return.student_id = Students.student_id
JOIN Books
    ON Issue_Return.book_id = Books.book_id
WHERE Issue_Return.status = 'Issued';
SELECT   #returned books
    Students.name,
    Books.title,
    Issue_Return.issue_date,
    Issue_Return.return_date
FROM Issue_Return
JOIN Students
    ON Issue_Return.student_id = Students.student_id
JOIN Books
    ON Issue_Return.book_id = Books.book_id
WHERE Issue_Return.status = 'Returned';
SELECT *
FROM Books
WHERE title LIKE '%Harry%';
SELECT COUNT(*) AS total_students
FROM Students;
SELECT SUM(quantity) AS total_books #total books
FROM Books;
describe books;

 