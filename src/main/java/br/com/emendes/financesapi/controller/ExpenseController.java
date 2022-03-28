package br.com.emendes.financesapi.controller;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
  public ResponseEntity<ExpenseDto> create(@Valid @RequestBody ExpenseForm form, 
      UriComponentsBuilder uriBuilder,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.create(form, userId, uriBuilder);
  }

  @GetMapping
  public ResponseEntity<Page<ExpenseDto>> read(@RequestParam(required = false) String description,
      @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    if (description == null) {
      return expenseService.readAllByUser(userId, pageable);
    } else {
      return expenseService.readByDescriptionAndUser(description, userId, pageable);
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ExpenseDto> readById(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.readByIdAndUser(id, userId);
  }

  @GetMapping("/{year}/{month}")
  public ResponseEntity<Page<ExpenseDto>> readByYearAndMonth(
      @PathVariable Integer year, 
      @PathVariable Integer month,
      @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return expenseService.readByYearAndMonthAndUser(year, month, userId, pageable);
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
