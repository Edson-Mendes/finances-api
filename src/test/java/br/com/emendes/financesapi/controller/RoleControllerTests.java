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
  public void deveriaDevolver403QuandoUserBuscarRoles() throws Exception {
    mock.get("/role", tokenUser, 403);
  } 

  @Test
  @Order(2)
  public void deveriaDevolver200AoBuscarTodosOsRoles() throws Exception {
    mock.get("/role", tokenAdmin, 200);
  }

  @Test
  @Order(3)
  public void deveriaDevolver403QuandoUserBuscarRolePorId() throws Exception {
    Long id = 1l;
    mock.get("/role/"+id, tokenUser, 403);
  }

  @Test
  @Order(4)
  public void deveriaDevolver200ERoleDtoAoBuscarRolePorId() throws Exception {
    Long id = 1l;
    MvcResult result = mock.get("/role/"+id, tokenAdmin, 200);
    RoleDto roleDto = DtoFromMvcResult.roleDto(result);

    Assertions.assertEquals("ROLE_USER", roleDto.getName());
  }
  
}
