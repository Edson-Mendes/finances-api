package br.com.emendes.financesapi.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.web.servlet.MvcResult;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.config.validation.error_dto.FormErrorDto;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.UserDto;

public abstract class DtoFromMvcResult {
  
  public static FormErrorDto formErrorDtoFromMvcResult(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());
    JsonNode errorJsonNode = content.elements().next();
    String field = errorJsonNode.get("field").asText();
    String error = errorJsonNode.get("error").asText();

    FormErrorDto formErrorDto = new FormErrorDto(field, error);

    return formErrorDto;
  }

  public static UserDto userDtoFromMvcResult(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());

    Long id = content.get("id").asLong();
    String name = content.get("name").asText();
    String email = content.get("email").asText();

    UserDto user = new UserDto(id, name, email);
    return user;
  }

  public static ErrorDto errorDtoFromMvcResult(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());

    String error = content.get("error").asText();
    String message = content.get("message").asText();

    ErrorDto errorDto = new ErrorDto(error, message);
    return errorDto;
  }

  public static TokenDto tokenDtoFromMvcResult(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());

    String token = content.get("token").asText();
    String type = content.get("type").asText();

    TokenDto tokenDto = new TokenDto(token, type);
    return tokenDto;
  }

}
