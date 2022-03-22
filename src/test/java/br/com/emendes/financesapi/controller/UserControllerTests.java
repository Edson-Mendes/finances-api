package br.com.emendes.financesapi.controller;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.util.CustomMockMvc;
import br.com.emendes.financesapi.util.DtoFromMvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class UserControllerTests {

  @Autowired
  private CustomMockMvc mock;

  private String tokenUser;

  private String tokenAdmin;

  @BeforeAll
  public void addCommonUser() throws Exception {
    String name = "Common User";
    String email = "commonuser@email.com";
    String password = "111111111";
    String confirm = "111111111";

    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);

    mock.post("/auth/signup", paramsSignup, "", 201);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenUser = DtoFromMvcResult.tokenDto(result).getTypeWithToken();
  }

  @BeforeAll
  public void loginAdminUser() throws Exception {
    String email = "admin@email.com";
    String password = "123456";
  
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenAdmin = DtoFromMvcResult.tokenDto(result).getTypeWithToken();
  }

  @Test
  @Order(1)
  public void deveriaDevolver200AoAtualizarASenhaDoUsuario() throws Exception {
    String newPassword = "222222222";
    String confirm = "222222222";

    Map<String, Object> params = Map.of("newPassword", newPassword, "confirm", confirm);

    mock.put("/user/change-password", params, tokenUser, 200);
  }

  @Test
  @Order(2)
  public void deveriaDevolver403QuandoRole_UserDeletarUsuario() throws Exception{
    Long id = 1l;
    mock.delete("/user/"+id, tokenUser, 403);
  }

  @Test
  @Order(3)
  public void deveriaDevolver404EErrorDtoAoDeletarUsuarioInexistente() throws Exception {
    Long id = 100l;
    MvcResult result = mock.delete("/user/"+id, tokenAdmin, 404);

    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);
    Assertions.assertEquals("Not Found", errorDto.getError());
    Assertions.assertEquals("não existe usuário com id: "+id, errorDto.getMessage());
  }

  @Test
  @Order(4)
  public void deveriaDevolver200AoAdminDeletarUsuario() throws Exception {
    Long id = 2l;
    mock.delete("/user/"+id, tokenAdmin, 200);
  }

}
