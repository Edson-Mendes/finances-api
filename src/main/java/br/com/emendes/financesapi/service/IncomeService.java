package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.model.Income;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.repository.UserRepository;
import br.com.emendes.financesapi.util.Formatter;

@Service
public class IncomeService {

  @Autowired
  private IncomeRepository incomeRepository;

  @Autowired
  private UserRepository userRepository;

  public ResponseEntity<IncomeDto> create(IncomeForm form, Long userId, UriComponentsBuilder uriBuilder) {
    User user = userRepository.findById(userId).get();
    Income income = form.convert(incomeRepository, userId);
    income.setUser(user);

    incomeRepository.save(income);

    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(income.getId()).toUri();
    return ResponseEntity.created(uri).body(new IncomeDto(income));
  }

  public ResponseEntity<Page<IncomeDto>> readAllByUser(Long userid, Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByUserId(userid, pageable);

    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui receitas");
    }
    Page<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(incomesDto);
  }

  public ResponseEntity<Page<IncomeDto>> readByDescriptionAndUser(String description, Long userid, Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByDescriptionAndUserId(description, userid, pageable);
    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("O usuário não possui receitas com descrição similar a " + description);
    }
    Page<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(incomesDto);
  }

  public ResponseEntity<IncomeDto> readByIdAndUser(Long incomeId, Long userId) {
    Optional<Income> optional = incomeRepository.findByIdAndUserId(incomeId, userId);
    if (optional.isEmpty()) {
      throw new NoResultException("Nenhuma receita com esse id para esse usuário");
    }
    IncomeDto incomeDto = new IncomeDto(optional.get());
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(incomeDto);
  }

  public ResponseEntity<Page<IncomeDto>> readByYearAndMonthAndUser(Integer year, Integer month, Long userId,
      Pageable pageable) {
    Page<Income> incomes = incomeRepository.findByYearAndMonthAndUserId(year, month, userId, pageable);
    if (incomes.getTotalElements() == 0) {
      throw new NoResultException("Não há receitas para o ano " + year + " e mês " + month);
    }
    Page<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(incomesDto);
  }

  public ResponseEntity<IncomeDto> update(Long id, IncomeForm incomeForm, Long userId) {
    Optional<Income> optional = incomeRepository.findByIdAndUserId(id, userId);
    if (optional.isPresent() && !incomeForm.alreadyExist(incomeRepository, id, userId)) {
      Income income = optional.get();

      income.setDescription(incomeForm.getDescription());
      income.setValue(incomeForm.getValue());
      income.setDate(LocalDate.parse(incomeForm.getDate(), Formatter.dateFormatter));

      return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json;charset=UTF-8")
          .body(new IncomeDto(income));
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

}
