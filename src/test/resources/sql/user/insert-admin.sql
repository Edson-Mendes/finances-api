INSERT INTO tb_user (name, email, password) VALUES
    ('Admin', 'admin@email.com', '{bcrypt}$2a$10$g8ZNLct0Rcoyq2mExowkheD7GdQzwj/UNl7JvQnk.UiXnIFwt4be6');

INSERT INTO tb_user_roles (user_id, roles_id) VALUES
    (1, 2);