package org.obiba.magma.benchmark;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.Variable;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.datasource.jdbc.JdbcDatasourceFactory;
import org.obiba.magma.datasource.jdbc.JdbcDatasourceSettings;
import org.obiba.magma.support.DatasourceCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.obiba.magma.benchmark.MagmaBenchmark.PARTICIPANT;

@Component
@Transactional
public class JdbcBenchmark {

  private static final Logger log = LoggerFactory.getLogger(JdbcBenchmark.class);

  public void benchmarkMysql(Collection<Variable> variables, int nbEntities) throws IOException {
    Properties prop = new Properties();
    prop.setProperty(JdbcDatasourceFactory.DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");
    prop.setProperty(JdbcDatasourceFactory.URL, "jdbc:mysql://localhost:3306/magma_benchmark?characterEncoding=UTF-8");
    prop.setProperty(JdbcDatasourceFactory.USERNAME, "root");
    prop.setProperty(JdbcDatasourceFactory.PASSWORD, "1234");

    benchmark(variables, nbEntities, prop, "jdbc-mysql");
  }

  public void benchmarkHsql(Collection<Variable> variables, int nbEntities) throws IOException {
    Properties prop = new Properties();
    prop.setProperty(JdbcDatasourceFactory.DRIVER_CLASS_NAME, "org.hsqldb.jdbcDriver");
    prop.setProperty(JdbcDatasourceFactory.URL, "jdbc:hsqldb:file:build/jdbc.db;shutdown=true");
    prop.setProperty(JdbcDatasourceFactory.USERNAME, "sa");
    prop.setProperty(JdbcDatasourceFactory.PASSWORD, "");

    benchmark(variables, nbEntities, prop, "jdbc-hsql");
  }

  private void benchmark(Collection<Variable> variables, int nbEntities, Properties jdbcProperties, String name)
      throws IOException {

    JdbcDatasourceFactory factory = new JdbcDatasourceFactory();
    factory.setName(name + "-" + nbEntities);
    factory.setJdbcProperties(jdbcProperties);
    factory.setDatasourceSettings(new JdbcDatasourceSettings(PARTICIPANT, null, null, true));
    Datasource datasource = factory.create();

    log.info("Generate Data for {}", datasource.getName());
    long start = System.currentTimeMillis();
    ValueTable valueTable = new GeneratedValueTable(datasource, variables, nbEntities);
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(valueTable, "Table1", datasource);
    long end = System.currentTimeMillis();

    Results.addStat(name, variables.size(), nbEntities, start, end);
    log.info("{} - Generated data ({} variables, {} entities) in {}", datasource.getName(), variables.size(),
        nbEntities, PeriodFormat.getDefault().print(new Period(start, end)));
  }

}
