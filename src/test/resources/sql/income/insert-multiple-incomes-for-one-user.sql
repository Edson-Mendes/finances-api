-- Add usuário.
INSERT INTO tb_user (name, email, password) VALUES
    ('John Doe', 'john.doe@email.com', '{bcrypt}$2a$10$g8ZNLct0Rcoyq2mExowkheD7GdQzwj/UNl7JvQnk.UiXnIFwt4be6');

-- Add role do usuário.
INSERT INTO tb_user_roles (user_id, roles_id) VALUES
    (1, 1);

-- Add as receitas (incomes) do usuário.
INSERT INTO tb_income (description, value, date, user_id) VALUES
    ('Salário', 3500.00, '2023-02-05', 1),
    ('Freela', 325.00, '2023-02-06', 1),
    ('Hora extra', 200.00, '2023-02-13', 1),
    ('Salário', 3400.00, '2023-03-04', 1),
    ('Hora extra', 1500.00, '2023-03-05', 1);