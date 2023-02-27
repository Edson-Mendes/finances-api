INSERT INTO tb_user (name, email, password) VALUES
    ('Admin', 'admin@email.com', '{bcrypt}$2a$10$H0Mbg9.ZehMCCUkLuweLOenMAgGWxrzr1I0DWf0gwVs/S8zsPwXO.');

INSERT INTO tb_user_roles (user_id, roles_id) VALUES
    (1, 2);