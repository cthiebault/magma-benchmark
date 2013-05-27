package org.obiba.magma.benchmark;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.obiba.magma.Datasource;
import org.obiba.magma.datasource.hibernate.HibernateDatasource;
import org.obiba.magma.datasource.hibernate.support.LocalSessionFactoryProvider;

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

  public static HibernateDatasourceBenchmark getMySqlInstance(int nbVariables, int nbEntities) {
    LocalSessionFactoryProvider provider = new LocalSessionFactoryProvider("com.mysql.jdbc.Driver", //
        "jdbc:mysql://localhost:3306/magma_benchmark?characterEncoding=UTF-8", //
        "root", //
        "1234", //
        "org.hibernate.dialect.MySQL5InnoDBDialect");
    Properties p = new Properties();
    p.setProperty(Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider");
    provider.setProperties(p);
    provider.initialise();
    return new HibernateDatasourceBenchmark(nbVariables, nbEntities, provider.getSessionFactory());
  }

  public static HibernateDatasourceBenchmark getHsqlInstance(int nbVariables, int nbEntities) {
    LocalSessionFactoryProvider provider = new LocalSessionFactoryProvider("org.hsqldb.jdbcDriver", //
        "jdbc:hsqldb:mem:magma_benchmark;shutdown=true", //
        "sa", //
        "", //
        "org.hibernate.dialect.HSQLDialect");
    Properties p = new Properties();
    p.setProperty(Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider");
    provider.setProperties(p);
    provider.initialise();
    return new HibernateDatasourceBenchmark(nbVariables, nbEntities, provider.getSessionFactory());
  }

}
