package br.com.emendes.financesapi.unit.controller;

import br.com.emendes.financesapi.controller.IncomeController;
import br.com.emendes.financesapi.controller.dto.ExpenseDto;
import br.com.emendes.financesapi.controller.dto.IncomeDto;
import br.com.emendes.financesapi.controller.form.IncomeForm;
import br.com.emendes.financesapi.service.IncomeService;
import br.com.emendes.financesapi.util.creator.IncomeDtoCreator;
import br.com.emendes.financesapi.util.creator.IncomeFormCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests for IncomeController")
class IncomeControllerTests {

  @InjectMocks
  private IncomeController incomeController;

  @Mock
  private IncomeService incomeServiceMock;

  private final UriComponentsBuilder URI_BUILDER = UriComponentsBuilder.fromHttpUrl("http://localhost:8080");
  private final IncomeForm INCOME_FORM = IncomeFormCreator.validIncomeForm();
  private final Pageable PAGEABLE = PageRequest.of(0, 10, Direction.DESC, "date");

  @BeforeEach
  public void setUp() {
    IncomeDto incomeDto = IncomeDtoCreator.validIncomeDto();
    Page<IncomeDto> pageIncomeDto = new PageImpl<>(List.of(incomeDto));

    BDDMockito.when(incomeServiceMock.create(
            ArgumentMatchers.any(IncomeForm.class)))
        .thenReturn(incomeDto);

    BDDMockito.when(incomeServiceMock.readAllByUser(PAGEABLE))
        .thenReturn(pageIncomeDto);

    BDDMockito.when(incomeServiceMock.readByDescriptionAndUser("ario", PAGEABLE))
        .thenReturn(pageIncomeDto);

    BDDMockito.when(incomeServiceMock.readByIdAndUser(incomeDto.getId()))
        .thenReturn(incomeDto);

    BDDMockito.when(incomeServiceMock.readByYearAndMonthAndUser(2022, 1, PAGEABLE))
        .thenReturn(pageIncomeDto);

    BDDMockito.when(incomeServiceMock.update(incomeDto.getId(), INCOME_FORM))
        .thenReturn(incomeDto);

    BDDMockito.doNothing().when(incomeServiceMock).deleteById(incomeDto.getId());
  }

  @Test
  @DisplayName("create must returns ResponseEntity<IncomeDto> when create successfully")
  void create_ReturnsResponseEntityIncomeDto_WhenSuccessful() {
    IncomeForm form = INCOME_FORM;

    ResponseEntity<IncomeDto> response = incomeController.create(form, URI_BUILDER);

    HttpStatus statusCode = response.getStatusCode();
    IncomeDto responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.CREATED);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody.getDescription()).isEqualTo(form.getDescription());
    Assertions.assertThat(responseBody.getValue()).isEqualTo(form.getValue());
  }

  @Test
  @DisplayName("read must returns ResponseEntity<Page<IncomeDto>> when read successfully")
  void read_ReturnsResponseEntityPageIncomeDto_WhenReadSuccessful() {
    String description = null;

    ResponseEntity<Page<IncomeDto>> response = incomeController.read(description, PAGEABLE);


    HttpStatus statusCode = response.getStatusCode();
    Page<IncomeDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody
            .getContent()
            .get(0)
            .getDescription()).isEqualTo("Salário");
    Assertions.assertThat(responseBody
            .getContent()
            .get(0)
            .getValue()).isEqualTo(new BigDecimal("2500.00"));
  }

  @Test
  @DisplayName("read must returns ResponseEntity<Page<IncomeDto>> when read with description successfully")
  void read_ReturnsResponseEntityPageIncomeDto_WhenReadWithDescriptionSuccessful() {
    String description = "ario";

    ResponseEntity<Page<IncomeDto>> response = incomeController.read(description, PAGEABLE);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(
            response.getBody()
                .getContent()
                .get(0)
                .getDescription())
        .isEqualTo("Salário");
    Assertions.assertThat(
            response.getBody()
                .getContent()
                .get(0)
                .getValue())
        .isEqualTo(new BigDecimal("2500.00"));
  }

  @Test
  @DisplayName("readById must returns ResponseEntity<IncomeDto> when read with description successfully")
  void readById_ReturnsResponseEntityIncomeDto_WhenReadWithDescriptionSuccessful() {
    IncomeDto incomeDto = IncomeDtoCreator.validIncomeDto();
    ResponseEntity<IncomeDto> response = incomeController.readById(incomeDto.getId());

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(response.getBody().getId()).isEqualTo(incomeDto.getId());
    Assertions.assertThat(response.getBody().getDescription()).isEqualTo(incomeDto.getDescription());
  }

  @Test
  @DisplayName("readByYearAndMonth must returns ResponseEntity<Page<ExpenseDto>> when successful")
  void readByYearAndMonth_ReturnsResponseEntityPageExpenseDto_WhenSuccessful() {
    ResponseEntity<Page<IncomeDto>> response = incomeController.readByYearAndMonth(2022, 1, PAGEABLE);

    HttpStatus statusCode = response.getStatusCode();
    Page<IncomeDto> responseBody = response.getBody();

    Assertions.assertThat(statusCode).isEqualByComparingTo(HttpStatus.OK);
    Assertions.assertThat(responseBody).isNotNull();
    Assertions.assertThat(responseBody
        .getContent()
        .get(0)
        .getDescription()).isEqualTo("Salário");
    Assertions.assertThat(responseBody
        .getContent()
        .get(0)
        .getValue()).isEqualTo(new BigDecimal("2500.00"));
  }

  @Test
  @DisplayName("update must returns ResponseEntity<IncomeDto> when successful")
  void update_ReturnsResponseEntityIncomeDto_WhenSuccessful() {
    IncomeDto incomeDto = IncomeDtoCreator.validIncomeDto();
    ResponseEntity<IncomeDto> response = incomeController.update(incomeDto.getId(), INCOME_FORM);

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(200);
    Assertions.assertThat(response.getBody().getId()).isEqualTo(incomeDto.getId());
    Assertions.assertThat(response.getBody().getDescription()).isEqualTo(incomeDto.getDescription());
  }

  @Test
  @DisplayName("delete must return ResponseEntity NoContent when successful")
  void delete_ReturnsResponseEntityNoContent_WhenSuccessful() {
    IncomeDto incomeDto = IncomeDtoCreator.validIncomeDto();
    ResponseEntity<Void> response = incomeController.delete(incomeDto.getId());

    Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(204);
  }

}
