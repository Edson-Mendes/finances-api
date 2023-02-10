package br.com.emendes.financesapi.util.creator;

import br.com.emendes.financesapi.dto.request.SignInRequest;

public class LoginFormCreator {

  public static SignInRequest validLoginForm() {
    String email = "lorem@email.com";
    String password = "123456789O";

    return new SignInRequest(email, password);
  }

}
