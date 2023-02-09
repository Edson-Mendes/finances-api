package br.com.emendes.financesapi.util;

import java.time.format.DateTimeFormatter;

public abstract class Formatter {
  /**
   * @return DateTimeFormatter no padrão dd/MM/yyyy
   */
  // FIXME: Remover de todos os lugares!
  public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
}
