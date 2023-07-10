<h1 align="center"> Finances API </h1>

![Badge Concluído](https://img.shields.io/static/v1?label=Status&message=Concluído&color=success&style=for-the-badge)
![Badge Java](https://img.shields.io/static/v1?label=Java&message=17&color=orange&style=for-the-badge&logo=java)
![Badge Spring](https://img.shields.io/static/v1?label=Springboot&message=v2.6.3&color=brightgreen&style=for-the-badge&logo=springboot)
![Badge Maven](https://img.shields.io/static/v1?label=Maven&message=v3.8.4&color=critical&style=for-the-badge&logo=apache+maven)
![Badge JUnit5](https://img.shields.io/static/v1?label=JUnit5&message=v5.8.2&color=green&style=for-the-badge&logo=junit5)
![Badge Postgres](https://img.shields.io/static/v1?label=PostgreSQL&message=v14.4&color=blue&style=for-the-badge&logo=PostgreSQL)
![Badge Heroku](https://img.shields.io/static/v1?label=Heroku&message=Deploy&color=4f3074&style=for-the-badge&logo=Heroku)

## :book: Resumo do projeto
Finances API é uma REST API para controle de orçamento familiar que visa auxiliar o usuário a gerenciar 
suas receitas e despesas de forma clara e segura.

A aplicação possui endpoints para gerenciar e manipular os recursos Usuário (User), Receitas (Income), Despesas (Expense),
Autenticação (Auth), Resumos (Summary), Categorias (Category), que são protegidos e 
requerem autenticação por JWT (Json Web Token) para serem manipulados.

O projeto foi proposto pela Alura no Challenge Backend 2ª edição.
## :hammer: Funcionalidades
- `Autenticação de usuário`
  - `Cadastrar usuário`: Cadastro de usuários através de um **POST /api/auth/signup** com as informações de nome, email, senha e confirmação de senha.
  - `Logar usuário`: Login de usuários através de um **POST /api/auth/signin** com as informações de autenticação do usuário (email e senha), 
  devolve um token JWT do tipo Bearer, que deve ser enviado no header authorization em cada nova requisição para as outras funcionalidades.</br>

- `Receitas`
  - `Cadastrar receita`: Cadastro de receitas através de um **POST /api/incomes** com as informações da receita a ser salva (descrição, data e valor).
  - `Buscar receitas`: Busca as receitas do usuário através de um **GET /api/incomes**, opcional buscar por descrição.
  - `Buscar receita por id`: Busca a receita do usuário por id através de um **GET /api/incomes**{id}*.
  - `Buscar receitas por ano e mês`: Busca as receitas do usuário em um dado ano e mês, através de um **GET /api/incomes**{ano}/{mês}*.
  - `Atualizar receita`: Atualizar uma receita do usuário através de um **PUT /api/incomes**{id}*.
  - `Deletar receita`: Deletar uma receita do usuário através de um **DELETE /api/incomes**{id}*</br>

- `Despesas`
  - `Cadastrar despesa`: Cadastro de despesas através de um **POST /api/expenses** com as informações da despesa a ser salva (descrição, data, valor e categoria), o campo categoria é opcional, as categorias até o momemto são: ALIMENTACAO, SAUDE, MORADIA, TRANSPORTE, EDUCACAO, LAZER, IMPREVISTOS, OUTRAS.
  - `Buscar despesas`: Busca as despesas do usuário através de um **GET /api/expenses**, opcional buscar por descrição.
  - `Buscar despesa por id`: Busca a despesa do usuário por id através de um **GET para /api/expenses/{valor do id}**.
  - `Buscar despesas por ano e mês`: Busca as despesas do usuário em um dado ano e mês, através de um **GET /api/expenses/{ano}/{mês}**.
  - `Atualizar despesa`: Atualizar uma despesa do usuário através de um **PUT /api/expenses/{id}**.
  - `Deletar despesa`: Deletar uma despesa do usuário através de um **DELETE /api/expenses/{id}**</br>

- `Categorias`
  - `Buscar categorias`: Busca de categorias através de um **GET /api/categories**.</br>

- `Resumo`
  - `Resumo de mês`: Através de um **GET /api/summaries/{ano}/{mês}** o usuário tem acesso ao total de receitas e despesas de um dado ano e mês, 
  assim como o saldo do mês e o total de despesas por categoria.</br>

- `Usuário`
  - `Atualizar senha`: Através de um **POST /api/users/password** o usuário pode alterar sua senha.
  - `Buscar usuários`: Através de um **GET /api/users** o *admin* pode buscar todos os usuários cadastrados, usuários comuns não possuem permissão de uso dessa funcionalidade.
  - `Deletar usuário`: Através de um **DELETE /api/users/{id}** o admin pode deletar um usuário por *id*.</br>

## :toolbox: Tecnologias
<a href="https://www.jetbrains.com/idea/" target="_blank"><img src="https://img.shields.io/badge/intellij-000000.svg?&style=for-the-badge&logo=intellijidea&logoColor=white" target="_blank"></a>

<a href="https://pt.wikipedia.org/wiki/Java_(linguagem_de_programa%C3%A7%C3%A3o)" target="_blank"><img src="https://img.shields.io/badge/java%2017-D32323.svg?&style=for-the-badge&logo=java&logoColor=white" target="_blank"></a>

<a href="https://spring.io/projects/spring-boot" target="_blank"><img src="https://img.shields.io/badge/Springboot-6db33f.svg?&style=for-the-badge&logo=springboot&logoColor=white" target="_blank"></a>
<a href="https://spring.io/projects/spring-data-jpa" target="_blank"><img src="https://img.shields.io/badge/Spring%20Data%20JPA-6db33f.svg?&style=for-the-badge&logo=spring&logoColor=white" target="_blank"></a>
<a href="https://spring.io/projects/spring-security" target="_blank"><img src="https://img.shields.io/badge/Spring%20Security-6db33f.svg?&style=for-the-badge&logo=spring&logoColor=white" target="_blank"></a>

<a href="https://maven.apache.org/" target="_blank"><img src="https://img.shields.io/badge/Apache%20Maven-b8062e.svg?&style=for-the-badge&logo=apachemaven&logoColor=white" target="_blank"></a>

<a href="https://tomcat.apache.org/" target="_blank"><img src="https://img.shields.io/badge/Apache%20Tomcat-F8DC75.svg?&style=for-the-badge&logo=apachetomcat&logoColor=black" target="_blank"></a>

<a href="https://www.docker.com/" target="_blank"><img src="https://img.shields.io/badge/Docker-2496ED.svg?&style=for-the-badge&logo=docker&logoColor=white" target="_blank"></a>
<a href="https://www.postgresql.org/" target="_blank"><img src="https://img.shields.io/badge/PostgreSQL-4169E1.svg?&style=for-the-badge&logo=postgresql&logoColor=white" target="_blank"></a>
<a href="https://flywaydb.org/" target="_blank"><img src="https://img.shields.io/badge/Flyway-CC0200.svg?&style=for-the-badge&logo=flyway&logoColor=white" target="_blank"></a>

<a href="https://junit.org/junit5/" target="_blank"><img src="https://img.shields.io/badge/JUnit%205-25A162.svg?&style=for-the-badge&logo=junit5&logoColor=white" target="_blank"></a>
<a href="https://site.mockito.org/" target="_blank"><img src="https://img.shields.io/badge/Mockito-C5D9C8.svg?&style=for-the-badge" target="_blank"></a>
<a href="https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/web/client/TestRestTemplate.html" target="_blank"><img src="https://img.shields.io/badge/TestRestTemplate-6db33f.svg?&style=for-the-badge" target="_blank"></a>
<a href="https://www.testcontainers.org/" target="_blank"><img src="https://img.shields.io/badge/TestContainers-291A3F.svg?&style=for-the-badge&logo=testcontainers&logoColor=white" target="_blank"></a>
<a href="https://www.postman.com/" target="_blank"><img src="https://img.shields.io/badge/postman-ff6c37.svg?&style=for-the-badge&logo=postman&logoColor=white" target="_blank"></a>

<a href="https://swagger.io/" target="_blank"><img src="https://img.shields.io/badge/Swagger-85EA2D.svg?&style=for-the-badge&logo=swagger&logoColor=black" target="_blank"></a>
<a href="https://springdoc.org/" target="_blank"><img src="https://img.shields.io/badge/Spring%20Doc-85EA2D.svg?&style=for-the-badge" target="_blank"></a>

<a href="https://projectlombok.org/" target="_blank"><img src="https://img.shields.io/badge/Lombok-a4a4a4.svg?&style=for-the-badge&logo=lombok&logoColor=black" target="_blank"></a>
<a href="https://github.com/jwtk/jjwt" target="_blank"><img src="https://img.shields.io/badge/JJWT-a4a4a4.svg?&style=for-the-badge&logo=JJWT&logoColor=black" target="_blank"></a>

<a href="https://en.wikipedia.org/wiki/Unit_testing" target="_blank"><img src="https://img.shields.io/badge/Unit%20Tests-5a61d6.svg?&style=for-the-badge&logo=unittest&logoColor=white" target="_blank"></a>
<a href="https://en.wikipedia.org/wiki/Integration_testing" target="_blank"><img src="https://img.shields.io/badge/Integration%20Tests-5a61d6.svg?&style=for-the-badge&logo=unittest&logoColor=white" target="_blank"></a>

<a href="https://www.heroku.com/home" target="_blank"><img src="https://img.shields.io/badge/Heroku-430098.svg?&style=for-the-badge&logo=heroku&logoColor=white" target="_blank"></a>

## :hammer_and_wrench: Deploy
O deploy da aplicação foi realizado no *Heroku*, você pode testar/brincar/usar [aqui](https://apifinances.herokuapp.com/swagger-ui.html)

OBS: As aplicações que usam conta gratuita do heroku *adormecem* se ficarem inativas, então pode ser que a primeira requisição demore um pouco (até uns 60 segundos), apenas seja paciente :wink:.

## :card_file_box: Documentação
Documentação feita com Springdoc openapi, gerando [Swagger UI](https://apifinances.herokuapp.com/swagger-ui.html) e [Api docs](https://apifinances.herokuapp.com/api-docs)

## :gear: Atualizações futuras
- [x] Troca do banco de dados H2 para outro (MySQL, PostgreSQL, ou outro)
- [ ] Mais relatórios, tipo relatório anual, ou por categoria em dados mês e ano.
- [ ] Adicionar algum recurso para monitorar e gerenciar a aplicação, tipo o Actuator
- [x] Endpoint para listar todas as categorias.
- [x] Remoção da regra de negócio que **não permitia** receitas/despesas com mesmo **DESCRIÇÃO** no mesmo **MÊS** e **ANO**.
- [ ] Atualizar versão do Spring boot (2.7.* ou 3.0.*)