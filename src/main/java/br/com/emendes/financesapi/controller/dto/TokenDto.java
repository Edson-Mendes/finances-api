package br.com.emendes.financesapi.controller.dto;

public class TokenDto {
  
  private String token;
  private String type;

  public TokenDto(String token, String type){
    this.token = token;
    this.type = type;
  }

  public String getToken() {
    return token;
  }

  public String getType() {
    return type;
  }

  public String getTypeWithToken(){
    return type+" "+token;
  }

}
