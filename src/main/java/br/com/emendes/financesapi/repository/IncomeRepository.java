package br.com.emendes.financesapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.emendes.financesapi.model.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long>{

  @Query("SELECT i FROM Income i WHERE i.description = :description AND MONTH(i.date) = :month AND YEAR(i.date) = :year")
  Optional<Income> findByDescriptionAndMonthAndYear(@Param("description") String description
    , @Param("month") Integer month, @Param("year") Integer year);
  
}
