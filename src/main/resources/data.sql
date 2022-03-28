INSERT INTO user(name, email, password) VALUES ('User Admin', 'admin@email.com', '$2a$10$VZD0YfOeyiGs9W/B62mrqeXUL.WonN.cIBVfNLdzHF.9nBEp9/vEe');
-- INSERT INTO user(name, email, password) VALUES ('User Common', 'user@email.com', '$2a$10$VZD0YfOeyiGs9W/B62mrqeXUL.WonN.cIBVfNLdzHF.9nBEp9/vEe');

INSERT INTO role(id, name) VALUES(1, 'ROLE_USER');
INSERT INTO role(id, name) VALUES(2, 'ROLE_ADMIN');

INSERT INTO user_roles(user_id, roles_id) VALUES(1, 1);
INSERT INTO user_roles(user_id, roles_id) VALUES(1, 2);
-- INSERT INTO user_roles(user_id, roles_id) VALUES(2, 1);

-- INSERT INTO expense(description, value, date, category, user_id) VALUES ('Internet', 100.00, '2022-01-05', 'MORADIA', 2);
-- INSERT INTO expense(description, value, date, category, user_id) VALUES ('Água', 50.00, '2022-01-08', 'MORADIA', 2);
-- INSERT INTO expense(description, value, date, category, user_id) VALUES ('Ônibus', 150.00, '2022-01-02', 'TRANSPORTE', 2);
-- INSERT INTO expense(description, value, date, category, user_id) VALUES ('Supermercado', 500.00, '2022-01-20', 'ALIMENTACAO', 2);
-- INSERT INTO expense(description, value, date, category, user_id) VALUES ('Internet', 101.00, '2022-02-05', 'MORADIA', 2);
-- INSERT INTO expense(description, value, date, category, user_id) VALUES ('Água', 51.00, '2022-02-08', 'MORADIA', 2);
-- INSERT INTO expense(description, value, date, category, user_id) VALUES ('Ônibus', 151.00, '2022-02-02', 'TRANSPORTE', 2);
-- INSERT INTO expense(description, value, date, category, user_id) VALUES ('Supermercado', 501.00, '2022-02-20', 'ALIMENTACAO', 2);

-- INSERT INTO income(description, value, date, user_id) VALUES ('Salário' , 2600.00, '2022-01-05', 2);
-- INSERT INTO income(description, value, date, user_id) VALUES ('Venda Celular Antigo' , 400.00, '2022-01-17', 2);
-- INSERT INTO income(description, value, date, user_id) VALUES ('Trabalho por fora' , 500.00, '2022-01-19', 2);

-- INSERT INTO income(description, value, date, user_id) VALUES ('Salário' , 2600.00, '2022-02-05', 2);
-- INSERT INTO income(description, value, date, user_id) VALUES ('Venda dos halteres' , 350.00, '2022-02-23', 2);

-- INSERT INTO income(description, value, date, user_id) VALUES ('Salário', 4200.00, '2022-01-08', 2);
-- INSERT INTO income(description, value, date, user_id) VALUES ('Venda do Ipod', 350.00, '2022-01-21', 2);

-- INSERT INTO income(description, value, date, user_id) VALUES ('Salário', 4400.00, '2022-02-08', 2);

-- INSERT INTO income(description, value, date, user_id) VALUES ('Salário', 4050.00, '2022-03-08', 2);

