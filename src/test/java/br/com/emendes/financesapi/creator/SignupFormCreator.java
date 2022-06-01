package br.com.emendes.financesapi.creator;

import br.com.emendes.financesapi.controller.form.SignupForm;

public class SignupFormCreator {

  public static SignupForm validSignupForm() {
    String name = "Ipsum Dolor";
    String email = "ipsum@email.com";
    String password = "123456";
    String confirm = "123456";
    return new SignupForm(name, email, password, confirm);
  }

}
