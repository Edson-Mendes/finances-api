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

// TODO: Refatorar as repetições de código.
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
    String token = tokenService.recoverToken(request);
    Long userId = tokenService.getIdUser(token);
    return incomeService.create(form, uriBuilder, userId);
  }

  @GetMapping
  public ResponseEntity<List<IncomeDto>> read(@RequestParam(required = false) String description,
      HttpServletRequest request) {
    String token = tokenService.recoverToken(request);
    Long userId = tokenService.getIdUser(token);
    if (description == null) {
      return incomeService.readAllByUser(userId);
    }
    return incomeService.readByDescriptionAndUser(description, userId);
  }

  @GetMapping("/{id}")
  public ResponseEntity<IncomeDto> readById(@PathVariable Long id, HttpServletRequest request) {
    String token = tokenService.recoverToken(request);
    Long userId = tokenService.getIdUser(token);
    return incomeService.readByIdAndUser(id, userId);
  }

  // TODO: Fazer tratamento caso o path não contenha um número para ano e mês.
  @GetMapping("/{year}/{month}")
  public ResponseEntity<?> readByYearAndMonth(@PathVariable Integer year, @PathVariable Integer month,
      HttpServletRequest request) {
    String token = tokenService.recoverToken(request);
    Long userId = tokenService.getIdUser(token);
    return incomeService.readByYearAndMonthAndUser(year, month, userId);
  }

  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<IncomeDto> update(@PathVariable Long id, @Valid @RequestBody IncomeForm incomeForm) {
    return incomeService.update(id, incomeForm);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    return incomeService.delete(id);
  }

}
