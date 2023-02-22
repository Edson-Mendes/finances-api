package br.com.emendes.financesapi.dto.problem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProblemDetail {

  private URI type;
  private String title;
  private String detail;
  private int status;
  private LocalDateTime timestamp;

}
