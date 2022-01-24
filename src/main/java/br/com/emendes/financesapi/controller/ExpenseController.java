package br.com.emendes.financesapi.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<ExpenseDto> create(@Valid @RequestBody ExpenseForm form, UriComponentsBuilder uriBuilder){
    Expense expense = form.convert(expenseRepository);

    expenseRepository.save(expense);

    URI uri = uriBuilder.path("/despesas/{id}").buildAndExpand(expense.getId()).toUri();
    return ResponseEntity.created(uri).body(new ExpenseDto(expense));
  }

  // @GetMapping
  // public List<IncomeDto> readAll(){
  //   List<Income> incomes = incomeRepository.findAll();
    
  //   List<IncomeDto> listIncomeDto = IncomeDto.convert(incomes);

  //   return listIncomeDto;
  // }

  // @GetMapping("/{id}")
  // public ResponseEntity<IncomeDto> readById(@PathVariable Long id){
  //   Optional<Income> optional =  incomeRepository.findById(id);
  //   if(optional.isPresent()){
  //     IncomeDto incomeDto = new IncomeDto(optional.get());
  //     return ResponseEntity.ok(incomeDto);
  //   }

  //   return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  // }

  // @PutMapping("/{id}")
  // @Transactional
  // public ResponseEntity<IncomeDto> update(@PathVariable Long id, @Valid @RequestBody IncomeForm incomeForm){
  //   Optional<Income> optional =  incomeRepository.findById(id);
  //   if(optional.isPresent()){
  //     Income income = optional.get();
      
  //     income.setDescription(incomeForm.getDescription());
  //     income.setValue(incomeForm.getValue());
  //     income.setDate(LocalDate.parse(incomeForm.getDate()));

  //     return ResponseEntity.ok(new IncomeDto(income));
  //   }

  //   return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  // }

  // @DeleteMapping("/{id}")
  // public ResponseEntity<?> delete(@PathVariable Long id){
  //   Optional<Income> optional =  incomeRepository.findById(id);
  //   if(optional.isPresent()){
  //     incomeRepository.deleteById(id);
  //     return ResponseEntity.ok().build();
  //   }

  //   return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  // }

}
