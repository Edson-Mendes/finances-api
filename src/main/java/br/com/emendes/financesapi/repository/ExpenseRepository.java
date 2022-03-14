package br.com.emendes.financesapi.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.enumerator.Category;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  
  @Query("SELECT e FROM Expense e WHERE e.user.id = :userId")
  List<Expense> findByUserId(@Param("userId") Long userId);

  @Query("SELECT e FROM Expense e WHERE e.description = :description AND MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.user.id = :userId")
  Optional<Expense> findByDescriptionAndMonthAndYearAndUserId(@Param("description") String description
    , @Param("month") Integer month, @Param("year") Integer year, @Param("userId") Long userId);

  @Query("SELECT e FROM Expense e WHERE e.description = :description AND MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.user.id = :userId AND e.id != :id")
  Optional<Expense> findByDescriptionAndMonthAndYearAndUserIdAndNotId(@Param("description") String description
  , @Param("month") Integer month, @Param("year") Integer year, @Param("userId") Long userId, @Param("id") Long id);

  @Query("SELECT e FROM Expense e WHERE e.description LIKE %:description% AND e.user.id = :userId")
  List<Expense> findByDescriptionAndUserId(@Param("description") String description, @Param("userId") Long userId);

  @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month")
  List<Expense> findByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

  @Query("SELECT SUM(e.value) FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month")
  Optional<BigDecimal> getTotalValueByMonthAndYear(@Param("year") Integer year, @Param("month") Integer month);

  @Query("SELECT SUM(value) FROM Expense e WHERE YEAR(e.date) = :year AND MONTH (e.date) = :month AND e.category = :category")
  Optional<BigDecimal> getTotalByCategoryInYearAndMonth(@Param("category") Category category, @Param("year") Integer year, @Param("month") Integer month);
}
