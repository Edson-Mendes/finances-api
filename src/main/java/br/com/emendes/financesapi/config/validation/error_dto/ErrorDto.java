package br.com.emendes.financesapi.config.validation.error_dto;

public class ErrorDto {
  private String error;
  private String message;

  public ErrorDto(String error, String message) {
    this.error = error;
    this.message = message;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "[ErrorDto: error="+error+", message="+message+"]";
  }

}
