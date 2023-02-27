package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.dto.response.IncomeResponse;
import br.com.emendes.financesapi.dto.request.IncomeRequest;
import br.com.emendes.financesapi.exception.EntityNotFoundException;
import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.service.IncomeService;
import br.com.emendes.financesapi.util.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Month;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class IncomeServiceImpl implements IncomeService {

  private final IncomeRepository incomeRepository;
  private final AuthenticationFacade authenticationFacade;

  @Override
  public IncomeResponse create(IncomeRequest incomeRequest) {
    Authentication authentication = authenticationFacade.getAuthentication();

    Long userId = ((User) authentication.getPrincipal()).getId();

    Income income = incomeRequest.convert(userId);
    incomeRepository.save(income);

    return new IncomeResponse(income);
  }

  @Override
  public Page<IncomeResponse> readAllByUser(Pageable pageable) {
    Page<Income> incomes = incomeRepository.findAllByUser(pageable);

    if (incomes.getTotalElements() == 0) {
      throw new EntityNotFoundException("The user has no incomes");
    }
    return IncomeResponse.convert(incomes);
  }

  @Override
  public Page<IncomeResponse> readByDescriptionAndUser(String description, Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByDescriptionAndUser(description, pageable);
    if (incomes.getTotalElements() == 0) {
      throw new EntityNotFoundException("The user has no incomes with a description similar to " + description);
    }
    return IncomeResponse.convert(incomes);
  }

  @Override
  public IncomeResponse readByIdAndUser(Long incomeId) {
    return new IncomeResponse(findByIdAndUser(incomeId));
  }

  @Override
  public Page<IncomeResponse> readByYearAndMonthAndUser(int year, int month, Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByYearAndMonthAndUser(year, month, pageable);
    if (incomes.getTotalElements() == 0) {
      throw new EntityNotFoundException(String.format("Has no incomes for year %d and month %s", year, Month.of(month)));
    }
    return IncomeResponse.convert(incomes);
  }

  @Override
  public IncomeResponse update(Long id, IncomeRequest incomeRequest) {
    Income incomeToBeUpdated = findByIdAndUser(id);

    incomeToBeUpdated.setParams(incomeRequest);
    return new IncomeResponse(incomeToBeUpdated);
  }

  @Override
  public void deleteById(Long id) {
    incomeRepository.delete(findByIdAndUser(id));
  }

  @Override
  public BigDecimal getTotalValueByMonthAndYearAndUserId(int year, int month) {
    return incomeRepository.getTotalValueByMonthAndYearAndUser(year, month).orElse(BigDecimal.ZERO);
  }

  private Income findByIdAndUser(Long id) {
    Optional<Income> optionalExpense = incomeRepository.findByIdAndUser(id);

    return optionalExpense.orElseThrow(
        () -> new EntityNotFoundException("Income not found"));
  }

}
