package br.com.emendes.financesapi.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.emendes.financesapi.model.Expense;
import br.com.emendes.financesapi.model.enumerator.Category;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  Page<Expense> findByUserId(Long userId, Pageable pageable);

  @Query("SELECT e FROM Expense e WHERE e.description = :description AND " +
      "MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.user.id = :userId")
  Optional<Expense> findByDescriptionAndMonthAndYearAndUserId(@Param("description") String description,
      @Param("month") Integer month, @Param("year") Integer year, @Param("userId") Long userId);

  @Query("SELECT e FROM Expense e WHERE e.description = :description AND " +
      "MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.user.id = :userId AND e.id != :id")
  Optional<Expense> findByDescriptionAndMonthAndYearAndUserIdAndNotId(@Param("description") String description,
      @Param("month") Integer month, @Param("year") Integer year, @Param("userId") Long userId, @Param("id") Long id);

  @Query("SELECT e FROM Expense e WHERE e.description LIKE %:description% AND e.user.id = :userId")
  Page<Expense> findByDescriptionAndUserId(@Param("description") String description,
      @Param("userId") Long userId,
      Pageable pageable);

  @Query("SELECT e FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date)= :month AND e.user.id = :userId")
  Page<Expense> findByYearAndMonthAndUserId(@Param("year") Integer year,
      @Param("month") Integer month,
      @Param("userId") long userId,
      Pageable pageable);

  @Query("SELECT SUM(e.value) FROM Expense e WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month AND e.user.id = :userId")
  Optional<BigDecimal> getTotalValueByMonthAndYearAndUserId(@Param("year") Integer year,
      @Param("month") Integer month,
      @Param("userId") Long userId);

  @Query("SELECT SUM(value) FROM Expense e WHERE YEAR(e.date) = :year AND " +
      "MONTH (e.date) = :month AND e.category = :category AND e.user.id = :userId")
  Optional<BigDecimal> getTotalByCategoryOnYearAndMonth(@Param("category") Category category,
      @Param("year") Integer year,
      @Param("month") Integer month,
      @Param("userId") Long userId);

  Optional<Expense> findByIdAndUserId(Long id, Long userId);

}
