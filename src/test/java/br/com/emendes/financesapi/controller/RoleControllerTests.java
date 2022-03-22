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
import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.util.CustomMockMvc;
import br.com.emendes.financesapi.util.DtoFromMvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class RoleControllerTests {
  
  @Autowired
  private CustomMockMvc mock;

  private String tokenAdmin;

  private String tokenUser;

  @BeforeAll
  public void generateTokenUser() throws Exception{
    String name = "User Common";
    String email = "user@email.com";
    String password = "111111111";
    String confirm = "111111111";

    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);

    mock.post("/auth/signup", paramsSignup, "", 201);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenUser = DtoFromMvcResult.tokenDto(result).getTypeWithToken();
  }

  @BeforeAll
  public void generateTokenAdmin() throws Exception{
    String email = "admin@email.com";
    String password = "123456";

    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);
    
    tokenAdmin = DtoFromMvcResult.tokenDto(result).getTypeWithToken();
  }

  @Test
  @Order(1)
  public void deveriaDevolver403QuantoUserCriarRole() throws Exception{
    String name = "ROLE_MANAGER";
    Map<String, Object> params = Map.of("name", name);

    mock.post("/role", params, tokenUser, 403);
  }

  @Test
  @Order(2)
  public void deveriaDevolver400AoCriarRoleComNomeInvalido() throws Exception {
    mock.post("/role", Map.of("name", "Role_manager"), tokenAdmin, 400);
    mock.post("/role", Map.of("name", "MANAGER"), tokenAdmin, 400);
    mock.post("/role", Map.of("name", "ROLEMANAGER"), tokenAdmin, 400);
    mock.post("/role", Map.of("name", "ROLE_"), tokenAdmin, 400);
    mock.post("/role", Map.of("name", "ROLE_manager"), tokenAdmin, 400);
    mock.post("/role", Map.of("name", "ROL_MANAGER"), tokenAdmin, 400);
  }

  @Test
  @Order(3)
  public void deveriaDevolver201AoCriarRoleCorretamente() throws Exception {
    String name = "ROLE_MANAGER";
    Map<String, Object> params = Map.of("name", name);

    mock.post("/role", params, tokenAdmin, 201);
  }

  @Test
  @Order(4)
  public void deveriaDevolver409EErrorDtoAoCriarRoleComNomeExistente() throws Exception {
    String name = "ROLE_MANAGER";
    Map<String, Object> params = Map.of("name", name);

    MvcResult result = mock.post("/role", params, tokenAdmin, 409);

    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("CONFLICT", errorDto.getError());
    Assertions.assertEquals("já existe role com esse nome", errorDto.getMessage());
  }

  @Test
  @Order(5)
  public void deveriaDevolver403QuandoUserBuscarRoles() throws Exception {
    mock.get("/role", tokenUser, 403);
  } 

  @Test
  @Order(6)
  public void deveriaDevolver200AoBuscarTodosOsRoles() throws Exception {
    mock.get("/role", tokenAdmin, 200);
  }

  @Test
  @Order(7)
  public void deveriaDevolver403QuandoUserBuscarRolePorId() throws Exception {
    Long id = 1l;
    mock.get("/role/"+id, tokenUser, 403);
  }

  @Test
  @Order(8)
  public void deveriaDevolver200ERoleDtoAoBuscarRolePorId() throws Exception {
    Long id = 1l;
    MvcResult result = mock.get("/role/"+id, tokenAdmin, 200);
    RoleDto roleDto = DtoFromMvcResult.roleDto(result);

    Assertions.assertEquals("ROLE_USER", roleDto.getName());
  }

  @Test
  @Order(9)
  public void deveriaDevolver403QuantoUserDeletarRole() throws Exception {
    Long id = 3l;
    mock.delete("/role/"+id, tokenUser, 403);
  }

  @Test
  @Order(10)
  public void deveriaDevolver404EErrorDtoAoDeletarRoleComIdInvalido() throws Exception {
    Long id = 1000l;
    MvcResult result = mock.delete("/role/"+id, tokenAdmin, 404);

    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Not Found", errorDto.getError());
    Assertions.assertEquals("não existe role com id: 1000", errorDto.getMessage());
  }

  @Test
  @Order(11)
  public void deveriaDevolver200AoDeletarRole() throws Exception {
    Long id = 3l;

    mock.delete("/role/"+id, tokenAdmin, 200);
  }
  
}
