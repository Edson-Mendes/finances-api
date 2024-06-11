## TODO

Lista com ideias que eu desejo analisar para adicionar ao sistema, e problemas que eu encontrei e não pude resolver no momento
ou que precisava de mais tempo para pensar.

### fix

- Não é possível deletar um usuário que tenha incomes/expenses através do endpoint DELETE /api/users/{ID}.
  Porque o user_id é chave extrangeira nas tabelas income e expense.
    - Pensar em como será feita essa exclusão de user e das linhas relacionadas a user.