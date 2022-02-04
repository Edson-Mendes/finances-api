package br.com.emendes.financesapi.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExpenseControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void deveriaDevolverStatus201AoCriarDespesa() throws Exception {

    String description = "Gasolina";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "2022-01-28";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    postDespesa(params, 201);
  }

  @Test
  public void deveriaDevolverCategoryOutrasQuandoNaoInseridoCategory() throws Exception {

    String description = "Mercado";
    BigDecimal value = BigDecimal.valueOf(719.40);
    String date = "2022-01-31";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    MvcResult result = postDespesa(params, 201);

    String resultContent = result.getResponse().getContentAsString();
    Assertions.assertTrue(resultContent.contains("\"category\":\"OUTRAS\""));

  }

  @Test
  public void deveriaDevolver409AoCadastrarDescricaoDuplicadaEmMesmoMesEAno() throws Exception{

    String description = "Aluguel";
    BigDecimal value = BigDecimal.valueOf(1500.00);
    String date = "2022-01-08";
    String category = "MORADIA";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    postDespesa(params, 201);
    postDespesa(params, 409);

  }

  @Test
  public void deveriaDevolver201AoCadastrarDescricaoEmMesDiferentes() throws Exception{

    String description = "Netflix";
    BigDecimal value = BigDecimal.valueOf(39.90);
    String date = "2022-01-18";
    String category = "LAZER";

    Map<String, Object> params1 = Map.of("description", description, "value", value, "date", date, "category", category);
    
    String newDate = "2022-02-18";
    Map<String, Object> params2 = Map.of("description", description, "value", value, "date", newDate, "category", category);
    
    postDespesa(params1, 201);
    postDespesa(params2, 201);

  }

  @Test
  public void deveriaDevolver400AoNaoEnviarAlgumParametroObrigatorio() throws Exception{

    String description = "Farmácia";
    BigDecimal value = BigDecimal.valueOf(85.00);
    String date = "2022-01-22";
    String category = "SAUDE";
    
    postDespesa(Map.of("value", value, "date", date, "category", category), 400);
    postDespesa(Map.of("description", description, "date", date, "category", category), 400);
    postDespesa(Map.of("description", description, "value", value, "category", category), 400);
    postDespesa(Map.of("value", value, "category", category), 400);
    postDespesa(Map.of("description", description, "category", category), 400);
    postDespesa(Map.of("date", date, "category", category), 400);
    postDespesa(Map.of(), 400);
  }

  @Test
  public void deveriaDevolver200AoBuscarTodasAsDespesas() throws Exception {
    getDespesa(new URI("/despesas"), 200);
  }

  @Test
  public void deveriaDevolver200AoBuscarPorIdExistente() throws Exception {
    getDespesa(new URI("/despesas/1"), 200);
  }

  @Test
  public void deveriaDevolver200AoBuscarPorAnoEMesExistentes() throws Exception {
    getDespesa(new URI("/despesas/2022/01"), 200);
  }

  @Test
  public void deveriaDevolver404AoBuscarPorAnoEMesInexistentes() throws Exception {
    getDespesa(new URI("/despesas/2022/03"), 404);
  }

  @Test
  public void deveriaDevolver404AoBuscarPorIdInexistentes() throws Exception {
    getDespesa(new URI("/despesas/999"), 404);
  }

  @Test
  public void deveriaDevolver200AoAtualizarDespesaCorretamente() throws Exception {
    int id = 1;
    String description = "Combustivel";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "2022-01-28";
    String category = "TRANSPORTE";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    putDespesa(params, 200, id);
  }

  @Test
  public void deveriaDevolver404AoAtualizarDespesaComIdInexistente() throws Exception {
    int id = 1000;
    String description = "Combustivel";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "2022-01-28";
    String category = "TRANSPORTE";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date, "category", category);

    putDespesa(params, 404, id);
  }

  @Test
  public void deveriaDevolver400AoAtualizarDespesaSemAlgumParametroObrigatorio() throws Exception {
    int id = 1;
    String description = "Combustivel";
    BigDecimal value = BigDecimal.valueOf(341.87);
    String date = "2022-01-28";
    String category = "TRANSPORTE";

    putDespesa(Map.of("value", value, "date", date, "category", category), 400, 1);
    putDespesa(Map.of("description", description, "date", date, "category", category), 400, 1);
    putDespesa(Map.of("description", description, "value", value, "category", category), 400, 1);
    putDespesa(Map.of("value", value, "category", category), 400, 1);
    putDespesa(Map.of("description", description, "category", category), 400, 1);
    putDespesa(Map.of("date", date, "category", category), 400, 1);
    putDespesa(Map.of(), 400, 1);
  }

  @Test
  public void deveriaDevolver200AoDeletarUmaDespesaComIdExistente() throws Exception {
    int id = 1;
    deleteDespesa(200, id);
  }

  @Test
  public void deveriaDevolver400AoDeletarUmaDespesaComIdInexistente() throws Exception {
    int id = 1000;
    deleteDespesa(404, id);
  }

  private MvcResult postDespesa(Map<String, Object> params, int status) throws Exception{
    URI uri = new URI("/despesas");
    String body = new ObjectMapper().writeValueAsString(params);

    return mockMvc
        .perform(MockMvcRequestBuilders
            .post(uri)
            .content(body)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }
  
  private MvcResult getDespesa(URI uri, int status) throws Exception{
    return mockMvc
    .perform(MockMvcRequestBuilders
    .get(uri)
    .contentType(MediaType.APPLICATION_JSON))
    .andExpect(MockMvcResultMatchers.status().is(status))
    .andReturn();
  }
  
  private MvcResult putDespesa(Map<String, Object> params, int status, int id) throws Exception{
    URI uri = new URI("/despesas/"+id);
    String body = new ObjectMapper().writeValueAsString(params);

    return mockMvc
        .perform(MockMvcRequestBuilders
            .put(uri)
            .content(body)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }
  
  private MvcResult deleteDespesa(int status, int id) throws Exception {
    URI uri = new URI("/despesas/"+id);
    return mockMvc
        .perform(MockMvcRequestBuilders
            .delete(uri)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }
}
