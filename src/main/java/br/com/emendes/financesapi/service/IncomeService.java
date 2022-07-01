package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.NoResultException;

import br.com.emendes.financesapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.model.Income;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.util.Formatter;

@Service
public class IncomeService {

  @Autowired
  private IncomeRepository incomeRepository;

  public IncomeDto create(IncomeForm incomeForm) {
    alreadyExist(incomeForm);

    Long userId = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    Income income = incomeForm.convert(userId);
    incomeRepository.save(income);

    return new IncomeDto(income);
  }

  public Page<IncomeDto> readAllByUser(Pageable pageable) {
    Page<Income> incomes = incomeRepository.findAllByUser(pageable);

    if (incomes.isEmpty()) {
      throw new NoResultException("O usuário não possui receitas");
    }
    return IncomeDto.convert(incomes);
  }

  public Page<IncomeDto> readByDescriptionAndUser(String description, Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByDescriptionAndUser(description, pageable);
    if (incomes.isEmpty()) {
      throw new NoResultException("O usuário não possui receitas com descrição similar a " + description);
    }
    return IncomeDto.convert(incomes);
  }

  public IncomeDto readByIdAndUser(Long incomeId) {
    Optional<Income> optional = incomeRepository.findByIdAndUser(incomeId);
    return new IncomeDto(optional.orElseThrow(() -> {
      throw new NoResultException("Nenhuma receita com esse id para esse usuário");
    }));
  }

  public Page<IncomeDto> readByYearAndMonthAndUser(Integer year, Integer month,
      Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByYearAndMonthAndUser(year, month, pageable);
    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("Não há receitas para o ano " + year + " e mês " + month);
    }
    return IncomeDto.convert(incomes);
  }

  public IncomeDto update(Long id, IncomeForm incomeForm) {
    Optional<Income> optional = incomeRepository.findByIdAndUser(id);
    if (optional.isPresent() && !alreadyExist(incomeForm, id)) {
      Income income = optional.get();

      income.setDescription(incomeForm.getDescription());
      income.setValue(incomeForm.getValue());
      income.setDate(LocalDate.parse(incomeForm.getDate(), Formatter.dateFormatter));

      return new IncomeDto(income);
    }

    throw new NoResultException("Nenhuma receita com esse id para esse usuário");
  }

  public void delete(Long id) {
    Optional<Income> optional = incomeRepository.findByIdAndUser(id);
    if (optional.isEmpty()) {
      throw new NoResultException("Nenhuma receita com esse id para esse usuário");
    }

    incomeRepository.deleteById(id);
  }

  public Optional<BigDecimal> getTotalValueByMonthAndYearAndUserId(Integer year, Integer month) {
    return incomeRepository.getTotalValueByMonthAndYearAndUser(year, month);
  }

  /**
   * Verifica se o usuário já possui outra receita com a mesma descrição no mesmo
   * mês
   * e ano.
   * 
   * @param incomeForm
   * @return false, se não existir uma receita com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir receita.
   */
  public boolean alreadyExist(IncomeForm incomeForm) {
    LocalDate date = incomeForm.parseDateToLocalDate();
    Optional<Income> optional = incomeRepository.findByDescriptionAndMonthAndYearAndUser(incomeForm.getDescription(),
        date.getMonthValue(),
        date.getYear());
    if (optional.isPresent()) {
      String message = "Uma receita com essa descrição já existe em " + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }

  /**
   * Verifica se o usuário já possui outra receita com a mesma descrição no mesmo
   * mês e
   * ano da respectiva receita e com id diferente do mesmo.
   * 
   * @param incomeForm
   * @param incomeId
   * @return false, se não existir uma receita com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir receita.
   */
  public boolean alreadyExist(IncomeForm incomeForm, Long incomeId) {
    LocalDate date = LocalDate.parse(incomeForm.getDate(), Formatter.dateFormatter);
    Optional<Income> optional = incomeRepository.findByDescriptionAndMonthAndYearAndNotIdAndUser(
        incomeForm.getDescription(),
        date.getMonthValue(), date.getYear(), incomeId);
    if (optional.isPresent()) {
      String message = "Descrição de receita duplicada para " + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }
}
