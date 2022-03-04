package br.com.emendes.financesapi.config.validation.error_dto;

public class FormErrorDto {

  private String field;
  private String error;

  public FormErrorDto(String field, String error) {
    this.field = field;
    this.error = error;
  }

  public String getField() {
    return field;
  }

  public String getError() {
    return error;
  }

  @Override
  public String toString() {
    return "[FormErrorDto: field="+field+", error="+error+"]";
  }

}
