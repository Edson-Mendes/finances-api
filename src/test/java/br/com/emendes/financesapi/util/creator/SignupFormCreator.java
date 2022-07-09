package br.com.emendes.financesapi.util.creator;

import br.com.emendes.financesapi.controller.form.SignupForm;

public class SignupFormCreator {

  public static SignupForm validSignupForm() {
    String name = "Ipsum Dolor";
    String email = "ipsum@email.com";
    String password = "123456";
    String confirm = "123456";
    return new SignupForm(name, email, password, confirm);
  }

  public static SignupForm withNameAndEmail(String name, String email){
    String password = "1234567890";
    String confirm = "1234567890";
    return new SignupForm(name, email, password, confirm);
  }

}
