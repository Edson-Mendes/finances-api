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