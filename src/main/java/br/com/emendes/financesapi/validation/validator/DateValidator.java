package br.com.emendes.financesapi.validation.validator;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.emendes.financesapi.validation.annotation.DateValidation;

public class DateValidator implements ConstraintValidator<DateValidation, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try {
      LocalDate.parse(convertDateToUSFormat(value));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Formatação necessária em
   * <b>br.com.emendes.financesapi.config.validation.annotation.DateValidation</b>
   * pois,
   * o LocalDate.parse(date,
   * <b>br.com.emendes.financesapi.util.Formatter.dateFormatter</b>)
   * estava aceitando datas inválidas como 31/04/2022.
   * 
   * <p>
   * Foi a maneira que encontrei para contornar o problema. Eventualmente vou
   * pensar em outra maneira.
   * </p>
   * 
   * @param date no formato dd/MM/yyyy
   * @return data no formato yyyy-MM-dd
   */
  private String convertDateToUSFormat(String date) {
    String[] dateArray = date.split("/");
    return dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];
  }

}
