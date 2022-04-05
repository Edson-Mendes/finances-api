package br.com.emendes.financesapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class TokenDto {
  
  @Schema(example = "3x3mp10-70k3n-p4r4-4u73n71c4c40")
  private String token;

  @Schema(example = "Bearer")
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

  public String generateTypeWithToken(){
    return type+" "+token;
  }

}
