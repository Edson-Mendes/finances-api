package br.com.emendes.financesapi.repository;

import br.com.emendes.financesapi.controller.dto.ValueByCategoryDto;
import br.com.emendes.financesapi.model.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  @Query("SELECT e FROM Expense e WHERE e.user.id = ?#{ principal?.id }")
  Page<Expense> findAllByUser(Pageable pageable);

  // TODO: Remover query
  @Query("SELECT count(e) > 0 FROM Expense e " +
      "WHERE lower_unaccent(e.description) = lower_unaccent(:description) " +
      "AND MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.user.id = ?#{ principal?.id }")
  boolean existsByDescriptionAndMonthAndYearAndUser(
      @Param("description") String description,
      @Param("month") int month,
      @Param("year") int year);

  // TODO: Remover query
  @Query("SELECT count(e) > 0 FROM Expense e " +
      "WHERE lower_unaccent(e.description) = lower_unaccent(:description) " +
      "AND MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.id != :id AND e.user.id = ?#{ principal?.id }")
  boolean existsByDescriptionAndMonthAndYearAndNotIdAndUser(
      @Param("description") String description,
      @Param("month") int month,
      @Param("year") int year,
      @Param("id") Long id);

  @Query("SELECT e FROM Expense e " +
      "WHERE lower_unaccent(e.description) LIKE lower_unaccent('%' || :description || '%') " +
      "AND e.user.id = ?#{ principal?.id }")
  Page<Expense> findByDescriptionAndUser(@Param("description") String description, Pageable pageable);

  @Query("SELECT e FROM Expense e " +
      "WHERE YEAR(e.date) = :year AND MONTH(e.date)= :month AND e.user.id = ?#{ principal?.id }")
  Page<Expense> findByYearAndMonthAndUser(
      @Param("year") int year,
      @Param("month") int month,
      Pageable pageable);

  @Query("SELECT e FROM Expense e WHERE e.id = :id AND e.user.id = ?#{ principal?.id }")
  Optional<Expense> findByIdAndUser(@Param("id") Long id);

  @Query("SELECT new br.com.emendes.financesapi.controller.dto.ValueByCategoryDto(e.category, SUM(e.value)) " +
      "FROM Expense e " +
      "WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month " +
      "AND e.user.id = ?#{ principal?.id } " +
      "GROUP BY e.category")
  List<ValueByCategoryDto> getValueByCategoryAndMonthAndYearAndUser(@Param("year") int year, @Param("month") int month);
}
