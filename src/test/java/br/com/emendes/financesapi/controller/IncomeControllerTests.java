package br.com.emendes.financesapi.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.emendes.financesapi.util.CustomMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IncomeControllerTests {

  @Autowired
  private CustomMockMvc mock;

  @Test
  public void deveriaDevolver201AoCriarReceita() throws Exception {
    String description = "Salário";
    BigDecimal value = new BigDecimal(2500.00);
    String date = "2022-01-05";
    
    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.post("/receitas", params, 201);
  }
  
  @Test
  public void deveriaDevolver400AoCriarSemAlgumParametroObrigatorio() throws Exception{

    String description = "Venda do PS5";
    BigDecimal value = BigDecimal.valueOf(2885.00);
    String date = "2022-01-22";
    
    mock.post("/receitas", Map.of("value", value, "date", date), 400);
    mock.post("/receitas", Map.of("description", description, "date", date), 400);
    mock.post("/receitas", Map.of("description", description, "value", value), 400);
    mock.post("/receitas", Map.of("value", value), 400);
    mock.post("/receitas", Map.of("description", description), 400);
    mock.post("/receitas", Map.of("date", date), 400);
    mock.post("/receitas", Map.of(), 400);
  }

  @Test
  public void deveriaDevolver200AoBuscarTodasAsReceitas() throws Exception {
    mock.get("/receitas", 200);
  }

  @Test
  public void deveriaDevolver409AoCadastrarDescricaoDuplicadaEmMesmoMesEAno() throws Exception{

    String description = "Lotofácil";
    BigDecimal value = BigDecimal.valueOf(500.00);
    String date = "2022-01-08";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.post("/receitas", params, 201);
    mock.post("/receitas", params, 409);

  }

  @Test
  public void deveriaDevolver201AoCadastrarDescricaoEmMesDiferentes() throws Exception{

    String description = "Trabalho por fora";
    BigDecimal value = BigDecimal.valueOf(300.00);
    String date = "2022-01-18";

    Map<String, Object> params1 = Map.of("description", description, "value", value, "date", date);
    
    String newDate = "2022-02-18";
    Map<String, Object> params2 = Map.of("description", description, "value", value, "date", newDate);
    
    mock.post("/receitas", params1, 201);
    mock.post("/receitas", params2, 201);

  }

  @Test
  public void deveriaDevolver200AoBuscarPorIdExistente() throws Exception {
    mock.get("/receitas/1", 200);
  }

  @Test
  public void deveriaDevolver200AoBuscarPorAnoEMesExistentes() throws Exception {
    mock.get("/receitas/2022/01", 200);
  }

  @Test
  public void deveriaDevolver404AoBuscarPorAnoEMesInexistentes() throws Exception {
    mock.get("/receitas/2022/03", 404);
  }

  @Test
  public void deveriaDevolver404AoBuscarPorIdInexistentes() throws Exception {
    mock.get("/receitas/999", 404);
  }

  @Test
  public void deveriaDevolver200AoBuscarPorDescricaoExistente() throws Exception{
    mock.get("/receitas?description=sal", 200);
  }

  @Test
  public void deveriaDevolver404AoBuscarPorDescricaoInexistente() throws Exception{
    mock.get("/receitas?description=salllllll", 404);
  }

  @Test
  public void deveriaDevolver200AoAtualizarReceitaCorretamente() throws Exception {
    int id = 1;
    String description = "Salário 1";
    BigDecimal value = BigDecimal.valueOf(2500.00);
    String date = "2022-01-08";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.put("/receitas/"+id, params, 200);
  }

  @Test
  public void deveriaDevolver404AoAtualizarReceitaComIdInexistente() throws Exception {
    int id = 1000;
    String description = "Salário 1";
    BigDecimal value = BigDecimal.valueOf(2500.00);
    String date = "2022-01-08";

    Map<String, Object> params = Map.of("description", description, "value", value, "date", date);

    mock.put("/receitas/"+id, params, 404);
  }

  @Test
  public void deveriaDevolver400AoAtualizarReceitaSemAlgumParametroObrigatorio() throws Exception {
    String description = "Venda do PS5";
    BigDecimal value = BigDecimal.valueOf(2885.00);
    String date = "2022-01-22";
    
    mock.put("/receitas/1", Map.of("value", value, "date", date), 400);
    mock.put("/receitas/1", Map.of("description", description, "date", date), 400);
    mock.put("/receitas/1", Map.of("description", description, "value", value), 400);
    mock.put("/receitas/1", Map.of("value", value), 400);
    mock.put("/receitas/1", Map.of("description", description), 400);
    mock.put("/receitas/1", Map.of("date", date), 400);
    mock.put("/receitas/1", Map.of(), 400);
  }

  @Test
  public void deveriaDevolver200AoDeletarUmaReceitaComIdExistente() throws Exception {
    int id = 2;
    mock.delete("/receitas/"+id, 200);
  }
  
  @Test
  public void deveriaDevolver404AoDeletarUmaReceitaComIdInexistente() throws Exception {
    int id = 1000;
    mock.delete("/receitas/"+id, 404);
  }


}
