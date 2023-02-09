package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.dto.ValueByCategoryDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExpenseService {

  ExpenseDto create(ExpenseForm expenseForm);

  Page<ExpenseDto> readAllByUser(Pageable pageable);

  Page<ExpenseDto> readByDescriptionAndUser(String description, Pageable pageable);

  ExpenseDto readByIdAndUser(Long id);

  Page<ExpenseDto> readByYearAndMonthAndUser(int year, int month, Pageable pageable);

  ExpenseDto update(Long id, ExpenseForm expenseForm);

  void deleteById(Long id);

  List<ValueByCategoryDto> getValuesByCategoryOnMonthAndYearByUser(Integer year, Integer month);

}
