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

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.util.CustomMockMvc;
import br.com.emendes.financesapi.util.DtoFromMvcResult;
import br.com.emendes.financesapi.util.Formatter;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class ExpenseControllerTests {

  @Autowired
  private CustomMockMvc mock;

  private String tokenLorem;

  private String tokenIpsum;

  @BeforeAll
  public void addUsuarioLorem() {
    String name = "Lorem Amet";
    String email = "lorem.a@email.com";
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
    String name = "Ipsum Amet";
    String email = "ipsum.a@email.com";
    String password = "22222222";
    String confirm = "22222222";

    Map<String, Object> paramsSignup = Map.of("name", name, "email", email, "password", password, "confirm", confirm);
    Map<String, Object> paramsSignin = Map.of("email", email, "password", password);

    mock.post("/auth/signup", paramsSignup, "", 201);
    MvcResult result = mock.post("/auth/signin", paramsSignin, "", 200);

    tokenIpsum = DtoFromMvcResult.tokenDto(result).generateTypeWithToken();
  }

  @Test
  @Order(1)
  public void deveriaDevolverStatus201AoCriarDespesa() {

    String description = "Gasolina";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "28/01/2022";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.post("/despesas", params, tokenLorem, 201);
  }

  @Test
  @Order(2)
  public void deveriaDevolverCategoryOutrasQuandoNaoInseridoCategory() {

    String description = "Mercado";
    BigDecimal value = BigDecimal.valueOf(719.40);
    String date = "31/01/2022";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    MvcResult result = mock.post("/despesas", params, tokenLorem, 201);
    ExpenseDto expenseDto = DtoFromMvcResult.expenseDto(result);

    Assertions.assertEquals(expenseDto.getCategory(), Category.OUTRAS);

  }

  @Test
  @Order(3)
  public void deveriaDevolver409AoCadastrarDescricaoDuplicadaEmMesmoMesEAno() {

    String description = "Aluguel";
    BigDecimal value = BigDecimal.valueOf(1500.00);
    String date = "08/01/2022";
    String category = "MORADIA";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    mock.post("/despesas", params, tokenLorem, 201);
    mock.post("/despesas", params, tokenLorem, 409);

  }

  @Test
  @Order(4)
  public void deveriaDevolver201AoCadastrarDescricaoEmMesDiferentes() {

    String description = "Netflix";
    BigDecimal value = BigDecimal.valueOf(39.90);
    String date = "18/01/2022";
    String category = "LAZER";

    Map<String, Object> params1 = Map.of("description", description, "value", value, "date", date, "category",
        category);

    String newDate = "18/02/2022";
    Map<String, Object> params2 = Map.of("description", description, "value", value, "date", newDate, "category",
        category);

    mock.post("/despesas", params1, tokenLorem, 201);
    mock.post("/despesas", params2, tokenLorem, 201);

  }

  @Test
  @Order(5)
  public void deveriaDevolver400AoNaoEnviarAlgumParametroObrigatorio() {

    String description = "Farmácia";
    BigDecimal value = BigDecimal.valueOf(85.00);
    String date = "22/01/2022";
    String category = "SAUDE";

    mock.post("/despesas", Map.of("value", value, "date", date, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("description", description, "date", date, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("description", description, "value", value, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("value", value, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("description", description, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of("date", date, "category", category), tokenLorem, 400);
    mock.post("/despesas", Map.of(), tokenLorem, 400);
  }

  @Test
  @Order(6)
  public void deveriaDevolver200AoBuscarTodasAsDespesas() {
    int pagina = 0;
    int quantidade = 1;
    mock.get("/despesas?page=" + pagina + "&size=" + quantidade, tokenLorem, 200);
  }

  @Test
  @Order(7)
  public void deveriaDevolver200AoBuscarPorIdExistente() {
    int pagina = 0;
    int quantidade = 1;
    MvcResult result = mock.get("/despesas?page=" + pagina + "&size=" + quantidade, tokenLorem, 200);
    List<ExpenseDto> listExpenseDto = DtoFromMvcResult.listExpenseDto(result);

    Long id = listExpenseDto.get(0).getId();

    mock.get("/despesas/" + id, tokenLorem, 200);
  }

  @Test
  @Order(8)
  public void deveriaDevolver200AoBuscarPorAnoEMesExistentes() {
    mock.get("/despesas/2022/01?page=0&size=1", tokenLorem, 200);
  }

  @Test
  @Order(9)
  public void deveriaDevolver404AoBuscarPorAnoEMesInexistentes() {
    mock.get("/despesas/2022/03?page=0&size=1", tokenLorem, 404);
  }

  @Test
  @Order(10)
  public void deveriaDevolver404AoBuscarPorIdInexistentes() {
    mock.get("/despesas/999", tokenLorem, 404);
  }

  @Test
  @Order(11)
  public void deveriaDevolver200AoBuscarPorDescricaoExistente() {
    mock.get("/despesas?description=net&page=0&size=1", tokenLorem, 200);
  }

  @Test
  @Order(12)
  public void deveriaDevolver404AoBuscarPorDescricaoInexistente() {
    int pagina = 0;
    int quantidade = 1;
    mock.get("/despesas?description=nettttt&page=" + pagina + "&size=" + quantidade, tokenLorem, 404);
  }

  @Test
  @Order(13)
  public void deveriaDevolver200AoAtualizarDespesaCorretamente() {
    int pagina = 0;
    int quantidade = 1;
    MvcResult result = mock.get("/despesas?page=" + pagina + "&size=" + quantidade, tokenLorem, 200);
    List<ExpenseDto> listExpenseDto = DtoFromMvcResult.listExpenseDto(result);

    Long id = listExpenseDto.get(0).getId();

    String description = "Combustivel";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "28/01/2022";
    String category = "TRANSPORTE";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    mock.put("/despesas/" + id, params, tokenLorem, 200);
  }

  @Test
  @Order(14)
  public void deveriaDevolver404AoAtualizarDespesaComIdInexistente() {
    int id = 1000;
    String description = "Combustivel";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "28/01/2022";
    String category = "TRANSPORTE";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    mock.put("/despesas/" + id, params, tokenLorem, 404);
  }

  @Test
  @Order(15)
  public void deveriaDevolver400AoAtualizarDespesaSemAlgumParametroObrigatorio() {
    String description = "Combustivel";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "28/01/2022";
    String category = "TRANSPORTE";

    mock.put("/despesas/1", Map.of("value", value, "date", date, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("description", description, "date", date, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("description", description, "value", value, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("value", value, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("description", description, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of("date", date, "category", category), tokenLorem, 400);
    mock.put("/despesas/1", Map.of(), tokenLorem, 400);
  }

  @Test
  @Order(16)
  public void deveriaDevolver200AoDeletarUmaDespesaComIdExistente() {
    int pagina = 0;
    int quantidade = 1;
    MvcResult result = mock.get("/despesas?page=" + pagina + "&size=" + quantidade, tokenLorem, 200);
    List<ExpenseDto> listExpenseDto = DtoFromMvcResult.listExpenseDto(result);

    Long id = listExpenseDto.get(0).getId();

    mock.delete("/despesas/" + id, tokenLorem, 200);
  }

  @Test
  @Order(17)
  public void deveriaDevolver404AoDeletarUmaDespesaComIdInexistente() {
    int id = 1000;
    mock.delete("/despesas/" + id, tokenLorem, 404);
  }

  @Test
  @Order(18)
  public void deveriaDevolverSomenteAsDespesasDeIpsum() {
    String description1 = "Aluguel";
    BigDecimal value1 = new BigDecimal("1000.0");
    String date1 = "05/01/2022";
    String category = "MORADIA";

    String description2 = "Condomínio";
    BigDecimal value2 = new BigDecimal("200.0");
    String date2 = "06/01/2022";

    Map<String, Object> params1 = Map.of("description", description1, "value", value1, "date", date1, "category",
        category);
    Map<String, Object> params2 = Map.of("description", description2, "value", value2, "date", date2, "category",
        category);

    mock.post("/despesas", params1, tokenIpsum, 201);
    mock.post("/despesas", params2, tokenIpsum, 201);

    int pagina = 0;
    int quantidade = 2;
    MvcResult result = mock.get("/despesas?page=" + pagina + "&size=" + quantidade, tokenIpsum, 200);
    List<ExpenseDto> listExpenseDto = DtoFromMvcResult.listExpenseDto(result);
    List<ExpenseDto> listExpected = new ArrayList<>();

    ExpenseDto expenseDto1 = new ExpenseDto(6l, description1, LocalDate.parse(date1, Formatter.dateFormatter), value1,
        Category.valueOf(category));
    ExpenseDto expenseDto2 = new ExpenseDto(7l, description2, LocalDate.parse(date2, Formatter.dateFormatter), value2,
        Category.valueOf(category));

    listExpected.add(expenseDto2);
    listExpected.add(expenseDto1);

    Assertions.assertEquals(listExpected.size(), listExpenseDto.size());
    Assertions.assertEquals(listExpected, listExpenseDto);
  }

  @Test
  @Order(19)
  public void deveriaDevolver404AoTentarAtualizarDespesaDeOutroUsuario() {
    int id = 1;
    String description = "Spotify";
    BigDecimal value = BigDecimal.valueOf(20.00);
    String date = "08/01/2022";
    String category = "LAZER";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    MvcResult result = mock.put("/despesas/" + id, params, tokenIpsum, 404);

    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Not Found", errorDto.getError());
    Assertions.assertEquals("Nenhuma despesa com esse id para esse usuário", errorDto.getMessage());
  }

  @Test
  @Order(20)
  public void deveriaDevolver404AoTentarDeletarDespesaDeOutroUsuario() {
    Long id = 1l;

    MvcResult result = mock.delete("/despesas/" + id, tokenIpsum, 404);

    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Not Found", errorDto.getError());
    Assertions.assertEquals("Nenhuma despesa com esse id para esse usuário", errorDto.getMessage());
  }

  @Test
  @Order(21)
  public void deveriaDevolver400EErrorDtoAoSalvarComCategoriaInvalida() {
    String description = "Farmácia";
    BigDecimal value = BigDecimal.valueOf(82.00);
    String date = "14/01/2022";
    String category = "FARMACIA";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    MvcResult result = mock.post("/despesas", params, tokenLorem, 400);
    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Categoria inválida", errorDto.getError());
    Assertions.assertEquals(
        "Categorias válidas: ALIMENTACAO, SAUDE, MORADIA, TRANSPORTE, EDUCACAO, LAZER, IMPREVISTOS, OUTRAS",
        errorDto.getMessage());
  }

  @Test
  @Order(21)
  public void deveriaDevolver400EErrorDtoAoSalvarComCategoriaVazia() {
    String description = "Farmácia";
    BigDecimal value = BigDecimal.valueOf(82.00);
    String date = "14/01/2022";
    String category = "";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    MvcResult result = mock.post("/despesas", params, tokenLorem, 400);
    ErrorDto errorDto = DtoFromMvcResult.errorDto(result);

    Assertions.assertEquals("Categoria inválida", errorDto.getError());
    Assertions.assertEquals(
        "Categorias válidas: ALIMENTACAO, SAUDE, MORADIA, TRANSPORTE, EDUCACAO, LAZER, IMPREVISTOS, OUTRAS",
        errorDto.getMessage());
  }

}
