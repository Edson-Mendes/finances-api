package br.com.emendes.financesapi.util;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacade {

  Authentication getAuthentication();

}
