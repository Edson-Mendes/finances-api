package br.com.emendes.financesapi.creator;

import br.com.emendes.financesapi.controller.form.LoginForm;

public class LoginFormCreator {

  public static LoginForm validLoginForm() {
    String email = "lorem@email.com";
    String password = "123456789O";

    return new LoginForm(email, password);
  }

}
