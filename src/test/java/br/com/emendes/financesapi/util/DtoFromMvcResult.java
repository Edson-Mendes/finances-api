package br.com.emendes.financesapi.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.web.servlet.MvcResult;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.config.validation.error_dto.FormErrorDto;
import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategory;
import br.com.emendes.financesapi.model.enumerator.Category;

public abstract class DtoFromMvcResult {

  public static FormErrorDto formErrorDto(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());
    JsonNode errorJsonNode = content.elements().next();
    String field = errorJsonNode.get("field").asText();
    String error = errorJsonNode.get("error").asText();

    FormErrorDto formErrorDto = new FormErrorDto(field, error);

    return formErrorDto;
  }

  public static UserDto userDto(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());

    Long id = content.get("id").asLong();
    String name = content.get("name").asText();
    String email = content.get("email").asText();

    UserDto user = new UserDto(id, name, email);
    return user;
  }

  public static ErrorDto errorDto(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());

    String error = content.get("error").asText();
    String message = content.get("message").asText();

    ErrorDto errorDto = new ErrorDto(error, message);
    return errorDto;
  }

  public static TokenDto tokenDto(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());

    String token = content.get("token").asText();
    String type = content.get("type").asText();

    TokenDto tokenDto = new TokenDto(token, type);
    return tokenDto;
  }

  public static List<IncomeDto> listIncomeDto(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());

    Iterator<JsonNode> elements = content.elements();
    List<IncomeDto> incomesDto = new ArrayList<>();
    
    while (elements.hasNext()) {
      JsonNode incomeJson = elements.next();

      incomesDto.add(incomeDto(incomeJson));
    }

    return incomesDto;
  }

  public static IncomeDto incomeDto(JsonNode incomeJson) {
    Long id = incomeJson.get("id").asLong();
    String description = incomeJson.get("description").asText();
    LocalDate date = LocalDate.parse(incomeJson.get("date").asText());
    BigDecimal value = incomeJson.get("value").decimalValue();

    IncomeDto incomeDto = new IncomeDto();
    incomeDto.setId(id);
    incomeDto.setDescription(description);
    incomeDto.setDate(date);
    incomeDto.setValue(value);

    return incomeDto;
  }

  public static List<ExpenseDto> listExpenseDto(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());

    Iterator<JsonNode> elements = content.elements();
    List<ExpenseDto> expensesDto = new ArrayList<>();
    
    while (elements.hasNext()) {
      JsonNode expenseJson = elements.next();

      expensesDto.add(expenseDto(expenseJson));
    }

    return expensesDto;
  }

  public static ExpenseDto expenseDto(JsonNode expenseJson) {
    Long id = expenseJson.get("id").asLong();
    String description = expenseJson.get("description").asText();
    LocalDate date = LocalDate.parse(expenseJson.get("date").asText());
    BigDecimal value = expenseJson.get("value").decimalValue();
    Category category = Category.valueOf(expenseJson.get("category").asText());

    ExpenseDto expenseDto = new ExpenseDto();
    expenseDto.setId(id);
    expenseDto.setDescription(description);
    expenseDto.setDate(date);
    expenseDto.setValue(value);
    expenseDto.setCategory(category);

    return expenseDto;
  }

  public static SummaryDto summaryDtoFromJsonNode(MvcResult result) throws Exception {
    JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());
    BigDecimal incomeTotalValue = content.get("incomeTotalValue").decimalValue();
    BigDecimal expenseTotalValue = content.get("expenseTotalValue").decimalValue();

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
