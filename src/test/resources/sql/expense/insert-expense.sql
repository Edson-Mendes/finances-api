-- Add o usuário owner da expense.
INSERT INTO tb_user (name, email, password) VALUES
    ('Lorem Ipsum', 'lorem@email.com', '{bcrypt}$2a$10$H0Mbg9.ZehMCCUkLuweLOenMAgGWxrzr1I0DWf0gwVs/S8zsPwXO.');

INSERT INTO tb_user_roles (user_id, roles_id) VALUES
    (1, 1);

INSERT INTO tb_expense (description, value, date, category, user_id) VALUES
    ('Aluguel', 1500.00, '2022-02-05', 'MORADIA', 1),
    ('Supermercado', 225.00, '2022-02-13', 'ALIMENTACAO', 1);