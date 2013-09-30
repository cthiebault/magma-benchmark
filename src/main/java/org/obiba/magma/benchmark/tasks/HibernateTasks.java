package org.obiba.magma.benchmark.tasks;

import java.io.IOException;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.datasource.hibernate.HibernateDatasource;
import org.obiba.magma.datasource.hibernate.support.LocalSessionFactoryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.jta.JtaTransactionManager;

import com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup;

@Transactional
@Component("hibernateTasks")
public class HibernateTasks extends AbstractTransactionalTasks {

  @Autowired
  private JtaTransactionManager transactionManager;

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public Datasource createDatasource(BenchmarkItem item) throws Exception {
    SessionFactory sessionFactory = getSessionFactory(item.getFlavor());
    Datasource datasource = new HibernateDatasource(
        item.getDatasource() + "-" + item.getFlavor() + "-" + item.getNbEntities(), sessionFactory);
    MagmaEngine.get().addDatasource(datasource);
    return datasource;
  }

  private SessionFactory getSessionFactory(String flavor) throws IOException {
    LocalSessionFactoryProvider provider = applicationContext
        .getBean(flavor + "SessionFactoryProvider", LocalSessionFactoryProvider.class);
    Properties properties = new Properties();
    properties.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "jta");
    properties.setProperty(Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName());
    properties.setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, TransactionManagerLookup.class.getName());
    properties.setProperty(Environment.AUTO_CLOSE_SESSION, "true");
    properties.setProperty(Environment.FLUSH_BEFORE_COMPLETION, "true");
    provider.setProperties(properties);
    provider.setJtaTransactionManager(transactionManager);
    provider.initialise();
    return provider.getSessionFactory();
  }

  @Override
  public void deleteDatasource(Datasource datasource) throws Exception {
    super.deleteDatasource(datasource);
    // delete all entities
    ((HibernateDatasource) datasource).getSessionFactory().getCurrentSession()
        .createQuery("delete from VariableEntityState").executeUpdate();
  }
}
