package org.obiba.magma.benchmark;

import java.io.IOException;
import java.util.Collection;

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
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Neo4jDatasourceBenchmark {

  private static final Logger log = LoggerFactory.getLogger(Neo4jDatasourceBenchmark.class);

  public void generateData(Collection<Variable> variables, int nbEntities) throws IOException {
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
    Datasource datasource = new Neo4jDatasourceFactory("neo4j", applicationContext).create();

    log.info("Generate Data for {}", datasource.getName());
    long start = System.currentTimeMillis();
    ValueTable valueTable = new GeneratedValueTable(datasource, variables, nbEntities);
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(valueTable, "Table1", datasource);
    long end = System.currentTimeMillis();
    log.info("{} - Generated data ({} variables, {} entities) in {}", datasource.getName(), variables.size(),
        nbEntities, PeriodFormat.getDefault().print(new Period(start, end)));
  }

}
