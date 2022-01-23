package br.com.emendes.financesapi.config.validation;

public class ConflictErrorDto {
  private Integer status;
  private String error;
  private String message;

  public ConflictErrorDto(Integer status, String error, String message) {
    this.status = status;
    this.error = error;
    this.message = message;
  }

  public Integer getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

}
