package br.com.emendes.financesapi.creator;

import br.com.emendes.financesapi.model.User;

public class UserCreator {

  public static User validUserForExpenseRepositoryTest() {
    String name = "Dolor Sit";
    String email = "dolor@email.com";
    String password = "123456";

    User user = new User(name, email, password);

    return user;
  }

  public static User validUserForIncomeRepositoryTest() {
    String name = "Lorem Ipsum";
    String email = "lorem@email.com";
    String password = "123456";

    return new User(name, email, password);
  }

  public static User validUserForUserRepositoryTest() {
    String name = "Amet xpto";
    String email = "amet@email.com";
    String password = "123456";

    return new User(name, email, password);
  }

}
