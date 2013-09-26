package org.obiba.magma.benchmark.processor;

import javax.sql.DataSource;

import org.obiba.magma.Datasource;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.benchmark.VariableRepository;
import org.obiba.magma.datasource.jdbc.JdbcDatasourceFactory;
import org.obiba.magma.datasource.jdbc.JdbcDatasourceSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("jdbcTasks")
public class JdbcTasks extends AbstractTransactionalTasks {

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public Datasource createDatasource(BenchmarkItem item) throws Exception {
    JdbcDatasourceFactory factory = new JdbcDatasourceFactory();
    factory.setName("jdbc-" + item.getFlavor() + "-" + item.getNbEntities());
    factory.setDataSource(getSqlDataSource(item.getFlavor()));
    factory.setDatasourceSettings(new JdbcDatasourceSettings(VariableRepository.PARTICIPANT, null, null, false));
    return factory.create();
  }

  private DataSource getSqlDataSource(String flavor) {
    return applicationContext.getBean(flavor + "DataSource", DataSource.class);
  }

}
