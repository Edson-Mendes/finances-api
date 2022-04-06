package br.com.emendes.financesapi.controller.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

public class FormErrorDto {

  @Schema(example = "date")
  private String field;

  @Schema(example = "A data informada é inválida!")
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
    return "FormErrorDto: {field: "+field+", error: "+error+"}";
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj){
      return true;
    }
    if(obj == null || obj.getClass() != getClass()){
      return false;
    }
    FormErrorDto other = (FormErrorDto) obj;
    return other.getField().equals(this.field) && other.getError().equals(this.error);
  }

  @Override
  public int hashCode() {
    int result = 1;
    if(field != null){
      result = result * 31 + field.hashCode();
    }
    if(error != null){
      result = result * 31 + error.hashCode();
    }
    return result;
  }

}
