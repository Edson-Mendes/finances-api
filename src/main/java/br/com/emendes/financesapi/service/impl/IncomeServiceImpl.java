package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.mapper.IncomeMapper;
import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.service.IncomeService;
import br.com.emendes.financesapi.util.component.CurrentAuthenticationComponent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class IncomeServiceImpl implements IncomeService {

  private final IncomeRepository incomeRepository;
  private final CurrentAuthenticationComponent currentAuthenticationComponent;
  private final IncomeMapper incomeMapper;

  @Override
  public IncomeResponse create(IncomeRequest incomeRequest) {
    log.info("attempt to create new income.");
    User currentUser = currentAuthenticationComponent.getCurrentUser();

    Income income = incomeMapper.toIncome(incomeRequest);
    income.setUser(currentUser);

    incomeRepository.save(income);
    return incomeMapper.toIncomeResponse(income);
  }

  @Override
  public Page<IncomeResponse> readAllByUser(Pageable pageable) {
    User currentUser = currentAuthenticationComponent.getCurrentUser();
    log.info("attempt to read incomes for user with id: {}.", currentUser.getId());

    Page<Income> incomePage = incomeRepository.findAllByUser(currentUser, pageable);
    if (incomePage.getTotalElements() == 0) {
      throw new EntityNotFoundException("The user has no incomes");
    }

    return incomePage.map(incomeMapper::toIncomeResponse);
  }

  @Override
  public Page<IncomeResponse> readByDescriptionAndUser(String description, Pageable pageable) {
    log.info("attempt to read income by description");

    User currentUser = currentAuthenticationComponent.getCurrentUser();
    Page<Income> incomePage = incomeRepository.findByDescriptionAndUser(description, currentUser, pageable);
    if (incomePage.getTotalElements() == 0) {
      throw new EntityNotFoundException("The user has no incomes with a description similar to " + description);
    }
    return incomePage.map(incomeMapper::toIncomeResponse);
  }

  @Override
  public IncomeResponse readByIdAndUser(Long incomeId) {
    log.info("attempt to read income by id.");
    return incomeMapper.toIncomeResponse(findByIdAndUser(incomeId));
  }

  @Override
  public Page<IncomeResponse> readByYearAndMonthAndUser(int year, int month, Pageable pageable) {
    log.info("attempt to read income by year and month.");
    User currentUser = currentAuthenticationComponent.getCurrentUser();
    Page<Income> incomePage = incomeRepository.findByYearAndMonthAndUser(year, month, currentUser, pageable);

    if (incomePage.getTotalElements() == 0) {
      throw new EntityNotFoundException(String.format("Has no incomes for year %d and month %s", year, Month.of(month)));
    }
    return incomePage.map(incomeMapper::toIncomeResponse);
  }

  @Override
  @Transactional
  public IncomeResponse update(Long id, IncomeRequest incomeRequest) {
    log.info("attempt to update income with id: {}", id);
    Income incomeToBeUpdated = findByIdAndUser(id);

    incomeMapper.merge(incomeToBeUpdated, incomeRequest);
    return incomeMapper.toIncomeResponse(incomeToBeUpdated);
  }

  @Override
  public void deleteById(Long id) {
    log.info("attempt to delete income with id: {}", id);

    incomeRepository.delete(findByIdAndUser(id));
  }

  @Override
  public BigDecimal getTotalValueByMonthAndYearAndUserId(int year, int month) {
    log.info("attempt to get total value of incomes for year: {} and month: {}", year, month);

    User currentUser = currentAuthenticationComponent.getCurrentUser();
    return incomeRepository.getTotalValueByMonthAndYearAndUser(year, month, currentUser).orElse(BigDecimal.ZERO);
  }

  /**
   * Busca Income por id e user, sendo que o user usado na busca é o usuário logado na requisição atual.
   *
   * @param id identificador da income a ser buscada.
   * @return Income encontrada para o dado id e user autenticado.
   * @throws EntityNotFoundException caso não seja encontrada nenhuma income para o dado id e user autenticado.
   */
  private Income findByIdAndUser(Long id) {
    log.info("attempt to find income with id: {}", id);
    User currentUser = currentAuthenticationComponent.getCurrentUser();
    Optional<Income> optionalIncome = incomeRepository.findByIdAndUser(id, currentUser);

    return optionalIncome.orElseThrow(
        () -> new EntityNotFoundException(String.format("Income not found with id: %d", id)));
  }

}
