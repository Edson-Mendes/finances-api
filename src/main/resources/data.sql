INSERT INTO user(name, email, password) VALUES ('Lorem ipsum', 'lorem@email.com', '$2a$10$VZD0YfOeyiGs9W/B62mrqeXUL.WonN.cIBVfNLdzHF.9nBEp9/vEe');

INSERT INTO profile(id, name) VALUES(1, 'ROLE_USER');

INSERT INTO user_profiles(user_id, profiles_id) VALUES(1, 1);

INSERT INTO expense(description, value, date, category) VALUES ('Internet', 100.00, '2022-01-05', 'MORADIA');
INSERT INTO expense(description, value, date, category) VALUES ('Água', 50.00, '2022-01-08', 'MORADIA');
INSERT INTO expense(description, value, date, category) VALUES ('Ônibus', 150.00, '2022-01-02', 'TRANSPORTE');
INSERT INTO expense(description, value, date, category) VALUES ('Asun Supermercado', 500.00, '2022-01-20', 'ALIMENTACAO');

INSERT INTO expense(description, value, date, category) VALUES ('Internet', 101.00, '2022-02-05', 'MORADIA');
INSERT INTO expense(description, value, date, category) VALUES ('Água', 51.00, '2022-02-08', 'MORADIA');
INSERT INTO expense(description, value, date, category) VALUES ('Ônibus', 151.00, '2022-02-02', 'TRANSPORTE');
INSERT INTO expense(description, value, date, category) VALUES ('Asun Supermercado', 501.00, '2022-02-20', 'ALIMENTACAO');

INSERT INTO income(description, value, date) VALUES ('Lorem Dolor' , 100.00, '2022-01-16');
INSERT INTO income(description, value, date) VALUES ('Lorem Dolor' , 101.00, '2022-02-17');
INSERT INTO income(description, value, date) VALUES ('Lorem Ipsum' , 200.00, '2022-01-16');
INSERT INTO income(description, value, date) VALUES ('Lorem Ipsum' , 201.00, '2022-02-17');
INSERT INTO income(description, value, date) VALUES ('Lorem Amet' , 300.00, '2022-01-16');
INSERT INTO income(description, value, date) VALUES ('Lorem Amet' , 301.00, '2022-02-17');
INSERT INTO income(description, value, date) VALUES ('Lorem' , 400.00, '2022-01-16');
INSERT INTO income(description, value, date) VALUES ('Lorem' , 401.00, '2022-02-17');
INSERT INTO income(description, value, date) VALUES ('Ipsum Lorem' , 500.00, '2022-01-16');
INSERT INTO income(description, value, date) VALUES ('Ipsum Lorem' , 501.00, '2022-02-17');
INSERT INTO income(description, value, date) VALUES ('Dolor Lorem' , 600.00, '2022-01-16');
INSERT INTO income(description, value, date) VALUES ('Dolor Lorem' , 601.00, '2022-02-17');
INSERT INTO income(description, value, date) VALUES ('Amet Lorem' , 700.00, '2022-01-16');
INSERT INTO income(description, value, date) VALUES ('Amet Lorem' , 701.00, '2022-02-17');

