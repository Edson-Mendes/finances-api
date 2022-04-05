package br.com.emendes.financesapi.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.data.domain.Page;

import br.com.emendes.financesapi.model.Income;
import io.swagger.v3.oas.annotations.media.Schema;

public class IncomeDto {

  @Schema(example = "10")
  private Long id;

  @Schema(example = "Salário")
  private String description;

  @JsonFormat(pattern = "dd/MM/yyyy")
  @Schema(pattern = "dd/MM/yyyy", type = "string", example = "05/01/2022")
  private LocalDate date;
  
  @Schema(example = "3500.00")
  private BigDecimal value;

  public IncomeDto() {
  }

  public IncomeDto(Income income) {
    this.id = income.getId();
    this.description = income.getDescription();
    this.date = income.getDate();
    this.value = income.getValue();
  }

  public IncomeDto(Long id, String description, LocalDate date, BigDecimal value) {
    this.id = id;
    this.description = description;
    this.date = date;
    this.value = value;
  }

  public Long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public LocalDate getDate() {
    return date;
  }

  public BigDecimal getValue() {
    return value;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    IncomeDto other = (IncomeDto) obj;

    return this.description.equals(other.getDescription())
        && this.date.equals(other.getDate())
        && this.value.equals(other.getValue());
  }

  @Override
  public int hashCode() {
    int result = 17;
    if (description != null) {
      result = result * 31 + description.hashCode();
    }
    if (date != null) {
      result = result * 31 + date.hashCode();
    }
    if (value != null) {
      result = result * 31 + value.hashCode();
    }

    return result;
  }

  public static Page<IncomeDto> convert(Page<Income> incomes) {
    return incomes.map(IncomeDto::new);
  }

  @Override
  public String toString() {
    return "IncomeDto:{id:" + id +
        ", description:" + description +
        ", date: " + date +
        ", value: " + value +
        "}";
  }
}
