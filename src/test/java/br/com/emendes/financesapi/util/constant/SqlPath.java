package br.com.emendes.financesapi.util.constant;

public class SqlPath {

  private SqlPath() {}

  public static final String CREATE_DATABASE_TABLES_SQL_PATH = "/sql/database/create_database_tables.sql";
  public static final String DROP_DATABASE_TABLES_SQL_PATH = "/sql/database/drop_database_tables.sql";

  public static final String INSERT_ADMIN_SQL_PATH = "/sql/user/insert-admin.sql";
  public static final String INSERT_USER_SQL_PATH = "/sql/user/insert-user.sql";
  public static final String INSERT_INCOMES_EXPENSES_SQL_PATH = "/sql/summary/insert-incomes-and-expenses.sql";
  public static final String INSERT_INCOME_SQL_PATH = "/sql/income/insert-income.sql";
  public static final String INSERT_EXPENSE_SQL_PATH = "/sql/expense/insert-expense.sql";

}
