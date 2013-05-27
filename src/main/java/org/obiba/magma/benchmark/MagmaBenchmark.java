package org.obiba.magma.benchmark;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.hibernate.cfg.Environment;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.ValueType;
import org.obiba.magma.Variable;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.datasource.hibernate.HibernateDatasource;
import org.obiba.magma.datasource.hibernate.support.LocalSessionFactoryProvider;
import org.obiba.magma.support.DatasourceCopier;
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

  @Resource
  private Neo4jDatasourceBenchmark neo4jDatasourceBenchmark;

  public Collection<Variable> createVariables(int nbVariables) {
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

  public void benchmarkNeo4j(Collection<Variable> variables, int nbEntities) throws IOException {
    neo4jDatasourceBenchmark.generateData(variables, 100);
  }

  public void benchmarkHibernateMySql(Collection<Variable> variables, int nbEntities) throws IOException {
    LocalSessionFactoryProvider provider = new LocalSessionFactoryProvider("com.mysql.jdbc.Driver",
        "jdbc:mysql://localhost:3306/magma_benchmark?characterEncoding=UTF-8", "root", "1234",
        "org.hibernate.dialect.MySQL5InnoDBDialect");
    Properties p = new Properties();
    p.setProperty(Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider");
    provider.setProperties(p);
    provider.initialise();

    HibernateDatasource datasource = new HibernateDatasource("hibernate-mysql", provider.getSessionFactory());

    log.info("Generate Data for {}", datasource.getName());
    long start = System.currentTimeMillis();
    ValueTable generatedValueTable = new GeneratedValueTable(datasource, variables, nbEntities);
    provider.getSessionFactory().getCurrentSession().beginTransaction();
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(generatedValueTable, "NewTable", datasource);
    provider.getSessionFactory().getCurrentSession().getTransaction().commit();
    long end = System.currentTimeMillis();
    log.info("{} - Generated data ({} variables, {} entities) in {}", datasource.getName(), variables.size(),
        nbEntities, PeriodFormat.getDefault().print(new Period(start, end)));
  }

  public void benchmarkHibernateHsql(Collection<Variable> variables, int nbEntities) throws IOException {
    LocalSessionFactoryProvider provider = new LocalSessionFactoryProvider("org.hsqldb.jdbcDriver",
        "jdbc:hsqldb:mem:magma_benchmark;shutdown=true", "sa", "", "org.hibernate.dialect.HSQLDialect");
    Properties p = new Properties();
    p.setProperty(Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider");
    provider.setProperties(p);
    provider.initialise();

    HibernateDatasource datasource = new HibernateDatasource("hibernate-hsql", provider.getSessionFactory());

    log.info("Generate Data for {}", datasource.getName());
    long start = System.currentTimeMillis();
    ValueTable generatedValueTable = new GeneratedValueTable(datasource, variables, nbEntities);
    provider.getSessionFactory().getCurrentSession().beginTransaction();
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(generatedValueTable, "NewTable", datasource);
    provider.getSessionFactory().getCurrentSession().getTransaction().commit();
    long end = System.currentTimeMillis();
    log.info("{} - Generated data ({} variables, {} entities) in {}", datasource.getName(), variables.size(),
        nbEntities, PeriodFormat.getDefault().print(new Period(start, end)));

  }

  public static void main(String... args) throws IOException {

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
    MagmaBenchmark benchmark = applicationContext.getBean(MagmaBenchmark.class);
    Collection<Variable> variables = benchmark.createVariables(100);

//    benchmark.benchmarkNeo4j(variables, 100);
    benchmark.benchmarkHibernateMySql(variables, 100);
    benchmark.benchmarkHibernateHsql(variables, 100);

  }
}
