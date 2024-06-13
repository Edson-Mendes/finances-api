package br.com.emendes.financesapi.util.constant;

/**
 * Classe para manter constantes de autenticação (email e password) usados em testes automatizados.
 */
public class AuthenticationConstant {

  private AuthenticationConstant() {
  }

  /**
   * username para autenticação em testes com role ROLE_USER.
   */
  public static final String USER_EMAIL = "john.doe@email.com";
  public static final String USER_PASSWORD = "1234567890";
  /**
   * username para autenticação em testes com role ROLE_ADMIN.
   */
  public static final String ADMIN_EMAIL = "admin@email.com";
  public static final String ADMIN_PASSWORD = "1234567890";
}
