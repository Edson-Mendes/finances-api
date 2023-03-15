package br.com.emendes.financesapi.repository;

import br.com.emendes.financesapi.model.entity.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

  @Query("SELECT i FROM Income i WHERE i.user.id = ?#{ principal?.id }")
  Page<Income> findAllByUser(Pageable pageable);

  @Query("SELECT i FROM Income i " +
      "WHERE lower_unaccent(i.description) LIKE lower_unaccent('%' || :description || '%') " +
      "AND i.user.id = ?#{ principal?.id }")
  Page<Income> findByDescriptionAndUser(
      @Param("description") String description,
      Pageable pageable);

  @Query("SELECT i FROM Income i WHERE YEAR(i.date) = :year " +
      "AND MONTH(i.date) = :month AND i.user.id = ?#{ principal?.id }")
  Page<Income> findByYearAndMonthAndUser(
      @Param("year") int year,
      @Param("month") int month,
      Pageable pageable);

  @Query("SELECT SUM(i.value) FROM Income i WHERE YEAR(i.date) = :year " +
      "AND MONTH(i.date) = :month AND i.user.id = ?#{ principal?.id }")
  Optional<BigDecimal> getTotalValueByMonthAndYearAndUser(
      @Param("year") int year,
      @Param("month") int month);

  @Query("SELECT i FROM Income i WHERE i.id = :id AND i.user.id = ?#{ principal?.id }")
  Optional<Income> findByIdAndUser(Long id);
}
