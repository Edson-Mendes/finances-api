package br.com.emendes.financesapi.util.constant;

public class SqlPath {

  private SqlPath() {}

  public static final String CREATE_DATABASE_TABLES_SQL_PATH = "/sql/database/create_database_tables.sql";
  public static final String DROP_DATABASE_TABLES_SQL_PATH = "/sql/database/drop_database_tables.sql";

  public static final String INSERT_ADMIN_SQL_PATH = "/sql/user/insert-admin.sql";
  /**
   * Path para um arquivo que insere um usuário com a role 'USER'.
   */
  public static final String INSERT_USER_SQL_PATH = "/sql/user/insert-user.sql";
  public static final String INSERT_INCOMES_EXPENSES_SQL_PATH = "/sql/summary/insert-incomes-and-expenses.sql";
  public static final String INSERT_INCOME_SQL_PATH = "/sql/income/insert-income.sql";

  /**
   * Path para um arquivo SQL que insere dois usuários e 2 (duas) despesas (expenses) para cada usuário.
   */
  public static final String INSERT_MULTIPLE_EXPENSES_AND_MULTIPLE_USERS = "/sql/expense/insert-multiple-expenses-and-multiple-users.sql";

  /**
   * Path para um arquivo SQL que insere um usuário e 5 (cinco) despesas (expenses) para o usuário.
   */
  public static final String INSERT_MULTIPLE_EXPENSES_FOR_ONE_USER = "/sql/expense/insert-multiple-expenses-for-one-user.sql";
  public static final String INSERT_EXPENSE_SQL_PATH = "/sql/expense/insert-expense.sql";

}
