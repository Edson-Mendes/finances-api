package br.com.emendes.financesapi.service;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.repository.ExpenseRepository;

@Service
public class ExpenseService {

  @Autowired
  ExpenseRepository expenseRepository;

  public ResponseEntity<ExpenseDto> create(ExpenseForm form, UriComponentsBuilder uriBuilder) {
    Expense expense = form.convert(expenseRepository);

    expenseRepository.save(expense);

    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expense.getId()).toUri();
    return ResponseEntity.created(uri).body(new ExpenseDto(expense));
  }

  public List<ExpenseDto> readAll() {
    List<Expense> expenses = expenseRepository.findAll();
    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);

    return expensesDto;
  }

  public List<ExpenseDto> readByDescription(String description) {
    List<Expense> expenses = expenseRepository.findByDescription(description);
    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);

    return expensesDto;
  }

  public ResponseEntity<ExpenseDto> readById(Long id) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent()) {
      ExpenseDto expenseDto = new ExpenseDto(optional.get());
      return ResponseEntity.ok(expenseDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  // TODO: Quando não encontrar receitas para o ano e mês passados, retornar 200 e
  // lista vazia ou bad request?
  public List<ExpenseDto> readByYearAndMonth(Integer year, Integer month) {
    List<Expense> expenses = expenseRepository.findByYearAndMonth(year, month);

    List<ExpenseDto> expensesDto = ExpenseDto.convert(expenses);

    return expensesDto;
  }

  public ResponseEntity<ExpenseDto> update(Long id, ExpenseForm expenseForm) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent() && !expenseForm.exist(expenseRepository, id)) {
      Expense expense = optional.get();

      expense.setParams(expenseForm);

      return ResponseEntity.ok(new ExpenseDto(expense));
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  public ResponseEntity<?> delete(Long id) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent()) {
      expenseRepository.deleteById(id);
      return ResponseEntity.ok().build();
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

}
