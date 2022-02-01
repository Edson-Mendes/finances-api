package br.com.emendes.financesapi.controller;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.service.IncomeService;

@RestController
@RequestMapping("/receitas")
public class IncomeController {
  
  @Autowired
  private IncomeService incomeService;

  @PostMapping
  public ResponseEntity<IncomeDto> create(@Valid @RequestBody IncomeForm form, UriComponentsBuilder uriBuilder){
    return incomeService.create(form, uriBuilder);
  }

  @GetMapping
  public ResponseEntity<List<IncomeDto>> read(@RequestParam(required = false) String description){
    if(description == null){
      return incomeService.readAll();
    }
    return incomeService.readByDescription(description);
  }

  @GetMapping("/{id}")
  public ResponseEntity<IncomeDto> readById(@PathVariable Long id){
    return incomeService.readById(id);
  }

  // TODO: Fazer tratamento caso o path não contenha um número para ano e mês.
  @GetMapping("/{year}/{month}")
  public ResponseEntity<List<IncomeDto>> readByYearAndMonth(@PathVariable Integer year, @PathVariable Integer month){
    return incomeService.readByYearAndMonth(year, month);
  }

  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<IncomeDto> update(@PathVariable Long id, @Valid @RequestBody IncomeForm incomeForm){
    return incomeService.update(id, incomeForm);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id){
    return incomeService.delete(id);
  }

}
