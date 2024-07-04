package br.com.emendes.financesapi.mapper;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.model.entity.Income;

/**
 * Interface component com as abstrações para mapeamento do recurso Income.
 */
public interface IncomeMapper {

  /**
   * Mapeia um objeto {@link IncomeRequest} para {@link Income}.
   *
   * @param incomeRequest objeto a ser mapeado.
   * @return Objeto Income.
   * @throws IllegalArgumentException caso incomeRequest seja null.
   */
  Income toIncome(IncomeRequest incomeRequest);

  /**
   * Mapeia um objeto {@link Income} para {@link IncomeResponse}.
   *
   * @param income objeto a ser mapeado.
   * @return Objeto IncomeResponse.
   * @throws IllegalArgumentException caso income seja null.
   */
  IncomeResponse toIncomeResponse(Income income);

  /**
   * Mescla os dados de income com os dados de incomeRequest.
   *
   * @param income        objeto que receberá novos dados.
   * @param incomeRequest objeto fonte dos novos dados.
   * @throws IllegalArgumentException caso income ou incomeRequest sejam nulos.
   */
  void merge(Income income, IncomeRequest incomeRequest);

}
