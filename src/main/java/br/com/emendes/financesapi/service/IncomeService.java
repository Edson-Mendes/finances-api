package br.com.emendes.financesapi.service;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.config.validation.error_dto.ErrorDto;
import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.model.Income;
import br.com.emendes.financesapi.model.User;
import br.com.emendes.financesapi.repository.IncomeRepository;
import br.com.emendes.financesapi.repository.UserRepository;

// TODO Repensar esses retornos em exclamação (?)
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

  public ResponseEntity<?> readAllByUser(Long userid) {
    List<Income> incomes = incomeRepository.findByUserId(userid);
    if (incomes.isEmpty() || incomes == null) {
      // para esse usuário
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(new ErrorDto("Not Found", "O usuário não possui receitas"));
    }
    List<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(incomesDto);
  }

  public ResponseEntity<?> readByDescriptionAndUser(String description, Long userid) {
    List<Income> incomes = incomeRepository.findByDescriptionAndUserId(description, userid);
    if (incomes.isEmpty() || incomes == null) {

      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(new ErrorDto("Not Found", "O usuário não possui receitas com descrição similar a " + description));
    }
    List<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return ResponseEntity.status(HttpStatus.OK)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(incomesDto);
  }

  public ResponseEntity<?> readByIdAndUser(Long incomeId, Long userId) {
    Optional<Income> optional = incomeRepository.findByIdAndUserId(incomeId, userId);
    if (optional.isPresent()) {
      IncomeDto incomeDto = new IncomeDto(optional.get());
      return ResponseEntity.status(HttpStatus.OK)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(incomeDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(new ErrorDto("Not Found", "Nenhuma receita com esse id para esse usuário"));
  }

  public ResponseEntity<?> readByYearAndMonthAndUser(Integer year, Integer month, Long userId) {
    List<Income> incomes = incomeRepository.findByYearAndMonthAndUserId(year, month, userId);
    if (incomes.isEmpty()) {
      String message = "Não há receitas para o ano " + year + " e mês " + month;
      ErrorDto errorDto = new ErrorDto("Not Found", message);
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(errorDto);
    }
    List<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return ResponseEntity.status(HttpStatus.OK)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(incomesDto);
  }

  public ResponseEntity<?> update(Long id, IncomeForm incomeForm, Long userId) {
    Optional<Income> optional = incomeRepository.findByIdAndUserId(id, userId);
    if (optional.isPresent() && !incomeForm.exist(incomeRepository, id, userId)) {
      Income income = optional.get();

      income.setDescription(incomeForm.getDescription());
      income.setValue(incomeForm.getValue());
      income.setDate(LocalDate.parse(incomeForm.getDate()));

      return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json;charset=UTF-8")
          .body(new IncomeDto(income));
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(new ErrorDto("Not Found", "Nenhuma receita com esse id para esse usuário"));
  }

  public ResponseEntity<?> delete(Long id, Long userId) {
    Optional<Income> optional = incomeRepository.findByIdAndUserId(id, userId);
    if (optional.isPresent()) {
      incomeRepository.deleteById(id);
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .header("Content-Type", "application/json;charset=UTF-8")
        .body(new ErrorDto("Not Found", "Nenhuma receita com esse id para esse usuário"));
  }

  public BigDecimal getTotalValueByMonthAndYear(Integer year, Integer month) {
    if (incomeRepository.getTotalValueByMonthAndYear(year, month).isPresent()) {
      return incomeRepository.getTotalValueByMonthAndYear(year, month).get();
    }
    throw new RuntimeException("Total value not found for year: " + year + " e month: " + month);
  }

}
