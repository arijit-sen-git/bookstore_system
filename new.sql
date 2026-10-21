-- staff --

create table staff (
    staff_id int auto_increment primary key,
    staff_name varchar(255) not null,
    staff_phone varchar(10) unique,
    date_hired datetime default current_timestamp
) auto_increment = 1000;

-- customers --

create table customers (
    customer_phone varchar(10) primary key,
    customer_name varchar(255)
);

-- authors --

create table authors (
    author_id int auto_increment primary key,
    author_name varchar(255) not null
) auto_increment = 2000;

-- books --

create table books (
    book_id int auto_increment primary key,
    book_name varchar(255) not null,
	book_subject varchar(255) not null,
    book_price int,
    book_quantity int default 0,
    author_id int,
    foreign key (author_id) references authors(author_id)
) auto_increment = 3000;

-- transaction_histories --

create table transactions (
    transaction_id int auto_increment primary key,
    customer_phone varchar(10) not null,
    book_id int not null,
    staff_id int not null,
    quantity int not null,
    transaction_date datetime default current_timestamp,
    foreign key (customer_phone) references customers(customer_phone),
    foreign key (book_id) references books(book_id),
    foreign key (staff_id) references staff(staff_id)
);

-- Unique book subject constraint --

ALTER TABLE books
ADD CONSTRAINT unique_book_name_subject
UNIQUE (book_name, book_subject);
