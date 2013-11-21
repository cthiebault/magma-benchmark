package org.obiba.magma.benchmark.tasks;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.benchmark.support.SessionFactoryFactory;
import org.obiba.magma.datasource.hibernate.HibernateDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component("hibernateTasks")
public class HibernateTasks extends AbstractTransactionalTasks {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private SessionFactoryFactory sessionFactoryFactory;

  @Override
  public Datasource createDatasource(BenchmarkItem item) throws Exception {
    SessionFactory sessionFactory = getSessionFactory(item.getFlavor());
    Datasource datasource = new HibernateDatasource(
        item.getDatasource() + "-" + item.getFlavor() + "-" + item.getNbEntities(), sessionFactory);
    MagmaEngine.get().addDatasource(datasource);
    return datasource;
  }

  private SessionFactory getSessionFactory(String flavor) {
    return sessionFactoryFactory.getSessionFactory(applicationContext.getBean(flavor + "DataSource", DataSource.class));
  }

  @Override
  public void deleteDatasource(Datasource datasource) throws Exception {
    super.deleteDatasource(datasource);
    // delete all entities
    ((HibernateDatasource) datasource).getSessionFactory().getCurrentSession()
        .createQuery("delete from VariableEntityState").executeUpdate();
  }
}
