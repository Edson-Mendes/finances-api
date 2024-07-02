package br.com.emendes.financesapi.repository;

import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Interface repository com as abstrações para interação com o recurso Income no banco de dados.
 */
public interface IncomeRepository extends JpaRepository<Income, Long> {

  /**
   * Busca paginada de receitas (incomes) para um dado usuário (user).
   *
   * @param user     usuário relacionado com as receitas a serem buscadas.
   * @param pageable objeto que define como será a paginação (page, size e sort).
   * @return Objeto {@code Page<Income>} com as receitas encontradas para o dado user e pageable.
   */
  @Query("SELECT i FROM Income i WHERE i.user = :user")
  Page<Income> findAllByUser(@Param("user") User user, Pageable pageable);

  /**
   * Busca paginada de receitas (incomes) para um dado usuário (user) e descrição (description).<br>
   * <br>
   * OBS: A descrição da receita não precisa ser igual a description passada como parâmetro, e sim conte-la.
   * Ou seja, uma description 'Salário' seria buscada por exêmplo para parâmetros 'sal', 'Sala', 'RIO'.
   *
   * @param description descrição que as receitas devem conter para serem buscadas.
   * @param pageable    objeto que define como será feito a paginação (page, size e sort).
   * @param user        usuário relacionado com as receitas a serem buscadas.
   * @return Objeto {@code Page<Income>} com as receitas encontradas para o dado user, description e pageable.
   */
  @Query("""
      SELECT i FROM Income i
        WHERE lower(unaccent(i.description)) LIKE lower(unaccent('%' || :description || '%'))
        AND i.user = :user
      """)
  Page<Income> findByDescriptionAndUser(
      @Param("description") String description,
      @Param("user") User user,
      Pageable pageable);

  /**
   * Busca paginada de receitas (incomes) por ano (year), mês (month) e usuário (user).
   *
   * @param year     ano em que foi feito a receita.
   * @param month    mês em que foi feito a receita.
   * @param user     usuário relacionado com as receitas a serem buscadas.
   * @param pageable objeto que define como será feito a paginação (page, size e sort).
   * @return Objeto {@code Page<Income>} com as receitas que satisfaçam as restrições acima.
   */
  @Query("""
      SELECT i FROM Income i
        WHERE YEAR(i.date) = :year
        AND MONTH(i.date) = :month AND i.user = :user
      """)
  Page<Income> findByYearAndMonthAndUser(
      @Param("year") int year,
      @Param("month") int month,
      @Param("user") User user,
      Pageable pageable);

  /**
   * Retorna a soma de todas as receitas (incomes) com o mesmo ano (year), mês (month) e usuário (user).
   *
   * @param year  ano em que foi feito a receita.
   * @param month mês em que foi feito a receita
   * @param user  usuário relacionado com as receitas a serem buscadas.
   * @return {@code Optional<BigDecimal>} contendo a soma total, ou empty caso não encontre nenhuma receita que
   * satisfaça as condições acima.
   */
  @Query("""
      SELECT SUM(i.value) FROM Income i WHERE YEAR(i.date) = :year
        AND MONTH(i.date) = :month AND i.user = :user
      """)
  Optional<BigDecimal> getTotalValueByMonthAndYearAndUser(
      @Param("year") int year,
      @Param("month") int month,
      @Param("user") User user);

  /**
   * Busca receita (income) por id e user.
   *
   * @param id   identificador da receita.
   * @param user usuário relacionado com a receita a ser buscada.
   * @return Objeto {@code Optional<Income>} com a receita encontrada, ou empty caso contrário.
   */
  @Query("SELECT i FROM Income i WHERE i.id = :id AND i.user = :user")
  Optional<Income> findByIdAndUser(@Param("id") Long id, @Param("user") User user);

}
