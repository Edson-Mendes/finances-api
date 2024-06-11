package br.com.emendes.financesapi.config.psql.function;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

public class LowerUnaccentPsqlFunction implements FunctionContributor {

  @Override
  public void contributeFunctions(FunctionContributions functionContributions) {
    functionContributions.getFunctionRegistry().registerNamed(
        "lower_unaccent",
        functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)
    );
    functionContributions.getFunctionRegistry().registerNamed(
        "lower",
        functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)
    );
    functionContributions.getFunctionRegistry().registerNamed(
        "unaccent",
        functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.STRING)
    );
  }

}
