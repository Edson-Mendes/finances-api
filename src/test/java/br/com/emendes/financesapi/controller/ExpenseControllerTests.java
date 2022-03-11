// package br.com.emendes.financesapi.controller;

// import java.math.BigDecimal;
// import java.util.Map;

// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MvcResult;

// import br.com.emendes.financesapi.util.CustomMockMvc;

// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// public class ExpenseControllerTests {

//   @Autowired
//   private CustomMockMvc mock;

//   @Test
//   public void deveriaDevolverStatus201AoCriarDespesa() throws Exception {

//     String description = "Gasolina";
//     BigDecimal value = BigDecimal.valueOf(341.87);
//     String date = "2022-01-28";

//     Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

//     mock.post("/despesas", params, 201);
//   }

//   @Test
//   public void deveriaDevolverCategoryOutrasQuandoNaoInseridoCategory() throws Exception {

//     String description = "Mercado";
//     BigDecimal value = BigDecimal.valueOf(719.40);
//     String date = "2022-01-31";

//     Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

//     MvcResult result = mock.post("/despesas", params, 201);

//     String resultContent = result.getResponse().getContentAsString();
//     Assertions.assertTrue(resultContent.contains("\"category\":\"OUTRAS\""));

//   }

//   @Test
//   public void deveriaDevolver409AoCadastrarDescricaoDuplicadaEmMesmoMesEAno() throws Exception{

//     String description = "Aluguel";
//     BigDecimal value = BigDecimal.valueOf(1500.00);
//     String date = "2022-01-08";
//     String category = "MORADIA";

//     Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

//     mock.post("/despesas", params, 201);
//     mock.post("/despesas", params, 409);

//   }

//   @Test
//   public void deveriaDevolver201AoCadastrarDescricaoEmMesDiferentes() throws Exception{

//     String description = "Netflix";
//     BigDecimal value = BigDecimal.valueOf(39.90);
//     String date = "2022-01-18";
//     String category = "LAZER";

//     Map<String, Object> params1 = Map.of("description", description, "value", value, "date", date, "category", category);
    
//     String newDate = "2022-02-18";
//     Map<String, Object> params2 = Map.of("description", description, "value", value, "date", newDate, "category", category);
    
//     mock.post("/despesas", params1, 201);
//     mock.post("/despesas", params2, 201);

//   }

//   @Test
//   public void deveriaDevolver400AoNaoEnviarAlgumParametroObrigatorio() throws Exception{

//     String description = "Farmácia";
//     BigDecimal value = BigDecimal.valueOf(85.00);
//     String date = "2022-01-22";
//     String category = "SAUDE";
    
//     mock.post("/despesas", Map.of("value", value, "date", date, "category", category), 400);
//     mock.post("/despesas", Map.of("description", description, "date", date, "category", category), 400);
//     mock.post("/despesas", Map.of("description", description, "value", value, "category", category), 400);
//     mock.post("/despesas", Map.of("value", value, "category", category), 400);
//     mock.post("/despesas", Map.of("description", description, "category", category), 400);
//     mock.post("/despesas", Map.of("date", date, "category", category), 400);
//     mock.post("/despesas", Map.of(), 400);
//   }

//   @Test
//   public void deveriaDevolver200AoBuscarTodasAsDespesas() throws Exception {
//     mock.get("/despesas", 200);
//   }

//   @Test
//   public void deveriaDevolver200AoBuscarPorIdExistente() throws Exception {
//     mock.get("/despesas/1", 200);
//   }

//   @Test
//   public void deveriaDevolver200AoBuscarPorAnoEMesExistentes() throws Exception {
//     mock.get("/despesas/2022/01", 200);
//   }

//   @Test
//   public void deveriaDevolver404AoBuscarPorAnoEMesInexistentes() throws Exception {
//     mock.get("/despesas/2022/03", 404);
//   }

//   @Test
//   public void deveriaDevolver404AoBuscarPorIdInexistentes() throws Exception {
//     mock.get("/despesas/999", 404);
//   }

//   @Test
//   public void deveriaDevolver200AoBuscarPorDescricaoExistente() throws Exception{
//     mock.get("/despesas?description=net", 200);
//   }

//   @Test
//   public void deveriaDevolver404AoBuscarPorDescricaoInexistente() throws Exception{
//     mock.get("/despesas?description=nettttt", 404);
//   }

//   @Test
//   public void deveriaDevolver200AoAtualizarDespesaCorretamente() throws Exception {
//     int id = 1;
//     String description = "Combustivel";
//     BigDecimal value = BigDecimal.valueOf(341.87);
//     String date = "2022-01-28";
//     String category = "TRANSPORTE";

//     Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

//     mock.put("/despesas/"+id, params, 200);
//   }

//   @Test
//   public void deveriaDevolver404AoAtualizarDespesaComIdInexistente() throws Exception {
//     int id = 1000;
//     String description = "Combustivel";
//     BigDecimal value = BigDecimal.valueOf(341.87);
//     String date = "2022-01-28";
//     String category = "TRANSPORTE";

//     Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

//     mock.put("/despesas/"+id, params, 404);
//   }

//   @Test
//   public void deveriaDevolver400AoAtualizarDespesaSemAlgumParametroObrigatorio() throws Exception {
//     String description = "Combustivel";
//     BigDecimal value = BigDecimal.valueOf(341.87);
//     String date = "2022-01-28";
//     String category = "TRANSPORTE";

//     mock.put("/despesas/1", Map.of("value", value, "date", date, "category", category), 400);
//     mock.put("/despesas/1", Map.of("description", description, "date", date, "category", category), 400);
//     mock.put("/despesas/1", Map.of("description", description, "value", value, "category", category), 400);
//     mock.put("/despesas/1", Map.of("value", value, "category", category), 400);
//     mock.put("/despesas/1", Map.of("description", description, "category", category), 400);
//     mock.put("/despesas/1", Map.of("date", date, "category", category), 400);
//     mock.put("/despesas/1", Map.of(), 400);
//   }

//   @Test
//   public void deveriaDevolver200AoDeletarUmaDespesaComIdExistente() throws Exception {
//     int id = 1;
//     mock.delete("/despesas/"+id, 200);
//   }

//   @Test
//   public void deveriaDevolver400AoDeletarUmaDespesaComIdInexistente() throws Exception {
//     int id = 1000;
//     mock.delete("/despesas/"+id, 404);
//   }

// }
