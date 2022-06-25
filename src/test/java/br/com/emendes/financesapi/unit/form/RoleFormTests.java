package br.com.emendes.financesapi.unit.form;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.emendes.financesapi.controller.form.RoleForm;

@DisplayName("Tests for RoleForm")
public class RoleFormTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("Must return empty violations when RoleForm is valid")
  void mustReturnEmptyViolations_WhenRoleFormIsValid() {
    String roleName = "ROLE_MANAGER";
    RoleForm roleForm = new RoleForm(roleName);

    Set<ConstraintViolation<RoleForm>> violations = validator.validate(roleForm);

    Assertions.assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Must return empty violations when name don't matches with pattern")
  void mustReturnEmptyViolations_WhenNameDontMatchesWithPattern() {
    String roleName = "ROLE_manager";
    RoleForm roleForm = new RoleForm(roleName);

    Set<ConstraintViolation<RoleForm>> violations = validator.validate(roleForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

  @Test
  @DisplayName("Must return empty violations when name is Null")
  void mustReturnEmptyViolations_WhenNameIsNull() {
    String roleName = null;
    RoleForm roleForm = new RoleForm(roleName);

    Set<ConstraintViolation<RoleForm>> violations = validator.validate(roleForm);

    Assertions.assertThat(violations)
        .isNotEmpty()
        .hasSize(1);
  }

}
