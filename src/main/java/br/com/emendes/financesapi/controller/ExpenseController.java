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
import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.service.ExpenseService;

@RestController
@RequestMapping("/despesas")
public class ExpenseController {

  @Autowired
  private ExpenseService expenseService;

  @Autowired
  private TokenService tokenService;

  @PostMapping
  public ResponseEntity<ExpenseDto> create(@Valid @RequestBody ExpenseForm form, UriComponentsBuilder uriBuilder,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.create(form, userId, uriBuilder);
  }

  @GetMapping
  public ResponseEntity<List<ExpenseDto>> read(@RequestParam(required = false) String description,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    if (description == null) {
      return expenseService.readAllByUser(userId);
    } else {
      return expenseService.readByDescriptionAndUser(description, userId);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDto> readById(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.readByIdAndUser(id, userId);
  }

  @GetMapping("/{year}/{month}")
  public ResponseEntity<List<ExpenseDto>> readByYearAndMonth(@PathVariable Integer year, @PathVariable Integer month,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.readByYearAndMonthAndUser(year, month, userId);
  }

  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<ExpenseDto> update(@PathVariable Long id, @Valid @RequestBody ExpenseForm expenseForm,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.update(id, expenseForm, userId);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    expenseService.delete(id, userId);
  }

}
