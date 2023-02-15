package br.com.emendes.financesapi.service;

import br.com.emendes.financesapi.dto.response.ExpenseResponse;
import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.dto.request.ExpenseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExpenseService {

  ExpenseResponse create(ExpenseRequest expenseRequest);

  Page<ExpenseResponse> readAllByUser(Pageable pageable);

  Page<ExpenseResponse> readByDescriptionAndUser(String description, Pageable pageable);

  ExpenseResponse readByIdAndUser(Long id);

  Page<ExpenseResponse> readByYearAndMonthAndUser(int year, int month, Pageable pageable);

  ExpenseResponse update(Long id, ExpenseRequest expenseRequest);

  void deleteById(Long id);

  List<ValueByCategoryResponse> getValuesByCategoryOnMonthAndYearByUser(Integer year, Integer month);

}
