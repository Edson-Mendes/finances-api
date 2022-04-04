package br.com.emendes.financesapi.util;

import java.time.format.DateTimeFormatter;

public abstract class Formatter {
  public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
}
