package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * Interface service com as abstrações para manipulação do recurso Income.
 */
@Validated
public interface IncomeService {

  /**
   * Registra uma {@code Income} no sistema.
   *
   * @param incomeRequest objeto com as informações da Income a ser registrada.
   * @return {@code IncomeResponse} objeto com os dados resumidos da Income registrada.
   */
  IncomeResponse create(IncomeRequest incomeRequest);

  /**
   * Busca paginada de Income por usuário logado.
   *
   * @param pageable objeto que define como a paginação será feita.
   * @return {@code Page<IncomeResponse>} objeto com a paginação de IncomeResponse.
   * @throws EntityNotFoundException caso o usuário não tenha incomes.
   */
  Page<IncomeResponse> readAllByUser(Pageable pageable);

  /**
   * Busca paginada de incomes por descrição similiar a {@code description} e usuário logado.
   *
   * @param description descrição similar das incomes.
   * @param pageable    objeto que define como a paginação será feita.
   * @return {@code Page<IncomeResponse>} objeto com a paginação de IncomeResponse.
   * @throws EntityNotFoundException caso o usuário não tenha incomes.
   */
  Page<IncomeResponse> readByDescriptionAndUser(String description, Pageable pageable);

  /**
   * Busca income por id e usuário logado.
   *
   * @param incomeId identificador da income a ser buscada.
   * @return objeto IncomeResponse com os dados da income encontrada.
   * @throws EntityNotFoundException caso o usuário não tenha incomes.
   */
  IncomeResponse readByIdAndUser(Long incomeId);

  /**
   * Busca paginada de income por ano (year), mês (month) e usário logado.
   *
   * @param year     ano da income.
   * @param month    mês da income.
   * @param pageable objeto que define como a paginação será feita.
   * @return {@code Page<IncomeResponse>} objeto com a paginação de IncomeResponse.
   * @throws EntityNotFoundException caso o usuário não tenha incomes no ano e mês informados.
   */
  Page<IncomeResponse> readByYearAndMonthAndUser(
      @Min(value = 1970, message = "year must be equals or greater than {value}")
      @Max(value = 2099, message = "year must be equals or less than {value}") int year,
      @Min(value = 1, message = "month must be equals or greater than {value}")
      @Max(value = 12, message = "month must be equals or less than {value}") int month,
      Pageable pageable);

  /**
   * Atualiza os dados de uma income por id. A income deve pertencer ao usuário logado.
   *
   * @param id            identificador da income.
   * @param incomeRequest objeto com os novos dados da income.
   * @return objeto IncomeResponse com os dados da income atualizada.
   * @throws EntityNotFoundException caso a income não seja encontrada para o dado id.
   */
  IncomeResponse update(Long id, IncomeRequest incomeRequest);

  /**
   * Deleta income por id. A income deve pertencer ao usuário logado.
   *
   * @param id identificador da income.
   * @throws EntityNotFoundException caso a income não seja encontrada para o dado id.
   */
  void deleteById(Long id);

  /**
   * Busca a soma de incomes para um dado ano (year) e mês (month) e que pertençam ao usuário logado.
   *
   * @param year  ano que as incomes devem ter.
   * @param month mês que as incomes devem ter.
   * @return soma dos valores da incomes encontradas para os dados year e month, {@code BigDecimal.ZERO} caso
   * não sejam encontradas incomes.
   */
  BigDecimal getTotalValueByMonthAndYearAndUserId(int year, int month);

}
