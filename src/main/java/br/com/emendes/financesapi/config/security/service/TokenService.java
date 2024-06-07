package br.com.emendes.financesapi.config.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

/**
 * Interface service com as abstrações para manipular Json Web Token (JWT).
 */
public interface TokenService {

  /**
   * Gera um JWT a partir de um objeto Authentication.
   *
   * @param authentication Authentication a quem pertencerá o JWT.
   * @return String no padrão JWT.
   */
  String generateToken(Authentication authentication);

  /**
   * Verifica se um JWT é válido.
   *
   * @param token string contendo o JWT a ser validado.
   * @return true caso o jwt informado seja válido, false caso contrário.
   */
  boolean isTokenValid(String token);

  /**
   * Retorne o ID do usuário de quem o JWT pertence.
   *
   * @param token string que representa o JWT.
   * @return identificador do usuário que está no JWT.
   */
  Long getUserId(String token);

  /**
   * Recupera o JWT do objeto HttpServelRequest.
   *
   * @param request objeto que representa a requisição do cliente.
   * @return String que representa o JWT enviado na requisição.
   */
  String recoverToken(HttpServletRequest request);

}
