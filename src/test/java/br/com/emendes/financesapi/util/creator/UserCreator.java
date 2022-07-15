package br.com.emendes.financesapi.util.creator;

import br.com.emendes.financesapi.model.Role;
import br.com.emendes.financesapi.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

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

  // TODO: Talvez não seja usado
  public static User validUserForExpenseServiceTest() {
    String name = "Amet xpto";
    String email = "ametxpto@email.com";
    String password = "123456";

    return new User(name, email, password);
  }

  public static User userToBeSavedForUserServiceTests() {
    String name = "Ipsum Dolor";
    String email = "ipsum@email.com";
    String password = "123456";

    return new User(name, email, password);
  }

  public static User userSavedForUserServiceTests() {
    String name = "Ipsum Dolor";
    String email = "ipsum@email.com";
    String password = "123456";

    User user = new User(name, email, password);
    user.setId(10000L);

    return user;
  }

  public static User userWithIdAndRoles(){
    Long id = 100L;
    String name = null;
    String email = null;
    String password = null;
    List<Role> roles = List.of(RoleCreator.userRole());
    return new User(id, name, email, password, roles);
  }

  public static User userWithPasswordEncoded() {
    User user = userSavedForUserServiceTests();
    String passwordEncoded = new BCryptPasswordEncoder().encode(user.getPassword());
    user.setPassword(passwordEncoded);
    return user;
  }
}
