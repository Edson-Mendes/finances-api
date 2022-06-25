package br.com.emendes.financesapi.unit.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.emendes.financesapi.controller.ExpenseController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.form.ExpenseForm;
import br.com.emendes.financesapi.creator.ExpenseDtoCreator;
import br.com.emendes.financesapi.creator.ExpenseFormCreator;
import br.com.emendes.financesapi.service.ExpenseService;
import br.com.emendes.financesapi.service.TokenService;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for ExpenseController")
public class ExpenseControllerTests {

  @InjectMocks
  private ExpenseController expenseController;

  @Mock
  private ExpenseService expenseServiceMock;

  @Mock
  private TokenService tokenServiceMock;

  private final UriComponentsBuilder URI_BUILDER = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");
  private final HttpServletRequest REQUEST_MOCK = Mockito.mock(HttpServletRequest.class);
  private final ExpenseForm EXPENSE_FORM = ExpenseFormCreator.validExpenseForm();
  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.DESC, "date");

  @BeforeEach
  public void setUp() {
    Long userId = 100l;
    ExpenseDto expenseDto = ExpenseDtoCreator.validExpenseDto();
    Page<ExpenseDto> pageExpenseDto = new PageImpl<>(List.of(expenseDto));

    BDDMockito.when(tokenServiceMock.getUserId(ArgumentMatchers.any(HttpServletRequest.class)))
        .thenReturn(userId);

    BDDMockito.when(expenseServiceMock.create(
        ArgumentMatchers.any(ExpenseForm.class),
        ArgumentMatchers.eq(userId)))
        .thenReturn(expenseDto);

    BDDMockito.when(expenseServiceMock.readAllByUser(userId, PAGEABLE))
        .thenReturn(pageExpenseDto);

    BDDMockito.when(expenseServiceMock.readByDescriptionAndUser("Gaso", userId, PAGEABLE))
        .thenReturn(pageExpenseDto);

    BDDMockito.when(expenseServiceMock.readByIdAndUser(expenseDto.getId(), userId))
        .thenReturn(expenseDto);

    BDDMockito.when(expenseServiceMock.update(expenseDto.getId(), EXPENSE_FORM, userId))
        .thenReturn(expenseDto);

    BDDMockito.doNothing().when(expenseServiceMock).delete(expenseDto.getId(), userId);
  }

  @Test
  @DisplayName("create must returns ResponseEntity<ExpenseDto> when create successfully")
  void create_ReturnsResponseEntityExpenseDto_WhenSuccessful() {
    ExpenseForm form = EXPENSE_FORM;

    ResponseEntity<ExpenseDto> response = expenseController.create(form, URI_BUILDER, REQUEST_MOCK);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(201);
    Assertions.assertThat(response.getBody().getDescription()).isEqualTo(form.getDescription());
    Assertions.assertThat(response.getBody().getValue()).isEqualTo(form.getValue());
  }

  @Test
  @DisplayName("read must returns ResponseEntity<Page<ExpenseDto>> when read successfully")
  void read_ReturnsResponseEntityPageExpenseDto_WhenReadSuccessful() {
    String description = null;

    ResponseEntity<Page<ExpenseDto>> response = expenseController.read(description, PAGEABLE, REQUEST_MOCK);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(
        response.getBody()
            .getContent()
            .get(0)
            .getDescription())
        .isEqualTo("Gasolina");
    Assertions.assertThat(
        response.getBody()
            .getContent()
            .get(0)
            .getValue())
        .isEqualTo(new BigDecimal("250.00"));
  }

  @Test
  @DisplayName("read must returns ResponseEntity<Page<ExpenseDto>> when read with description successfully")
  void read_ReturnsResponseEntityPageExpenseDto_WhenReadWithDescriptionSuccessful() {
    String description = "Gaso";

    ResponseEntity<Page<ExpenseDto>> response = expenseController.read(description, PAGEABLE, REQUEST_MOCK);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(
        response.getBody()
            .getContent()
            .get(0)
            .getDescription())
        .isEqualTo("Gasolina");
    Assertions.assertThat(
        response.getBody()
            .getContent()
            .get(0)
            .getValue())
        .isEqualTo(new BigDecimal("250.00"));
  }

  @Test
  @DisplayName("readById must returns ResponseEntity<ExpenseDto> when read with description successfully")
  void readById_ReturnsResponseEntityExpenseDto_WhenReadWithDescriptionSuccessful() {
    ExpenseDto expenseDto = ExpenseDtoCreator.validExpenseDto();
    ResponseEntity<ExpenseDto> response = expenseController.readById(expenseDto.getId(), REQUEST_MOCK);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(response.getBody().getId()).isEqualTo(expenseDto.getId());
    Assertions.assertThat(response.getBody().getDescription()).isEqualTo(expenseDto.getDescription());
  }

  @Test
  @DisplayName("update must returns ResponseEntity<ExpenseDto> when successful")
  void update_ReturnsResponseEntityExpenseDto_WhenSuccessful() {
    ExpenseDto expenseDto = ExpenseDtoCreator.validExpenseDto();
    ResponseEntity<ExpenseDto> response = expenseController.update(expenseDto.getId(), EXPENSE_FORM, REQUEST_MOCK);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(response.getBody().getId()).isEqualTo(expenseDto.getId());
    Assertions.assertThat(response.getBody().getDescription()).isEqualTo(expenseDto.getDescription());
  }

  @Test
  @DisplayName("delete must return ResponseEntity NoContent when successful")
  void delete_ReturnsResponseEntityNoContent_WhenSuccessful() {
    ExpenseDto expenseDto = ExpenseDtoCreator.validExpenseDto();
    ResponseEntity<Void> response = expenseController.delete(expenseDto.getId(), REQUEST_MOCK);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(204);
  }

}
