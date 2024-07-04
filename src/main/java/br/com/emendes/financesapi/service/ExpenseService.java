package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Interface service com as abstrações para manipulação do recurso Expense.
 */
@Validated
public interface ExpenseService {

  /**
   * Registra uma {@code Expense} no sistema.
   *
   * @param expenseRequest objeto com as informações da Expense a ser registrada.
   * @return {@code ExpenseResponse} objeto com os dados resumidos da Expense registrada.
   */
  ExpenseResponse create(ExpenseRequest expenseRequest);

  /**
   * Busca paginada de Expense por usuário logado.
   *
   * @param pageable objeto que define como a paginação será feita.
   * @return {@code Page<ExpenseResponse>} objeto com a paginação de ExpenseResponse.
   * @throws EntityNotFoundException caso o usuário não tenha expenses.
   */
  Page<ExpenseResponse> readAllByUser(Pageable pageable);

  /**
   * Busca paginada de expenses por descrição similiar a {@code description} e usuário logado.
   *
   * @param description descrição similar das expenses.
   * @param pageable    objeto que define como a paginação será feita.
   * @return {@code Page<ExpenseResponse>} objeto com a paginação de ExpenseResponse.
   * @throws EntityNotFoundException caso o usuário não tenha expenses.
   */
  Page<ExpenseResponse> readByDescriptionAndUser(String description, Pageable pageable);

  /**
   * Busca expense por id e usuário logado.
   *
   * @param expenseId identificador da expense a ser buscada.
   * @return objeto ExpenseResponse com os dados da expense encontrada.
   * @throws EntityNotFoundException caso o usuário não tenha expenses.
   */
  ExpenseResponse readByIdAndUser(Long expenseId);

  /**
   * Busca paginada de expense por ano (year), mês (month) e usuário logado.
   *
   * @param year     ano da expense.
   * @param month    mês da expense.
   * @param pageable objeto que define como a paginação será feita.
   * @return {@code Page<ExpenseResponse>} objeto com a paginação de ExpenseResponse.
   * @throws EntityNotFoundException caso o usuário não tenha expenses no ano e mês informados.
   */
  Page<ExpenseResponse> readByYearAndMonthAndUser(
      @Min(value = 1970, message = "year must be equals or greater than {value}")
      @Max(value = 2099, message = "year must be equals or less than {value}") int year,
      @Min(value = 1, message = "month must be equals or greater than {value}")
      @Max(value = 12, message = "month must be equals or less than {value}") int month,
      Pageable pageable);

  /**
   * Atualiza os dados de uma expense por id. A expense deve pertencer ao usuário logado.
   *
   * @param expenseId      identificador da expense.
   * @param expenseRequest objeto com os novos dados da expense.
   * @return objeto ExpenseResponse com os dados da expense atualizada.
   * @throws EntityNotFoundException caso a expense não seja encontrada para o dado id.
   */
  ExpenseResponse update(Long expenseId, ExpenseRequest expenseRequest);

  /**
   * Deleta expense por id. A expense deve pertencer ao usuário logado.
   *
   * @param expenseId identificador da expense.
   * @throws EntityNotFoundException caso a expense não seja encontrada para o dado id.
   */
  void deleteById(Long expenseId);

  /**
   * Busca uma lista de valores por categoria de dado ano (year), mês (month) e usuário logado.
   * Cada {@link ValueByCategoryResponse} corresponde a soma de despesas para cada categoria em dado ano e mês.
   *
   * @param year  ano das expenses.
   * @param month mês das expenses.
   * @return {@code List<ValueByCategoryResponse>}.
   */
  List<ValueByCategoryResponse> getValuesByCategoryOnMonthAndYearByUser(Integer year, Integer month);

}
