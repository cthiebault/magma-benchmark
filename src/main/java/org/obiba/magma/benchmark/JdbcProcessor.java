package org.obiba.magma.benchmark;

import java.io.InputStream;
import java.util.Properties;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.datasource.jdbc.JdbcDatasourceFactory;
import org.obiba.magma.datasource.jdbc.JdbcDatasourceSettings;
import org.obiba.magma.support.DatasourceCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class JdbcProcessor implements ItemProcessor<BenchmarkItem, BenchmarkResult> {

  private static final Logger log = LoggerFactory.getLogger(JdbcProcessor.class);

  @Autowired
  private VariableRepository variableRepository;

  @Override
  public BenchmarkResult process(BenchmarkItem item) throws Exception {

    Properties prop = new Properties();
    InputStream in = getClass()
        .getResourceAsStream("/" + item.getDatasource() + "-" + item.getFlavor() + ".properties");
    prop.load(in);
    in.close();

    JdbcDatasourceFactory factory = new JdbcDatasourceFactory();
    factory.setName("jdbc-" + item.getFlavor() + "-" + item.getNbEntities());
    factory.setJdbcProperties(prop);
    factory.setDatasourceSettings(new JdbcDatasourceSettings(VariableRepository.PARTICIPANT, null, null, false));
    Datasource datasource = factory.create();

    log.info("Generate Data for {}", datasource.getName());
    long start = System.currentTimeMillis();
    ValueTable valueTable = new GeneratedValueTable(datasource, variableRepository.getVariables(),
        item.getNbEntities());
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(valueTable, "Table1", datasource);
    long end = System.currentTimeMillis();

    BenchmarkResult result = new BenchmarkResult();
    result.withStart(start).withEnd(end).withNbVariables(variableRepository.getNbVariables())
        .withDatasource(item.getDatasource()).withFlavor(item.getFlavor()).withNbEntities(item.getNbEntities());

    log.info("{} - Generated data ({} variables, {} entities) in {}", datasource.getName(),
        variableRepository.getNbVariables(), item.getNbEntities(),
        PeriodFormat.getDefault().print(new Period(start, end)));

    return result;
  }

}
