package br.com.emendes.financesapi.util;

import java.time.format.DateTimeFormatter;

public abstract class Formatter {
  /**
   * @return DateTimeFormatter no padrão dd/MM/yyyy
   */
  public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
}
