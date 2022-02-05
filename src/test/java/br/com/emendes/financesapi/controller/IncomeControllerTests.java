package br.com.emendes.financesapi.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

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
public class IncomeControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void deveriaDevolver201AoCriarReceita() throws Exception {
    String description = "Salário";
    BigDecimal value = new BigDecimal(2500.00);
    String date = "2022-01-05";
    
    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    postReceita(params, 201);
  }
  
  @Test
  public void deveriaDevolver400AoCriarSemAlgumParametroObrigatorio() throws Exception{

    String description = "Venda do PS5";
    BigDecimal value = BigDecimal.valueOf(2885.00);
    String date = "2022-01-22";
    
    postReceita(Map.of("value", value, "date", date), 400);
    postReceita(Map.of("description", description, "date", date), 400);
    postReceita(Map.of("description", description, "value", value), 400);
    postReceita(Map.of("value", value), 400);
    postReceita(Map.of("description", description), 400);
    postReceita(Map.of("date", date), 400);
    postReceita(Map.of(), 400);
  }

  @Test
  public void deveriaDevolver200AoBuscarTodasAsReceitas() throws Exception {
    getReceita(new URI("/receitas"), 200);
  }

  @Test
  public void deveriaDevolver409AoCadastrarDescricaoDuplicadaEmMesmoMesEAno() throws Exception{

    String description = "Lotofácil";
    BigDecimal value = BigDecimal.valueOf(500.00);
    String date = "2022-01-08";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    postReceita(params, 201);
    postReceita(params, 409);

  }

  @Test
  public void deveriaDevolver201AoCadastrarDescricaoEmMesDiferentes() throws Exception{

    String description = "Trabalho por fora";
    BigDecimal value = BigDecimal.valueOf(300.00);
    String date = "2022-01-18";

    Map<String, Object> params1 = Map.of("description", description, "value", value, "date", date);
    
    String newDate = "2022-02-18";
    Map<String, Object> params2 = Map.of("description", description, "value", value, "date", newDate);
    
    postReceita(params1, 201);
    postReceita(params2, 201);

  }

  @Test
  public void deveriaDevolver200AoBuscarPorIdExistente() throws Exception {
    getReceita(new URI("/receitas/1"), 200);
  }

  @Test
  public void deveriaDevolver200AoBuscarPorAnoEMesExistentes() throws Exception {
    getReceita(new URI("/receitas/2022/01"), 200);
  }

  @Test
  public void deveriaDevolver404AoBuscarPorAnoEMesInexistentes() throws Exception {
    getReceita(new URI("/receitas/2022/03"), 404);
  }

  @Test
  public void deveriaDevolver404AoBuscarPorIdInexistentes() throws Exception {
    getReceita(new URI("/receitas/999"), 404);
  }

  @Test
  public void deveriaDevolver200AoBuscarPorDescricaoExistente() throws Exception{
    getReceita(new URI("/receitas?description=sal"), 200);
  }

  @Test
  public void deveriaDevolver404AoBuscarPorDescricaoInexistente() throws Exception{
    getReceita(new URI("/receitas?description=salllllll"), 404);
  }

  @Test
  public void deveriaDevolver200AoAtualizarReceitaCorretamente() throws Exception {
    int id = 1;
    String description = "Salário 1";
    BigDecimal value = BigDecimal.valueOf(2500.00);
    String date = "2022-01-08";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    putReceita(params, 200, id);
  }

  @Test
  public void deveriaDevolver404AoAtualizarReceitaComIdInexistente() throws Exception {
    int id = 1000;
    String description = "Salário 1";
    BigDecimal value = BigDecimal.valueOf(2500.00);
    String date = "2022-01-08";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    putReceita(params, 404, id);
  }

  @Test
  public void deveriaDevolver400AoAtualizarReceitaSemAlgumParametroObrigatorio() throws Exception {
    String description = "Venda do PS5";
    BigDecimal value = BigDecimal.valueOf(2885.00);
    String date = "2022-01-22";
    
    putReceita(Map.of("value", value, "date", date), 400, 1);
    putReceita(Map.of("description", description, "date", date), 400, 1);
    putReceita(Map.of("description", description, "value", value), 400, 1);
    putReceita(Map.of("value", value), 400, 1);
    putReceita(Map.of("description", description), 400, 1);
    putReceita(Map.of("date", date), 400, 1);
    putReceita(Map.of(), 400, 1);
  }

  @Test
  public void deveriaDevolver200AoDeletarUmaReceitaComIdExistente() throws Exception {
    int id = 2;
    deleteReceita(200, id);
  }
  
  @Test
  public void deveriaDevolver400AoDeletarUmaReceitaComIdInexistente() throws Exception {
    int id = 1000;
    deleteReceita(404, id);
  }

  private MvcResult deleteReceita(int status, int id) throws Exception {
    URI uri = new URI("/receitas/"+id);
    return mockMvc
        .perform(MockMvcRequestBuilders
            .delete(uri)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }

  private MvcResult putReceita(Map<String, Object> params, int status, int id) throws Exception {
    URI uri = new URI("/receitas/"+id);
    String body = new ObjectMapper().writeValueAsString(params);

    return mockMvc
        .perform(MockMvcRequestBuilders
            .put(uri)
            .content(body)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }

  private MvcResult getReceita(URI uri, int status) throws Exception {
    return mockMvc
        .perform(MockMvcRequestBuilders
            .get(uri)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }

  private MvcResult postReceita(Map<String, Object> params, int status) throws Exception{
    URI uri = new URI("/receitas");
    String body = new ObjectMapper().writeValueAsString(params);
    return mockMvc
        .perform(MockMvcRequestBuilders
          .post(uri)
          .content(body)
          .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().is(status))
        .andReturn();
  }


}
