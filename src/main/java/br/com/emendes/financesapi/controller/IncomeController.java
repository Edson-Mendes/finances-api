package br.com.emendes.financesapi.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.model.Income;
import br.com.emendes.financesapi.repository.IncomeRepository;

@RestController
@RequestMapping("/receitas")
public class IncomeController {
  
  @Autowired
  IncomeRepository incomeRepository;

  @PostMapping
  public ResponseEntity<IncomeDto> create(@RequestBody @Valid IncomeForm form, UriComponentsBuilder uriBuilder){
    Income income = form.convert(incomeRepository);

    incomeRepository.save(income);

    URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(income.getId()).toUri();
    return ResponseEntity.created(uri).body(new IncomeDto(income));
  }

  @GetMapping
  public List<IncomeDto> readAll(){
    List<Income> incomes = incomeRepository.findAll();
    
    List<IncomeDto> listIncomeDto = IncomeDto.convert(incomes);

    return listIncomeDto;
  }

  @GetMapping("/{id}")
  public ResponseEntity<IncomeDto> readById(@PathVariable Long id){
    Optional<Income> optional =  incomeRepository.findById(id);
    if(optional.isPresent()){
      IncomeDto incomeDto = new IncomeDto(optional.get());
      return ResponseEntity.ok(incomeDto);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }
}
