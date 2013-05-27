package org.obiba.magma.benchmark;

import java.util.Collection;
import java.util.List;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.obiba.magma.Datasource;
import org.obiba.magma.ValueTable;
import org.obiba.magma.ValueType;
import org.obiba.magma.Variable;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.type.BooleanType;
import org.obiba.magma.type.DateTimeType;
import org.obiba.magma.type.DateType;
import org.obiba.magma.type.DecimalType;
import org.obiba.magma.type.IntegerType;
import org.obiba.magma.type.LocaleType;
import org.obiba.magma.type.TextType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public abstract class AbstractDatasourceBenchmark {

  private static final Logger log = LoggerFactory.getLogger(AbstractDatasourceBenchmark.class);

  private static final String PARTICIPANT = "Participant";

  private static final List<ValueType> types = Lists.newArrayList();

  static {
    types.add(TextType.get());
    types.add(LocaleType.get());
    types.add(DecimalType.get());
    types.add(IntegerType.get());
    types.add(BooleanType.get());
//    types.add(BinaryType.get());
    types.add(DateTimeType.get());
    types.add(DateType.get());
  }

  private final int nbVariables;

  private final int nbEntities;

  public AbstractDatasourceBenchmark(int nbVariables, int nbEntities) {
    this.nbVariables = nbVariables;
    this.nbEntities = nbEntities;
  }

  protected abstract Datasource createDatasource();

  public void run() {
    Datasource datasource = createDatasource();
    Collection<Variable> variables = createVariables();
    generateData(datasource, variables);
  }

  private void generateData(Datasource datasource, Collection<Variable> variables) {
    log.info("Generate Data for {}", datasource.getName());
    long start = System.currentTimeMillis();
    ValueTable valueTable = new GeneratedValueTable(datasource, variables, nbEntities);
    long end = System.currentTimeMillis();
    log.info("Generated data ({} variables, {} entities) in {} for {}", nbVariables, nbEntities,
        PeriodFormat.getDefault().print(new Period(start, end)), datasource.getName());
  }

  private Collection<Variable> createVariables() {
    log.info("Create {} variables", nbVariables);
    Collection<Variable> variables = Lists.newArrayList();
    for(int i = 0; i < nbVariables; i++) {
      Variable variable = createVariable("Variable " + i, getValueType(i), Math.random() < 0.5);
      variables.add(variable);
      log.trace("{}: {}, repeatable: {}", variable.getName(), variable.getValueType(), variable.isRepeatable());
    }
    return variables;
  }

  private ValueType getValueType(int i) {
    int nbTypes = types.size();
    return types.get(i % nbTypes);
  }

  private Variable createVariable(String name, ValueType valueType, boolean repeatable) {
    Variable.Builder builder = Variable.Builder.newVariable(name, valueType, PARTICIPANT);
    if(repeatable) builder.repeatable();
    return builder.build();
  }

//  public static void main(String... args) {
//    AbstractDatasourceBenchmark benchmark = new AbstractDatasourceBenchmark(50, 200);
//    benchmark.createVariables();
//  }

}
