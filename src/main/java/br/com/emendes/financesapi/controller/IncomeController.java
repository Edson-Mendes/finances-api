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
import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/receitas")
public class IncomeController {

  @Autowired
  private IncomeService incomeService;

  @Autowired
  TokenService tokenService;

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @PostMapping
  public ResponseEntity<IncomeDto> create(@Valid @RequestBody IncomeForm form, UriComponentsBuilder uriBuilder,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return incomeService.create(form, userId, uriBuilder);
  }

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @GetMapping
  public ResponseEntity<Page<IncomeDto>> read(
      @RequestParam(required = false) String description,
      @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    if (description == null) {
      return incomeService.readAllByUser(userId, pageable);
    }
    return incomeService.readByDescriptionAndUser(description, userId, pageable);
  }

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @GetMapping("/{id}")
  public ResponseEntity<IncomeDto> readById(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return incomeService.readByIdAndUser(id, userId);
  }

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @GetMapping("/{year}/{month}")
  public ResponseEntity<Page<IncomeDto>> readByYearAndMonth(
      @PathVariable Integer year,
      @PathVariable Integer month,
      @PageableDefault(sort = "date", direction = Direction.DESC, page = 0, size = 10) Pageable pageable,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return incomeService.readByYearAndMonthAndUser(year, month, userId, pageable);
  }

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @PutMapping("/{id}")
  @Transactional
  public ResponseEntity<IncomeDto> update(@PathVariable Long id, @Valid @RequestBody IncomeForm incomeForm,
      HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    return incomeService.update(id, incomeForm, userId);
  }

  @Operation(security = { @SecurityRequirement(name = "bearer-key") })
  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id, HttpServletRequest request) {
    Long userId = tokenService.getUserId(request);
    incomeService.delete(id, userId);
  }

}
