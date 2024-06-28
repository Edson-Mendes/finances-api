package br.com.emendes.financesapi.repository;

import br.com.emendes.financesapi.dto.response.ValueByCategoryResponse;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  @Deprecated
  @Query("SELECT e FROM Expense e WHERE e.user.id = ?#{ principal?.id }")
  Page<Expense> findAllByUser(Pageable pageable);

  /**
   * Busca paginada de despesas (expenses) para um dado usuário (user).
   *
   * @param user     usuário relacionado com as despesas a serem buscadas.
   * @param pageable objeto que define como será a paginação (page, size e sort).
   * @return Objeto {@code Page<Expense>} com as despesas encontradas para o dado user e pageable.
   */
  @Query("SELECT e FROM Expense e WHERE e.user = :user")
  Page<Expense> findAllByUser(@Param("user") User user, Pageable pageable);

  @Deprecated
  @Query("SELECT e FROM Expense e " +
         "WHERE lower_unaccent(e.description) LIKE lower_unaccent('%' || :description || '%') " +
         "AND e.user.id = ?#{ principal?.id }")
  Page<Expense> findByDescriptionAndUser(@Param("description") String description, Pageable pageable);

  /**
   * Busca paginada de despesas (expenses) para um dado usuário (user) e descrição (description).<br>
   * <br>
   * OBS: A descrição da despesa não precisa ser igual a description passada como parâmetro, e sim conte-la.
   * Ou seja, uma description 'Supermercado Zaffari' seria buscada por exêmplo para parâmetros 'mercado', 'zaffari', 'super'.
   *
   * @param description descrição que as despesas devem conter para serem buscadas.
   * @param pageable    objeto que define como será feito a paginação (page, size e sort).
   * @param user        usuário relacionado com as despesas a serem buscadas.
   * @return Objeto {@code Page<Expense>} com as despesas encontradas para o dado user, description e pageable.
   */
  @Query("""
      SELECT e FROM Expense e
        WHERE lower(unaccent(e.description)) LIKE lower(unaccent('%' || :description || '%'))
        AND e.user = :user
      """)
  Page<Expense> findByDescriptionAndUser(
      @Param("description") String description,
      @Param("user") User user,
      Pageable pageable);

  @Deprecated
  @Query("""
      SELECT e FROM Expense e
        WHERE YEAR(e.date) = :year
        AND MONTH(e.date)= :month
        AND e.user.id = ?#{ principal?.id }
      """)
  Page<Expense> findByYearAndMonthAndUser(
      @Param("year") int year,
      @Param("month") int month,
      Pageable pageable);

  /**
   * Busca paginada de despesas (expenses) por ano (year), mês (month) e usuário (user).
   *
   * @param year     ano em que foi feito a despesa.
   * @param month    mês em que foi feito a despesa.
   * @param user     usuário relacionado com as despesas a serem buscadas.
   * @param pageable objeto que define como será feito a paginação (page, size e sort).
   * @return Objeto {@code Page<Expense>} com as despesas que satisfaçam as restrições acima.
   */
  @Query("""
      SELECT e FROM Expense e
        WHERE YEAR(e.date) = :year
        AND MONTH(e.date) = :month
        AND e.user = :user
      """)
  Page<Expense> findByYearAndMonthAndUser(
      @Param("year") int year,
      @Param("month") int month,
      @Param("user") User user,
      Pageable pageable);

  @Deprecated
  @Query("SELECT e FROM Expense e WHERE e.id = :id AND e.user.id = ?#{ principal?.id }")
  Optional<Expense> findByIdAndUser(@Param("id") Long id);

  /**
   * Busca despesa (expense) por id e user.
   *
   * @param id   identificador da despesa.
   * @param user usuário relacionado com a despesa a ser buscada.
   * @return Objeto {@code Optional<Expense>} com a despesa encontrada, ou empty caso contrário.
   */
  @Query("SELECT e FROM Expense e WHERE e.id = :id AND e.user = :user")
  Optional<Expense> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

  @Deprecated
  @Query("SELECT new br.com.emendes.financesapi.dto.response.ValueByCategoryResponse(e.category, SUM(e.value)) " +
         "FROM Expense e " +
         "WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month " +
         "AND e.user.id = ?#{ principal?.id } " +
         "GROUP BY e.category")
  List<ValueByCategoryResponse> getValueByCategoryAndMonthAndYearAndUser(@Param("year") int year, @Param("month") int month);

  /**
   * Busca uma lista de {@link ValueByCategoryResponse}, agrupando todas as despesas por categoria em dado mês (month)
   * e ano (year)e por usuário.
   *
   * @param year  ano ao qual as despesas devem pertencer.
   * @param month mês ao qual as despesas devem pertencer.
   * @param user  usuário relacionado com as despesas a serem agrupadas.
   * @return {@code List<ValueByCategoryResponse>} contendo os gastos agrupados por categoria.
   */
  @Query("""
      SELECT new br.com.emendes.financesapi.dto.response.ValueByCategoryResponse(e.category, SUM(e.value))
         FROM Expense e
         WHERE YEAR(e.date) = :year AND MONTH(e.date) = :month
         AND e.user = :user
         GROUP BY e.category
      """)
  List<ValueByCategoryResponse> getValueByCategoryAndMonthAndYearAndUser(
      @Param("year") int year,
      @Param("month") int month,
      @Param("user") User user);
}
