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

  @Query("SELECT i FROM Income i WHERE i.user.id = :userId")
  Page<Income> findByUserId(@Param("userId") Long userid, Pageable pageable);

  @Query("SELECT i FROM Income i WHERE i.description = :description AND MONTH(i.date) = :month AND YEAR(i.date) = :year AND i.user.id = :userId")
  Optional<Income> findByDescriptionAndMonthAndYearAndUserId(
      @Param("description") String description,
      @Param("month") Integer month, 
      @Param("year") Integer year, 
      @Param("userId") Long userId);

  @Query("SELECT i FROM Income i WHERE i.description = :description AND MONTH(i.date) = :month AND YEAR(i.date) = :year AND i.user.id = :userId AND i.id != :id")
  Optional<Income> findByDescriptionAndMonthAndYearAndUserIdAndNotId(
      @Param("description") String description,
      @Param("month") Integer month,
      @Param("year") Integer year,
      @Param("userId") Long userId,
      @Param("id") Long id);

  @Query("SELECT i FROM Income i WHERE i.description LIKE %:description% AND i.user.id = :userId")
  Page<Income> findByDescriptionAndUserId(
      @Param("description") String description, 
      @Param("userId") Long userid, 
      Pageable pageable);

  @Query("SELECT i FROM Income i WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month AND i.user.id = :userId")
  Page<Income> findByYearAndMonthAndUserId(
      @Param("year") Integer year, 
      @Param("month") Integer month,
      @Param("userId") Long userid,
      Pageable pageable);

  @Query("SELECT SUM(i.value) FROM Income i WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month AND i.user.id = :userId")
  Optional<BigDecimal> getTotalValueByMonthAndYearAndUserId(
      @Param("year") Integer year,
      @Param("month") Integer month,
      @Param("userId") Long userId);

  Optional<Income> findByIdAndUserId(Long id, Long userId);
}
