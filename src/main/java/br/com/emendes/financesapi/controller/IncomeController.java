package br.com.emendes.financesapi.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
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

import br.com.emendes.financesapi.config.security.TokenService;
import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.service.IncomeService;

@RestController
@RequestMapping("/receitas")
public class IncomeController {

  @Autowired
  private IncomeService incomeService;

  @Autowired
  TokenService tokenService;

  @PostMapping
  public ResponseEntity<IncomeDto> create(@Valid @RequestBody IncomeForm form, UriComponentsBuilder uriBuilder,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return incomeService.create(form, userId, uriBuilder);
  }

  @GetMapping
  public ResponseEntity<List<IncomeDto>> read(@RequestParam(required = false) String description,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    if (description == null) {
      return incomeService.readAllByUser(userId);
    }
    return incomeService.readByDescriptionAndUser(description, userId);
  }

  @GetMapping("/{id}")
  public ResponseEntity<IncomeDto> readById(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return incomeService.readByIdAndUser(id, userId);
  }

  @GetMapping("/{year}/{month}")
  public ResponseEntity<List<IncomeDto>> readByYearAndMonth(@PathVariable Integer year, @PathVariable Integer month,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return incomeService.readByYearAndMonthAndUser(year, month, userId);
  }

  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<IncomeDto> update(@PathVariable Long id, @Valid @RequestBody IncomeForm incomeForm,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return incomeService.update(id, incomeForm, userId);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    incomeService.delete(id, userId);
  }

}
