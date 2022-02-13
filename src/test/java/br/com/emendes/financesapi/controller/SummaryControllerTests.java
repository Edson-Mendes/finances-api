package br.com.emendes.financesapi.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategory;
import br.com.emendes.financesapi.model.enumerator.Category;
import br.com.emendes.financesapi.util.CustomMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class SummaryControllerTests {

  @Autowired
  private CustomMockMvc mock;

  @BeforeAll
  public void populateDB() throws Exception {
    mock.post("/receitas", Map.of("description", "Salário1", "value", new BigDecimal(2500.00), "date", "2022-02-08"),
        201);
    mock.post("/receitas", Map.of("description", "Salário2", "value", new BigDecimal(1500.00), "date", "2022-02-10"),
        201);

    mock.post("/despesas",
        Map.of("description", "Aluguel", "value", new BigDecimal(1000.00), "date", "2022-02-05", "category", "MORADIA"),
        201);
    mock.post("/despesas",
        Map.of("description", "Alimentação", "value", new BigDecimal(800.00), "date", "2022-02-28", "category",
            "ALIMENTACAO"),
        201);
    mock.post("/despesas",
        Map.of("description", "Farmácia", "value", new BigDecimal(150.00), "date", "2022-02-28", "category", "SAUDE"),
        201);

  }

  @Test
  public void deveriaDevolver200AoBuscarPorResumoMensal() throws Exception {
    int year = 2022;
    int month = 2;
    mock.get("/resumo/" + year + "/" + month, 200);
  }

  @Test
  public void deveriaDevolver404AoBuscarPorResumoInexistente() throws Exception {
    int year = 2022;
    int month = 5;
    mock.get("/resumo/" + year + "/" + month, 404);
  }

  @Test
  public void summaryDtoRecebidoDeveriaSerIgualAoSummaryDtoComparado() throws Exception {
    int year = 2022;
    int month = 2;
    MvcResult result = mock.get("/resumo/" + year + "/" + month, 200);
    String content = result.getResponse().getContentAsString();
    SummaryDto summaryResult = SummaryDtoFromJsonNode(new ObjectMapper().readTree(content));

    List<ValueByCategory> valuesByCategory = new ArrayList<>();
    // FIXME: Refatorar as adicões abaixo.
    valuesByCategory.add(new ValueByCategory(Category.ALIMENTACAO, new BigDecimal("800.0")));
    valuesByCategory.add(new ValueByCategory(Category.SAUDE, new BigDecimal("150.0")));
    valuesByCategory.add(new ValueByCategory(Category.MORADIA, new BigDecimal("1000.0")));
    valuesByCategory.add(new ValueByCategory(Category.TRANSPORTE, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.EDUCACAO, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.LAZER, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.IMPREVISTOS, new BigDecimal("0")));
    valuesByCategory.add(new ValueByCategory(Category.OUTRAS, new BigDecimal("0")));

    SummaryDto summaryExpected = new SummaryDto(new BigDecimal("4000.0"), new BigDecimal("1950.0"), valuesByCategory);

    Assertions.assertEquals(summaryExpected, summaryResult);
  }

  @Test
  public void deveriaDevolver400AoBuscarComAnoNaoNumerico() throws Exception {
    mock.get("/resumo/2O22/12", 400);
  }

  private SummaryDto SummaryDtoFromJsonNode(JsonNode content) throws Exception {
    BigDecimal incomeTotalValue = content.get("incomeTotalValue").decimalValue();
    BigDecimal expenseTotalValue = content.get("expenseTotalValue").decimalValue();
    // BigDecimal finalBalance = content.get("finalBalance").decimalValue();

    List<String> categories = content.get("valuesByCategory").findValuesAsText("category");
    List<String> values = content.get("valuesByCategory").findValuesAsText("value");
    List<ValueByCategory> valuesByCategory = new ArrayList<>();
    for (int i = 0; i < categories.size(); i++) {
      ValueByCategory valueByCategory = new ValueByCategory(Category.valueOf(categories.get(i)),
          new BigDecimal(values.get(i)));
      valuesByCategory.add(valueByCategory);
    }

    return new SummaryDto(incomeTotalValue, expenseTotalValue, valuesByCategory);
  }

}
