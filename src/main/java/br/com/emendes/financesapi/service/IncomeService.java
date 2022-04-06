package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

  public IncomeDto create(IncomeForm incomeForm, Long userId) {
    alreadyExist(incomeForm, userId);
    Income income = incomeForm.convert(userId);
    incomeRepository.save(income);

    return new IncomeDto(income);
  }

  public Page<IncomeDto> readAllByUser(Long userid, Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByUserId(userid, pageable);

    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui receitas");
    }
    Page<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return incomesDto;
  }

  public Page<IncomeDto> readByDescriptionAndUser(String description, Long userid, Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByDescriptionAndUserId(description, userid, pageable);
    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui receitas com descrição similar a " + description);
    }
    Page<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return incomesDto;
  }

  public IncomeDto readByIdAndUser(Long incomeId, Long userId) {
    Optional<Income> optional = incomeRepository.findByIdAndUserId(incomeId, userId);
    if (optional.isEmpty()) {
      throw new NoResultException("Nenhuma receita com esse id para esse usuário");
    }
    IncomeDto incomeDto = new IncomeDto(optional.get());
    
    return incomeDto;
  }

  public Page<IncomeDto> readByYearAndMonthAndUser(Integer year, Integer month, Long userId,
      Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);
    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("Não há receitas para o ano " + year + " e mês " + month);
    }
    Page<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    
    return incomesDto;
  }

  public IncomeDto update(Long id, IncomeForm incomeForm, Long userId) {
    Optional<Income> optional = incomeRepository.findByIdAndUserId(id, userId);
    if (optional.isPresent() && !alreadyExist(incomeForm, id, userId)) {
      Income income = optional.get();

      income.setDescription(incomeForm.getDescription());
      income.setValue(incomeForm.getValue());
      income.setDate(LocalDate.parse(incomeForm.getDate(), Formatter.dateFormatter));

      return new IncomeDto(income);
    }

    throw new NoResultException("Nenhuma receita com esse id para esse usuário");
  }

  public void delete(Long id, Long userId) {
    Optional<Income> optional = incomeRepository.findByIdAndUserId(id, userId);
    if (optional.isEmpty()) {
      throw new NoResultException("Nenhuma receita com esse id para esse usuário");
    }

    incomeRepository.deleteById(id);
  }

  public Optional<BigDecimal> getTotalValueByMonthAndYearAndUserId(Integer year, Integer month, Long userId) {
    return incomeRepository.getTotalValueByMonthAndYearAndUserId(year, month, userId);
  }

  /**
   * Verifica se o usuário já possui outra receita com a mesma descrição no mesmo
   * mês
   * e ano.
   * 
   * @param incomeRepository
   * @param userId
   * @return false, se não existir uma receita com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir receita.
   */
  public boolean alreadyExist(IncomeForm incomeForm, Long userId) {
    LocalDate date = LocalDate.parse(incomeForm.getDate(), Formatter.dateFormatter);
    Optional<Income> optional = incomeRepository.findByDescriptionAndMonthAndYearAndUserId(incomeForm.getDescription(),
        date.getMonthValue(),
        date.getYear(), userId);
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
   * @param incomeRepository
   * @param id
   * @param userId
   * @return false, se não existir uma receita com a mesma descrição em um mesmo
   *         mês e ano.
   * @throws ResponseStatusException se existir receita.
   */
  public boolean alreadyExist(IncomeForm incomeForm, Long incomeId, Long userId) {
    LocalDate date = LocalDate.parse(incomeForm.getDate(), Formatter.dateFormatter);
    Optional<Income> optional = incomeRepository.findByDescriptionAndMonthAndYearAndUserIdAndNotId(
        incomeForm.getDescription(),
        date.getMonthValue(), date.getYear(), userId, incomeId);
    if (optional.isPresent()) {
      String message = "Descrição de receita duplicada para " + date.getMonth().name().toLowerCase() + " "
          + date.getYear();
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, message, null);
    }
    return false;
  }
}
