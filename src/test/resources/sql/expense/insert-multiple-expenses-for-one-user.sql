-- Add usuário.
INSERT INTO tb_user (name, email, password) VALUES
    ('John Doe', 'john.doe@email.com', '{bcrypt}$2a$10$g8ZNLct0Rcoyq2mExowkheD7GdQzwj/UNl7JvQnk.UiXnIFwt4be6');

-- Add role do usuário.
INSERT INTO tb_user_roles (user_id, roles_id) VALUES
    (1, 1);

-- Add as despesas (expenses) do usuário.
INSERT INTO tb_expense (description, value, date, category, user_id) VALUES
    ('Aluguel', 1500.00, '2023-02-05', 'MORADIA', 1),
    ('Supermercado', 325.00, '2023-02-06', 'ALIMENTACAO', 1),
    ('Mercado', 200.00, '2023-02-13', 'ALIMENTACAO', 1),
    ('MERCEARIA', 75.00, '2023-03-04', 'ALIMENTACAO', 1),
    ('Aluguel', 1500.00, '2023-03-05', 'MORADIA', 1);