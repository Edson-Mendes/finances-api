CREATE TABLE tb_user (
    id bigserial NOT NULL,
    name varchar(100) NOT NULL,
    email varchar(150) NOT NULL,
    password varchar(255) NOT NULL,
    CONSTRAINT tb_user_pk PRIMARY KEY (id),
    CONSTRAINT tb_user__f_email_unique UNIQUE (email)
);