//package br.com.emendes.financesapi.integrationold;
//
//import java.util.Map;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MvcResult;
//
//import br.com.emendes.financesapi.controller.dto.RoleDto;
//import br.com.emendes.financesapi.util.CustomMockMvc;
//import br.com.emendes.financesapi.util.DtoFromMvcResult;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@TestInstance(Lifecycle.PER_CLASS)
//@TestMethodOrder(OrderAnnotation.class)
//@ActiveProfiles("test")
//public class RoleControllerIT {
//
//  @Autowired
//  private CustomMockMvc mock;
//
//  private String tokenAdmin;
//
//  private String tokenUser;
//
//  @BeforeAll
//  public void generateTokenUser() {
//    String name = "User Common";
//    String email = "user@email.com";
//    String password = "111111111";
//    String confirm = "111111111";
//
//    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
//    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);
//
//    mock.post("/auth/signup", paramsSignup, "", 201);
//    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);
//
//    tokenUser = DtoFromMvcResult.tokenDto(result).generateTypeWithToken();
//  }
//
//  @BeforeAll
//  public void generateTokenAdmin() {
//    String email = "admin@email.com";
//    String password = "123456";
//
//    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);
//    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);
//
//    tokenAdmin = DtoFromMvcResult.tokenDto(result).generateTypeWithToken();
//  }
//
//  @Test
//  @Order(1)
//  public void deveriaDevolver403QuandoUserBuscarRoles() {
//    mock.get("/roles", tokenUser, 403);
//  }
//
//  @Test
//  @Order(2)
//  public void deveriaDevolver200AoBuscarTodosOsRoles() {
//    mock.get("/roles", tokenAdmin, 200);
//  }
//
//  @Test
//  @Order(3)
//  public void deveriaDevolver403QuandoUserBuscarRolePorId() {
//    Long id = 1l;
//    mock.get("/roles/" + id, tokenUser, 403);
//  }
//
//  @Test
//  @Order(4)
//  public void deveriaDevolver200ERoleDtoAoBuscarRolePorId() {
//    Long id = 1l;
//    MvcResult result = mock.get("/roles/" + id, tokenAdmin, 200);
//    RoleDto roleDto = DtoFromMvcResult.roleDto(result);
//
//    Assertions.assertEquals("ROLE_USER", roleDto.getName());
//  }
//
//}
