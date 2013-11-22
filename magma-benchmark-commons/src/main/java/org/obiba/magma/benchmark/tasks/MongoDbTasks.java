package org.obiba.magma.benchmark.tasks;

import org.obiba.magma.Datasource;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.datasource.mongodb.MongoDBDatasource;
import org.obiba.magma.datasource.mongodb.MongoDBFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("mongoTasks")
public class MongoDbTasks extends AbstractTransactionalTasks {

  @Value("#{mongo['connectionURI']}")
  private String connectionURI;

  @Override
  public Datasource createDatasource(BenchmarkItem item) {
    return new MongoDBDatasource("mongo-" + item.getUid(), new MongoDBFactory(connectionURI));
  }

}
