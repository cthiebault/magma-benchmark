package org.obiba.magma.benchmark.processor;

import org.obiba.magma.Datasource;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.datasource.mongodb.MongoDBDatasource;
import org.obiba.magma.datasource.mongodb.MongoDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MongoDbProcessor extends AbstractDatasourceProcessor {

  @Value("#{mongo['connectionURI']}")
  private String connectionURI;

  @Override
  protected Datasource createDatasource(BenchmarkItem item) {
    return new MongoDBDatasource("mongo-" + item.getNbEntities(), new MongoDBFactory(connectionURI));
  }

}
