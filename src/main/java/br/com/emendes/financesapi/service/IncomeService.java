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
import br.com.emendes.financesapi.repository.IncomeRepository;

@Service
public class IncomeService {

  @Autowired
  private IncomeRepository incomeRepository;

  public ResponseEntity<IncomeDto> create(IncomeForm form, UriComponentsBuilder uriBuilder) {
    Income income = form.convert(incomeRepository);

    incomeRepository.save(income);

    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(income.getId()).toUri();
    return ResponseEntity.created(uri).body(new IncomeDto(income));
  }

  public ResponseEntity<List<IncomeDto>> readAll() {
    List<Income> incomes = incomeRepository.findAll();

    List<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return ResponseEntity.ok(incomesDto);
  }

  public ResponseEntity<List<IncomeDto>> readByDescription(String description) {
    List<Income> incomes = incomeRepository.findByDescription(description);
    if(incomes.isEmpty() || incomes == null){
      return ResponseEntity.notFound().build();
    }
    List<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return ResponseEntity.ok(incomesDto);
  }

  public ResponseEntity<IncomeDto> readById(Long id) {
    Optional<Income> optional = incomeRepository.findById(id);
    if (optional.isPresent()) {
      IncomeDto incomeDto = new IncomeDto(optional.get());
      return ResponseEntity.ok(incomeDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  // TODO: Quando não encontrar receitas para o ano e mês passados, retornar 200 e
  // lista vazia ou not found?
  public ResponseEntity<?> readByYearAndMonth(Integer year, Integer month) {
    List<Income> incomes = incomeRepository.findByYearAndMonth(year, month);
    if(incomes.isEmpty()){
      String message = "Não há receitas para o ano " + year + " e mês " + month;
      ErrorDto errorDto = new ErrorDto("Not Found", message);
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .header("Content-Type", "application/json;charset=UTF-8")
          .body(errorDto);
    }
    List<IncomeDto> incomesDto = IncomeDto.convert(incomes);
    return ResponseEntity.ok(incomesDto);
  }

  public ResponseEntity<IncomeDto> update(Long id, IncomeForm incomeForm) {
    Optional<Income> optional = incomeRepository.findById(id);
    if (optional.isPresent() && !incomeForm.exist(incomeRepository, id)) {
      Income income = optional.get();

      income.setDescription(incomeForm.getDescription());
      income.setValue(incomeForm.getValue());
      income.setDate(LocalDate.parse(incomeForm.getDate()));

      return ResponseEntity.ok(new IncomeDto(income));
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  public ResponseEntity<?> delete(Long id) {
    Optional<Income> optional = incomeRepository.findById(id);
    if (optional.isPresent()) {
      incomeRepository.deleteById(id);
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  public BigDecimal getTotalValueByMonthAndYear(Integer year, Integer month) {
    if(incomeRepository.getTotalValueByMonthAndYear(year, month).isPresent()){
      return incomeRepository.getTotalValueByMonthAndYear(year, month).get();
    }
    throw new RuntimeException("Total value not found for year: "+year+" e month: "+month);
  }

}
