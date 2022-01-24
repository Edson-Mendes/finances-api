package br.com.emendes.financesapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.emendes.financesapi.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  
  @Query("SELECT e FROM Expense e WHERE e.description = :description AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
  Optional<Expense> findByDescriptionAndMonthAndYear(@Param("description") String description
    , @Param("month") Integer month, @Param("year") Integer year);
}
