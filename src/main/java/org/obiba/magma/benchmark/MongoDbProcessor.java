package org.obiba.magma.benchmark;

import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.datasource.mongodb.MongoDBDatasource;
import org.obiba.magma.support.DatasourceCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientURI;

@Component
public class MongoDbProcessor implements ItemProcessor<BenchmarkItem, BenchmarkResult> {

  private static final Logger log = LoggerFactory.getLogger(MongoDbProcessor.class);

  @Value("#{mongo['connectionURI']}")
  private String connectionURI;

  @Autowired
  private VariableRepository variableRepository;

  @Override
  public BenchmarkResult process(BenchmarkItem item) throws Exception {

    log.debug("connectionURI: {}", connectionURI);

    MongoDBDatasource datasource = new MongoDBDatasource("mongo-" + item.getNbEntities(),
        new MongoClientURI(connectionURI));

    long start = System.currentTimeMillis();
    ValueTable valueTable = new GeneratedValueTable(datasource, variableRepository.getVariables(),
        item.getNbEntities());
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(valueTable, "Table1", datasource);
    long end = System.currentTimeMillis();

    BenchmarkResult result = new BenchmarkResult();
    result.withStart(start).withEnd(end).withNbVariables(variableRepository.getNbVariables())
        .withDatasource(item.getDatasource()).withFlavor(item.getFlavor()).withNbEntities(item.getNbEntities());

    return result;
  }

}
