package br.com.emendes.financesapi.util.constant;

import br.com.emendes.financesapi.model.Category;
import br.com.emendes.financesapi.model.entity.Expense;
import br.com.emendes.financesapi.model.entity.Income;
import br.com.emendes.financesapi.model.entity.Role;
import br.com.emendes.financesapi.model.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class ConstantForTesting {

  private ConstantForTesting() {}

  public static final Pageable PAGEABLE = PageRequest.of(0, 10, Sort.Direction.DESC, "date");

  public static final Pageable PAGEABLE_WITH_PAGE_ONE = PageRequest.of(1, 10, Sort.Direction.DESC, "date");

  @Deprecated
  public static final Role ROLE_USER = Role.builder()
      .id(1)
      .name("ROLE_USER")
      .build();

  @Deprecated
  public static final User USER = User.builder()
      .id(1000L)
      .name("Lorem Ipsum")
      .email("lorem@email.com")
      .password("encodedpassword")
      .roles(List.of(ROLE_USER))
      .build();

  @Deprecated
  public static final Expense EXPENSE = Expense.builder()
      .id(100_000L)
      .description("Aluguel xpto")
      .category(Category.MORADIA)
      .value(new BigDecimal("1500.00"))
      .date(LocalDate.parse("2023-02-05"))
      .user(USER)
      .build();

  public static final Income INCOME = Income.builder()
      .id(100_000L)
      .description("Salário")
      .value(new BigDecimal("2500.00"))
      .date(LocalDate.parse("2023-02-08"))
      .user(USER)
      .build();

}
