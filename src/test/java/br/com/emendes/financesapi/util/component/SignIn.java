package br.com.emendes.financesapi.util.component;

import br.com.emendes.financesapi.dto.request.SignInRequest;
import br.com.emendes.financesapi.dto.response.TokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Profile(value = {"test", "integration"})
public class SignIn {

  @Autowired
  private TestRestTemplate testRestTemplate;

  /**
   * Realiza Sign In e devolve HttpHeaders com header authorization.
   * @param email do usuário
   * @param password do usuário
   * @return HttpHeaders com header authorization.
   */
  public HttpHeaders generateAuthorizationHeader(String email, String password) {
    HttpHeaders headers = new HttpHeaders();

    headers.setBearerAuth(signIn(email, password));

    return headers;
  }

  /**
   * Realiza Sign In e devolve o bearer token de autorização.
   * @param email do usuário
   * @param password do usuário
   * @return bearer token que deve ser adicinodo ao header authorization.
   */
  public String signIn(String email, String password) {
    HttpEntity<SignInRequest> requestBody = new HttpEntity<>(new SignInRequest(email, password));

    ResponseEntity<TokenResponse> actualResponse = testRestTemplate.exchange(
        "/api/auth/signin", HttpMethod.POST, requestBody, new ParameterizedTypeReference<>() {
        });

    TokenResponse body = actualResponse.getBody();

    if (body == null) {
      throw new IllegalArgumentException("Invalid email or password");
    }

    return body.getToken();
  }

}
