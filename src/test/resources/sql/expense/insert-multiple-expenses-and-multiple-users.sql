-- Add os usuários.
INSERT INTO tb_user (name, email, password) VALUES
    ('John Doe', 'john.doe@email.com', '{bcrypt}$2a$10$g8ZNLct0Rcoyq2mExowkheD7GdQzwj/UNl7JvQnk.UiXnIFwt4be6'),
    ('Jane Doe', 'jane.doe@email.com', '{bcrypt}$2a$10$g8ZNLct0Rcoyq2mExowkheD7GdQzwj/UNl7JvQnk.UiXnIFwt4be6');

-- Add roles dos usuários.
INSERT INTO tb_user_roles (user_id, roles_id) VALUES
    (1, 1),
    (2, 1);

-- Add as despesas (expenses) dos usuários.
INSERT INTO tb_expense (description, value, date, category, user_id) VALUES
    ('Aluguel', 1500.00, '2023-02-05', 'MORADIA', 1),
    ('Supermercado', 325.00, '2023-02-13', 'ALIMENTACAO', 1),
    ('Aluguel', 1200.00, '2023-02-05', 'MORADIA', 2),
    ('Supermercado', 175.00, '2023-02-13', 'ALIMENTACAO', 2);
