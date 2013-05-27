package org.obiba.magma.benchmark;

import org.obiba.magma.Datasource;
import org.obiba.magma.ValueTable;
import org.obiba.magma.Variable;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.type.DecimalType;
import org.obiba.magma.type.IntegerType;

import com.google.common.collect.ImmutableSet;

public class MagmaBenchmark {

  private int nbTables;

  private int nbVariables;

  private int nbEntities;

  private void generateData(Datasource datasource) {
    ImmutableSet<Variable> variables = ImmutableSet.of( //
        Variable.Builder.newVariable("Test Variable", IntegerType.get(), "Participant").build(), //
        Variable.Builder.newVariable("Other Variable", DecimalType.get(), "Participant").build());

    ValueTable generatedValueTable = new GeneratedValueTable(datasource, variables, nbEntities);
  }

}
