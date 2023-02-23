-- Add o usuário owner da expense.
INSERT INTO tb_user (name, email, password) VALUES
    ('Lorem Ipsum', 'lorem@email.com', '{bcrypt}$2a$10$H0Mbg9.ZehMCCUkLuweLOenMAgGWxrzr1I0DWf0gwVs/S8zsPwXO.');

INSERT INTO tb_user_roles (user_id, roles_id) VALUES
    (1, 1);

INSERT INTO tb_expense (description, value, date, category, user_id) VALUES
    ('Aluguel', 1500.00, '2023-02-05', 'MORADIA', 1),
    ('Supermercado', 225.00, '2023-02-13', 'ALIMENTACAO', 1),
    ('Internet', 100.00, '2023-02-03', 'MORADIA', 1),
    ('Luz', 200.00, '2023-02-06', 'MORADIA', 1),
    ('Condomínio', 150.00, '2023-02-04', 'MORADIA', 1),
    ('Supermercado', 300.00, '2023-02-23', 'ALIMENTACAO', 1),
    ('Ônibus', 400.00, '2023-02-28', 'TRANSPORTE', 1),
    ('Farmácia', 80.00, '2023-02-16', 'SAUDE', 1),
    ('Curso Online', 100.00, '2023-02-23', 'EDUCACAO', 1);

INSERT INTO tb_income (description, value, date, user_id) VALUES
    ('Salário', 3500.00, '2023-02-08', 1);