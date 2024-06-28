-- Add os usuários.
INSERT INTO tb_user (name, email, password) VALUES
    ('John Doe', 'john.doe@email.com', '{bcrypt}$2a$10$g8ZNLct0Rcoyq2mExowkheD7GdQzwj/UNl7JvQnk.UiXnIFwt4be6'),
    ('Jane Doe', 'jane.doe@email.com', '{bcrypt}$2a$10$g8ZNLct0Rcoyq2mExowkheD7GdQzwj/UNl7JvQnk.UiXnIFwt4be6');

-- Add roles dos usuários.
INSERT INTO tb_user_roles (user_id, roles_id) VALUES
    (1, 1),
    (2, 1);

-- Add as receitas (incomes) dos usuários.
INSERT INTO tb_income (description, value, date, user_id) VALUES
    ('Salário', 3500.00, '2023-02-08', 1),
    ('Freela', 325.00, '2023-02-13', 1),
    ('Salário', 3700.00, '2023-02-05', 2),
    ('Venda Video Game', 975.00, '2023-02-13', 2);
