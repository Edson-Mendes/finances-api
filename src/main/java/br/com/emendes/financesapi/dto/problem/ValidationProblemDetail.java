package br.com.emendes.financesapi.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ValidationProblemDetail extends ProblemDetail {

  private String fields;
  private String messages;

}
