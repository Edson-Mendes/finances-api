-- Create table User
CREATE TABLE tb_user (
    id bigserial NOT NULL,
    name varchar(100) NOT NULL,
    email varchar(150) NOT NULL CONSTRAINT tb_user__f_email_unique UNIQUE,
    password varchar(255) NOT NULL,
    CONSTRAINT tb_user_pk PRIMARY KEY (id)
);

-- Create table Expense
CREATE TABLE tb_expense (
    id bigserial NOT NULL,
    description  varchar(255) NOT NULL,
    value numeric(8, 2) NOT NULL,
    date date NOT NULL,
    category varchar(25) NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT tb_expense_pk PRIMARY KEY (id),
    CONSTRAINT f_user_id_fk_tb_expense FOREIGN KEY (user_id) REFERENCES tb_user(id)
);

-- Create table Income
CREATE TABLE tb_income (
    id bigserial NOT NULL,
    description  varchar(255) NOT NULL,
    value numeric(8, 2) NOT NULL,
    date date NOT NULL,
    user_id bigint NOT NULL,
    CONSTRAINT tb_income_pk PRIMARY KEY (id),
    CONSTRAINT f_user_id_fk_tb_income FOREIGN KEY (user_id) REFERENCES tb_user(id)
);

-- Create table Role
CREATE TABLE tb_role (
    id integer NOT NULL,
    name varchar(50) NOT NULL,
    CONSTRAINT tb_role_pk PRIMARY KEY (id)
);

-- Create table User_Roles
CREATE TABLE tb_user_roles (
    user_id bigint NOT NULL,
    roles_id integer NOT NULL,
    CONSTRAINT f_user_id_fk_tb_user_roles FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    CONSTRAINT f_roles_id_fk_tb_user_roles FOREIGN KEY (roles_id) REFERENCES tb_role(id)
);

-- Insert Roles USER e ADMIN
INSERT INTO tb_role(id, name) VALUES(1, 'ROLE_USER');
INSERT INTO tb_role(id, name) VALUES(2, 'ROLE_ADMIN');

-- Enable function unaccent
CREATE EXTENSION IF NOT EXISTS unaccent;