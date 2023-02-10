package br.com.emendes.financesapi.util.creator;

import br.com.emendes.financesapi.dto.request.SignupRequest;

public class SignupFormCreator {

  public static SignupRequest validSignupForm() {
    String name = "Ipsum Dolor";
    String email = "ipsum@email.com";
    String password = "123456";
    String confirm = "123456";
    return new SignupRequest(name, email, password, confirm);
  }

  public static SignupRequest withNameAndEmail(String name, String email){
    String password = "1234567890";
    String confirm = "1234567890";
    return new SignupRequest(name, email, password, confirm);
  }

}
