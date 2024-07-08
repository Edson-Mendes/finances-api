package br.com.emendes.financesapi.config.psql.function;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

/**
 * Class para registrar funções SQL non-standard ou custom para serem utilizadas com JPA.
 */
public class SQLFunctionContributor implements FunctionContributor {

  @Override
  public void contributeFunctions(FunctionContributions functionContributions) {
    functionContributions.getFunctionRegistry().registerNamed(
        "unaccent",
        functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)
    );
  }

}
