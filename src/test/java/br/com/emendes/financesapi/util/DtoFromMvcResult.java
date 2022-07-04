package br.com.emendes.financesapi.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.web.servlet.MvcResult;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.dto.RoleDto;
import br.com.emendes.financesapi.controller.dto.SummaryDto;
import br.com.emendes.financesapi.controller.dto.TokenDto;
import br.com.emendes.financesapi.controller.dto.UserDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategoryDto;
import br.com.emendes.financesapi.controller.dto.error.ErrorDto;
import br.com.emendes.financesapi.controller.dto.error.FormErrorDto;
import br.com.emendes.financesapi.model.enumerator.Category;

public abstract class DtoFromMvcResult {

  public static List<FormErrorDto> formErrorDto(MvcResult result) {
    List<FormErrorDto> listFormErrorDto = new ArrayList<>();
    JsonNode content = getJsonNodeFromMvcResult(result);

    content.elements().forEachRemaining(e -> {
      String field = e.get("field").asText();
      String error = e.get("error").asText();
      listFormErrorDto.add(new FormErrorDto(field, error));
    });

    return listFormErrorDto;
  }

  public static UserDto userDto(MvcResult result) {
    JsonNode content = getJsonNodeFromMvcResult(result);
    return userDto(content);
  }

  public static ErrorDto errorDto(MvcResult result) {
    JsonNode content = getJsonNodeFromMvcResult(result);

    String error = content.get("error").asText();
    String message = content.get("message").asText();

    ErrorDto errorDto = new ErrorDto(error, message);
    return errorDto;
  }

  public static TokenDto tokenDto(MvcResult result) {
    JsonNode content = getJsonNodeFromMvcResult(result);

    String token = content.get("token").asText();
    String type = content.get("type").asText();

    TokenDto tokenDto = new TokenDto(token, type);
    return tokenDto;
  }

  public static List<IncomeDto> listIncomeDto(MvcResult result) {
    JsonNode content = getJsonNodeFromMvcResult(result);

    Iterator<JsonNode> elements = content.get("content").elements();
    List<IncomeDto> incomesDto = new ArrayList<>();
    
    while (elements.hasNext()) {
      JsonNode incomeJson = elements.next();

      incomesDto.add(incomeDto(incomeJson));
    }

    return incomesDto;
  }

  public static IncomeDto incomeDto(JsonNode content) {
    Long id = content.get("id").asLong();
    String description = content.get("description").asText();
    LocalDate date = LocalDate.parse(content.get("date").asText(), Formatter.dateFormatter);
    BigDecimal value = content.get("value").decimalValue();

    IncomeDto incomeDto = new IncomeDto(id, description, date, value);
    return incomeDto;
  }

  public static List<ExpenseDto> listExpenseDto(MvcResult result) {
    JsonNode content = getJsonNodeFromMvcResult(result);

    Iterator<JsonNode> elements = content.get("content").elements();
    List<ExpenseDto> expensesDto = new ArrayList<>();
    
    while (elements.hasNext()) {
      JsonNode expenseJson = elements.next();

      expensesDto.add(expenseDto(expenseJson));
    }

    return expensesDto;
  }

  public static ExpenseDto expenseDto(JsonNode content) {
    Long id = content.get("id").asLong();
    String description = content.get("description").asText();
    LocalDate date = LocalDate.parse(content.get("date").asText(), Formatter.dateFormatter);
    BigDecimal value = content.get("value").decimalValue();
    Category category = Category.valueOf(content.get("category").asText());

    ExpenseDto expenseDto = new ExpenseDto(id, description, date, value, category);
    return expenseDto;
  }

  public static SummaryDto summaryDto(MvcResult result) {
    JsonNode content = getJsonNodeFromMvcResult(result);
    BigDecimal incomeTotalValue = content.get("incomeTotalValue").decimalValue();
    BigDecimal expenseTotalValue = content.get("expenseTotalValue").decimalValue();

    List<String> categories = content.get("valuesByCategory").findValuesAsText("category");
    List<String> values = content.get("valuesByCategory").findValuesAsText("value");
    List<ValueByCategoryDto> valuesByCategory = new ArrayList<>();
    for (int i = 0; i < categories.size(); i++) {
      ValueByCategoryDto valueByCategory = new ValueByCategoryDto(Category.valueOf(categories.get(i)),
          new BigDecimal(values.get(i)));
      valuesByCategory.add(valueByCategory);
    }

    return new SummaryDto(incomeTotalValue, expenseTotalValue, valuesByCategory);
  }

  public static ExpenseDto expenseDto(MvcResult result) {
    JsonNode content = getJsonNodeFromMvcResult(result);
    return expenseDto(content);
  }

  public static RoleDto roleDto(MvcResult result) {
    JsonNode content = getJsonNodeFromMvcResult(result);

    Long id = content.get("id").asLong();
    String name = content.get("name").asText();

    RoleDto roleDto = new RoleDto(id, name);
    return roleDto;
  }

  public static List<UserDto> listUserDto(MvcResult result) {
    List<UserDto> listUserDto = new ArrayList<UserDto>();

    JsonNode content = getJsonNodeFromMvcResult(result);
    Iterator<JsonNode> elements = content.get("content").elements();

    while (elements.hasNext()) {
      JsonNode expenseJson = elements.next();

      listUserDto.add(userDto(expenseJson));
    }

    return listUserDto;
  }

  private static UserDto userDto(JsonNode content) {
    Long id = content.get("id").asLong();
    String name = content.get("name").asText();
    String email = content.get("email").asText();

    UserDto user = new UserDto(id, name, email);
    return user;
  }
  
  private static JsonNode getJsonNodeFromMvcResult(MvcResult result){
    try{
      JsonNode content = new ObjectMapper().readTree(result.getResponse().getContentAsString());
      return content;
    }catch(Exception e){
      throw new RuntimeException("Não foi possível gerar o JsonNode");
    }
  }

}
