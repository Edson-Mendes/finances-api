INSERT INTO user(name, email, password) VALUES ('Lorem ipsum', 'lorem@email.com', '$2a$10$VZD0YfOeyiGs9W/B62mrqeXUL.WonN.cIBVfNLdzHF.9nBEp9/vEe');
INSERT INTO user(name, email, password) VALUES ('Dolor Amet', 'dolor@email.com', '$2a$10$VZD0YfOeyiGs9W/B62mrqeXUL.WonN.cIBVfNLdzHF.9nBEp9/vEe');

INSERT INTO role(id, name) VALUES(1, 'ROLE_USER');

INSERT INTO user_roles(user_id, roles_id) VALUES(1, 1);
INSERT INTO user_roles(user_id, roles_id) VALUES(2, 1);

INSERT INTO expense(description, value, date, category) VALUES ('Internet', 100.00, '2022-01-05', 'MORADIA');
INSERT INTO expense(description, value, date, category) VALUES ('Água', 50.00, '2022-01-08', 'MORADIA');
INSERT INTO expense(description, value, date, category) VALUES ('Ônibus', 150.00, '2022-01-02', 'TRANSPORTE');
INSERT INTO expense(description, value, date, category) VALUES ('Supermercado', 500.00, '2022-01-20', 'ALIMENTACAO');

INSERT INTO expense(description, value, date, category) VALUES ('Internet', 101.00, '2022-02-05', 'MORADIA');
INSERT INTO expense(description, value, date, category) VALUES ('Água', 51.00, '2022-02-08', 'MORADIA');
INSERT INTO expense(description, value, date, category) VALUES ('Ônibus', 151.00, '2022-02-02', 'TRANSPORTE');
INSERT INTO expense(description, value, date, category) VALUES ('Supermercado', 501.00, '2022-02-20', 'ALIMENTACAO');

INSERT INTO income(description, value, date, user_id) VALUES ('Salário' , 2600.00, '2022-01-05', 1);
INSERT INTO income(description, value, date, user_id) VALUES ('Venda Celular Antigo' , 400.00, '2022-01-17', 1);
INSERT INTO income(description, value, date, user_id) VALUES ('Trabalho por fora' , 500.00, '2022-01-19', 1);

INSERT INTO income(description, value, date, user_id) VALUES ('Salário' , 2600.00, '2022-02-05', 1);
INSERT INTO income(description, value, date, user_id) VALUES ('Venda dos halteres' , 350.00, '2022-02-23', 1);

