package org.obiba.magma.benchmark;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.obiba.magma.ValueType;
import org.obiba.magma.Variable;
import org.obiba.magma.type.BooleanType;
import org.obiba.magma.type.DateTimeType;
import org.obiba.magma.type.DateType;
import org.obiba.magma.type.DecimalType;
import org.obiba.magma.type.IntegerType;
import org.obiba.magma.type.LocaleType;
import org.obiba.magma.type.TextType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class MagmaBenchmark {

  private static final Logger log = LoggerFactory.getLogger(MagmaBenchmark.class);

  public static final String PARTICIPANT = "Participant";

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

  @Resource
  private Neo4jBenchmark neo4jBenchmark;

  @Resource
  private HibernateBenchmark hibernateBenchmark;

  @Resource
  private JdbcBenchmark jdbcBenchmark;

  public Collection<Variable> createVariables(int nbVariables) {
    log.info("Create {} variables", nbVariables);
    Collection<Variable> variables = Lists.newArrayList();
    for(int i = 0; i < nbVariables; i++) {
      Variable variable = createVariable("Variable " + i, getValueType(i), Math.random() < 0.5);
      variables.add(variable);
      log.debug("{}: {}, repeatable: {}", variable.getName(), variable.getValueType(), variable.isRepeatable());
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

  public void run(int nbVariables) throws IOException {
    Collection<Variable> variables = createVariables(nbVariables);
    run(variables, 100);
    run(variables, 1000);
//    run(variables, 10000);
    Results.dump();
  }

  private void run(Collection<Variable> variables, int nbEntities) throws IOException {
    hibernateBenchmark.benchmarkHsql(variables, nbEntities);
    hibernateBenchmark.benchmarkMySql(variables, nbEntities);
    jdbcBenchmark.benchmarkHsql(variables, nbEntities);
    jdbcBenchmark.benchmarkMysql(variables, nbEntities);
    neo4jBenchmark.benchmark(variables, nbEntities);
  }

  public static void main(String... args) throws IOException {
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
    MagmaBenchmark benchmark = applicationContext.getBean(MagmaBenchmark.class);
    benchmark.run(100);
  }
}
