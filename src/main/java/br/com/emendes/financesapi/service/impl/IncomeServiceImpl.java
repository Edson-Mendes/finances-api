package br.com.emendes.financesapi.service.impl;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.User;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class IncomeServiceImpl implements IncomeService {

  private final IncomeRepository incomeRepository;

  @Override
  public IncomeDto create(IncomeForm incomeForm) {
    Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    Income income = incomeForm.convert(userId);
    incomeRepository.save(income);

    return new IncomeDto(income);
  }

  @Override
  public Page<IncomeDto> readAllByUser(Pageable pageable) {
    Page<Income> incomes = incomeRepository.findAllByUser(pageable);

    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui receitas");
    }
    return IncomeDto.convert(incomes);
  }

  @Override
  public Page<IncomeDto> readByDescriptionAndUser(String description, Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByDescriptionAndUser(description, pageable);
    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui receitas com descrição similar a " + description);
    }
    return IncomeDto.convert(incomes);
  }

  @Override
  public IncomeDto readByIdAndUser(Long incomeId) {
    return new IncomeDto(findByIdAndUser(incomeId));
  }

  @Override
  public Page<IncomeDto> readByYearAndMonthAndUser(int year, int month, Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByYearAndMonthAndUser(year, month, pageable);
    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("Não há receitas para o ano " + year + " e mês " + month);
    }
    return IncomeDto.convert(incomes);
  }

  @Override
  public IncomeDto update(Long id, IncomeForm incomeForm) {
    Income incomeToBeUpdated = findByIdAndUser(id);

    incomeToBeUpdated.setParams(incomeForm);
    return new IncomeDto(incomeToBeUpdated);
  }

  @Override
  public void deleteById(Long id) {
    findByIdAndUser(id);
    incomeRepository.deleteById(id);
  }

  @Override
  public BigDecimal getTotalValueByMonthAndYearAndUserId(int year, int month) {
    return incomeRepository.getTotalValueByMonthAndYearAndUser(year, month).orElse(BigDecimal.ZERO);
  }

  private Income findByIdAndUser(Long id) {
    Optional<Income> optionalExpense = incomeRepository.findByIdAndUser(id);

    return optionalExpense.orElseThrow(
        () -> new NoResultException(String.format("Nenhuma receita com id = %d para esse usuário", id)));
  }

}
