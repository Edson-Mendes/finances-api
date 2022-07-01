package br.com.emendes.financesapi.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.emendes.financesapi.model.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

  @Query("SELECT i FROM Income i WHERE i.user.id = ?#{ principal?.id }")
  Page<Income> findAllByUser(Pageable pageable);

  // FIXME: Refatorar essa busca
  @Query("SELECT i FROM Income i WHERE i.description = :description AND " +
      "MONTH(i.date) = :month AND YEAR(i.date) = :year AND i.user.id = ?#{ principal?.id }")
  Optional<Income> findByDescriptionAndMonthAndYearAndUser(
      @Param("description") String description,
      @Param("month") Integer month,
      @Param("year") Integer year);

  // FIXME: Refatorar essa busca
  @Query("SELECT i FROM Income i WHERE i.description = :description AND "
      + "MONTH(i.date) = :month AND YEAR(i.date) = :year AND i.id != :id AND i.user.id = ?#{ principal?.id }")
  Optional<Income> findByDescriptionAndMonthAndYearAndNotIdAndUser(
      @Param("description") String description,
      @Param("month") Integer month,
      @Param("year") Integer year,
      @Param("id") Long id);

  @Query("SELECT i FROM Income i WHERE i.description LIKE %:description% AND i.user.id = ?#{ principal?.id }")
  Page<Income> findByDescriptionAndUser(
      @Param("description") String description,
      Pageable pageable);

  @Query("SELECT i FROM Income i WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month AND i.user.id = ?#{ principal?.id }")
  Page<Income> findByYearAndMonthAndUser(
      @Param("year") Integer year,
      @Param("month") Integer month,
      Pageable pageable);

  @Query("SELECT SUM(i.value) FROM Income i WHERE YEAR(i.date) = :year AND "
      + "MONTH(i.date) = :month AND i.user.id = ?#{ principal?.id }")
  Optional<BigDecimal> getTotalValueByMonthAndYearAndUser(
      @Param("year") Integer year,
      @Param("month") Integer month);

  @Query("SELECT i FROM Income i WHERE i.id = :id AND i.user.id = ?#{ principal?.id }")
  Optional<Income> findByIdAndUser(Long id);
}
