package org.obiba.magma.benchmark.importer;

import org.obiba.magma.Datasource;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.datasource.mongodb.MongoDBDatasource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientURI;

@Component
public class MongoDbProcessor extends AbstractDatasourceProcessor {

  private static final Logger log = LoggerFactory.getLogger(MongoDbProcessor.class);

  @Value("#{mongo['connectionURI']}")
  private String connectionURI;

  @Override
  protected Datasource createDatasource(BenchmarkItem item) {
    return new MongoDBDatasource("mongo-" + item.getNbEntities(), new MongoClientURI(connectionURI));
  }

}
