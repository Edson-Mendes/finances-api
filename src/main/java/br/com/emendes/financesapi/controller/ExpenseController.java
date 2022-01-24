package br.com.emendes.financesapi.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.repository.ExpenseRepository;

// TODO: Refatorar e colocar alguns códigos/responsabilidades dentro de um service.
@RestController
@RequestMapping("/despesas")
public class ExpenseController {

  @Autowired
  ExpenseRepository expenseRepository;

  @PostMapping
  public ResponseEntity<ExpenseDto> create(@Valid @RequestBody ExpenseForm form, UriComponentsBuilder uriBuilder) {
    Expense expense = form.convert(expenseRepository);

    expenseRepository.save(expense);

    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expense.getId()).toUri();
    return ResponseEntity.created(uri).body(new ExpenseDto(expense));
  }

  @GetMapping
  public List<ExpenseDto> readAll() {
    List<Expense> expenses = expenseRepository.findAll();

    List<ExpenseDto> listIncomeDto = ExpenseDto.convert(expenses);

    return listIncomeDto;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDto> readById(@PathVariable Long id) {
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent()) {
      ExpenseDto incomeDto = new ExpenseDto(optional.get());
      return ResponseEntity.ok(incomeDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<ExpenseDto> update(@PathVariable Long id, @Valid @RequestBody ExpenseForm expenseForm) {
    // FIXME: É possível atualizar uma despesa com mesma descrição em um mês e ano que já contém tal despesa.
    Optional<Expense> optional = expenseRepository.findById(id);
    if (optional.isPresent()) {
      Expense income = optional.get();

      income.setDescription(expenseForm.getDescription());
      income.setValue(expenseForm.getValue());
      income.setDate(LocalDate.parse(expenseForm.getDate()));

      return ResponseEntity.ok(new ExpenseDto(income));
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  // @DeleteMapping("/{id}")
  // public ResponseEntity<?> delete(@PathVariable Long id){
  // Optional<Income> optional = incomeRepository.findById(id);
  // if(optional.isPresent()){
  // incomeRepository.deleteById(id);
  // return ResponseEntity.ok().build();
  // }

  // return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  // }

}
