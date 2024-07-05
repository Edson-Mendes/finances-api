package br.com.emendes.financesapi.mapper.impl;

import br.com.emendes.financesapi.dto.request.SignupRequest;
import br.com.emendes.financesapi.dto.response.UserResponse;
import br.com.emendes.financesapi.mapper.UserMapper;
import br.com.emendes.financesapi.model.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Implementação de {@link UserMapper}.
 */
@Component
public class UserMapperImpl implements UserMapper {

  @Override
  public User toUser(SignupRequest signupRequest) {
    Assert.notNull(signupRequest, "signupRequest must not be null.");

    return User.builder()
        .name(signupRequest.getName())
        .email(signupRequest.getEmail())
        .password(signupRequest.getPassword())
        .build();
  }

  @Override
  public UserResponse toUserResponse(User user) {
    Assert.notNull(user, "user must not be null.");

    return UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .build();
  }

}
