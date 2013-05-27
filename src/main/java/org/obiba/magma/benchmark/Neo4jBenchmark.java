package org.obiba.magma.benchmark;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.Resource;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.Variable;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.datasource.neo4j.Neo4jDatasourceFactory;
import org.obiba.magma.support.DatasourceCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class Neo4jBenchmark {

  private static final Logger log = LoggerFactory.getLogger(Neo4jBenchmark.class);

  @Resource
  private ApplicationContext applicationContext;

  public void benchmark(Collection<Variable> variables, int nbEntities) throws IOException {
    Datasource datasource = new Neo4jDatasourceFactory("neo4j-" + nbEntities, applicationContext).create();

    log.info("Generate Data for {}", datasource.getName());
    long start = System.currentTimeMillis();
    ValueTable valueTable = new GeneratedValueTable(datasource, variables, nbEntities);
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(valueTable, "Table1", datasource);
    long end = System.currentTimeMillis();

    Results.addStat("neo4j", variables.size(), nbEntities, start, end);
    log.info("{} - Generated data ({} variables, {} entities) in {}", datasource.getName(), variables.size(),
        nbEntities, PeriodFormat.getDefault().print(new Period(start, end)));
  }

}
