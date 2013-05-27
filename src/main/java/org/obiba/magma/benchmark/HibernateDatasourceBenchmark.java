package org.obiba.magma.benchmark;

import org.hibernate.SessionFactory;
import org.obiba.magma.Datasource;
import org.obiba.magma.datasource.hibernate.HibernateDatasource;

public class HibernateDatasourceBenchmark extends AbstractDatasourceBenchmark {

  private final SessionFactory sessionFactory;

  public HibernateDatasourceBenchmark(int nbVariables, int nbEntities, SessionFactory sessionFactory) {
    super(nbVariables, nbEntities);
    this.sessionFactory = sessionFactory;
  }

  @Override
  protected Datasource createDatasource() {
    return new HibernateDatasource("hibernate", sessionFactory);
  }
}
