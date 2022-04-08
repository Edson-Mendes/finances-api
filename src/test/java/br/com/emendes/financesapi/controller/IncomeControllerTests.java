package br.com.emendes.financesapi.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.util.CustomMockMvc;
import br.com.emendes.financesapi.util.Formatter;
import br.com.emendes.financesapi.util.DtoFromMvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class IncomeControllerTests {

  @Autowired
  private CustomMockMvc mock;

  private String tokenLorem;

  private String tokenIpsum;

  @BeforeAll
  public void addUsuarioLorem() {
    String name = "Lorem Sit";
    String email = "lorem.s@email.com";
    String password = "111111111";
    String confirm = "111111111";

    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);

    mock.post("/auth/signup", paramsSignup, "", 201);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenLorem = DtoFromMvcResult.tokenDto(result).generateTypeWithToken();
  }

  @BeforeAll
  public void addUsuarioIpsum() {
    String name = "Ipsum Sit";
    String email = "ipsum.s@email.com";
    String password = "222222222";
    String confirm = "222222222";

    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);

    mock.post("/auth/signup", paramsSignup, "", 201);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenIpsum = DtoFromMvcResult.tokenDto(result).generateTypeWithToken();
  }

  @Test
  @Order(1)
  public void deveriaDevolver201AoCriarReceita() {
    String description = "Salário";
    BigDecimal value = new BigDecimal(2500.00);
    String date = "05/01/2022";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.post("/receitas", params, tokenLorem, 201);
  }

  @Test
  @Order(2)
  public void deveriaDevolver400AoCriarSemAlgumParametroObrigatorio() {

    String description = "Venda do PS5";
    BigDecimal value = BigDecimal.valueOf(2885.00);
    String date = "22/01/2022";

    mock.post("/receitas", Map.of("value", value, "date", date), tokenLorem, 400);
    mock.post("/receitas", Map.of("description", description, "date", date), tokenLorem, 400);
    mock.post("/receitas", Map.of("description", description, "value", value), tokenLorem, 400);
    mock.post("/receitas", Map.of("value", value), tokenLorem, 400);
    mock.post("/receitas", Map.of("description", description), tokenLorem, 400);
    mock.post("/receitas", Map.of("date", date), tokenLorem, 400);
    mock.post("/receitas", Map.of(), tokenLorem, 400);
  }

  @Test
  @Order(3)
  public void deveriaDevolver200AoBuscarTodasAsReceitas() {
    mock.get("/receitas", tokenLorem, 200);
  }

  @Test
  @Order(4)
  public void deveriaDevolver409AoCadastrarDescricaoDuplicadaEmMesmoMesEAno() {

    String description = "Lotofácil";
    BigDecimal value = BigDecimal.valueOf(500.00);
    String date = "08/01/2022";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.post("/receitas", params, tokenLorem, 201);
    mock.post("/receitas", params, tokenLorem, 409);

  }

  @Test
  @Order(5)
  public void deveriaDevolver201AoCadastrarDescricaoEmMesDiferentes() {

    String description = "Trabalho por fora";
    BigDecimal value = BigDecimal.valueOf(300.00);
    String date = "18/01/2022";

    Map<String, Object> params1 = Map.of("description", description, "value", value, "date", date);

    String newDate = "18/02/2022";
    Map<String, Object> params2 = Map.of("description", description, "value", value, "date", newDate);

    mock.post("/receitas", params1, tokenLorem, 201);
    mock.post("/receitas", params2, tokenLorem, 201);

  }

  @Test
  @Order(6)
  public void deveriaDevolver200AoBuscarPorIdExistente() {
    MvcResult result = mock.get("/receitas", tokenLorem, 200);
    List<IncomeDto> listIncomeDto = DtoFromMvcResult.listIncomeDto(result);

    Long id = listIncomeDto.get(0).getId();

    mock.get("/receitas/" + id, tokenLorem, 200);
  }

  @Test
  @Order(7)
  public void deveriaDevolver200AoBuscarPorAnoEMesExistentes() {
    mock.get("/receitas/2022/01", tokenLorem, 200);
  }

  @Test
  @Order(8)
  public void deveriaDevolver404AoBuscarPorAnoEMesInexistentes() {
    mock.get("/receitas/2022/03", tokenLorem, 404);
  }

  @Test
  @Order(9)
  public void deveriaDevolver404AoBuscarPorIdInexistentes() {
    mock.get("/receitas/999", tokenLorem, 404);
  }

  @Test
  @Order(10)
  public void deveriaDevolver200AoBuscarPorDescricaoExistente() {
    mock.get("/receitas?description=sal", tokenLorem, 200);
  }

  @Test
  @Order(11)
  public void deveriaDevolver404AoBuscarPorDescricaoInexistente() {
    mock.get("/receitas?description=salllllll", tokenLorem, 404);
  }

  @Test
  @Order(12)
  public void deveriaDevolver200AoAtualizarReceitaCorretamente() {
    MvcResult result = mock.get("/receitas", tokenLorem, 200);
    List<IncomeDto> listIncomeDto = DtoFromMvcResult.listIncomeDto(result);

    Long id = listIncomeDto.get(0).getId();

    String description = "Salário 1";
    BigDecimal value = BigDecimal.valueOf(2500.00);
    String date = "08/01/2022";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.put("/receitas/" + id, params, tokenLorem, 200);
  }

  @Test
  @Order(13)
  public void deveriaDevolver404AoAtualizarReceitaComIdInexistente() {
    int id = 1000;
    String description = "Salário 1";
    BigDecimal value = BigDecimal.valueOf(2500.00);
    String date = "08/01/2022";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.put("/receitas/" + id, params, tokenLorem, 404);
  }

  @Test
  @Order(14)
  public void deveriaDevolver400AoAtualizarReceitaSemAlgumParametroObrigatorio() {
    String description = "Venda do PS5";
    BigDecimal value = BigDecimal.valueOf(2885.00);
    String date = "22/01/2022";

    mock.put("/receitas/1", Map.of("value", value, "date", date), tokenLorem, 400);
    mock.put("/receitas/1", Map.of("description", description, "date", date), tokenLorem, 400);
    mock.put("/receitas/1", Map.of("description", description, "value", value), tokenLorem, 400);
    mock.put("/receitas/1", Map.of("value", value), tokenLorem, 400);
    mock.put("/receitas/1", Map.of("description", description), tokenLorem, 400);
    mock.put("/receitas/1", Map.of("date", date), tokenLorem, 400);
    mock.put("/receitas/1", Map.of(), tokenLorem, 400);
  }

  @Test
  @Order(15)
  public void deveriaDevolver200AoDeletarUmaReceitaComIdExistente() {
    MvcResult result = mock.get("/receitas", tokenLorem, 200);
    List<IncomeDto> listIncomeDto = DtoFromMvcResult.listIncomeDto(result);

    Long id = listIncomeDto.get(1).getId();
    mock.delete("/receitas/" + id, tokenLorem, 204);
  }

  @Test
  @Order(16)
  public void deveriaDevolver404AoDeletarUmaReceitaComIdInexistente() {
    int id = 1000;
    mock.delete("/receitas/" + id, tokenLorem, 404);
  }

  @Test
  @Order(17)
  public void deveriaDevolverSomenteAsReceitasDeIpsum() {

    String description1 = "Salário";
    BigDecimal value1 = new BigDecimal("2500.0");
    String date1 = "08/01/2022";

    String description2 = "Venda do PC";
    BigDecimal value2 = new BigDecimal("1200.0");
    String date2 = "22/01/2022";

    Map<String, Object> params1 = Map.of("description", description1, "value", value1, "date", date1);
    Map<String, Object> params2 = Map.of("description", description2, "value", value2, "date", date2);

    mock.post("/receitas", params1, tokenIpsum, 201);
    mock.post("/receitas", params2, tokenIpsum, 201);

    MvcResult result = mock.get("/receitas", tokenIpsum, 200);
    List<IncomeDto> listIncomeDto = DtoFromMvcResult.listIncomeDto(result);

    List<IncomeDto> listExpected = new ArrayList<>();

    IncomeDto incomeDto1 = new IncomeDto(5l, description1, LocalDate.parse(date1, Formatter.dateFormatter), value1);
    IncomeDto incomeDto2 = new IncomeDto(6l, description2, LocalDate.parse(date2, Formatter.dateFormatter), value2);

    listExpected.add(incomeDto2);
    listExpected.add(incomeDto1);

    Assertions.assertEquals(listExpected.size(), listIncomeDto.size());
    Assertions.assertEquals(listExpected, listIncomeDto);
  }

  @Test
  @Order(18)
  public void deveriaDevolver404AoTentarAtualizarReceitaDeOutroUsuario() {
    int id = 1;
    String description = "Salário 1";
    BigDecimal value = BigDecimal.valueOf(2500.00);
    String date = "08/01/2022";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    MvcResult result = mock.put("/receitas/" + id, params, tokenIpsum, 404);

    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Not Found", errorDto.getError());
    Assertions.assertEquals("Nenhuma receita com esse id para esse usuário", errorDto.getMessage());
  }

  @Test
  @Order(19)
  public void deveriaDevolver404AoTentarDeletarReceitaDeOutroUsuario() {
    Long id = 1l;

    MvcResult result = mock.delete("/receitas/" + id, tokenIpsum, 404);

    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Not Found", errorDto.getError());
    Assertions.assertEquals("Nenhuma receita com esse id para esse usuário", errorDto.getMessage());
  }

}
