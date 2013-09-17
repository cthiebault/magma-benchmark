package org.obiba.magma.benchmark.processor;

import java.io.IOException;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.NoSuchDatasourceException;
import org.obiba.magma.ValueTable;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.benchmark.BenchmarkResult;
import org.obiba.magma.benchmark.VariableRepository;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.datasource.hibernate.HibernateDatasource;
import org.obiba.magma.datasource.hibernate.support.LocalSessionFactoryProvider;
import org.obiba.magma.support.DatasourceCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HibernateProcessor implements ItemProcessor<BenchmarkItem, BenchmarkResult> {

  private static final Logger log = LoggerFactory.getLogger(HibernateProcessor.class);

  @Value("#{mysql['driverClassName']}")
  private String mysqlDriver;

  @Value("#{mysql['url']}")
  private String mysqlUrl;

  @Value("#{mysql['username']}")
  private String mysqlUsername;

  @Value("#{mysql['password']}")
  private String mysqlPassword;

  @Value("#{hsql['driverClassName']}")
  private String hsqlDriver;

  @Value("#{hsql['url']}")
  private String hsqlUrl;

  @Value("#{hsql['username']}")
  private String hsqlUsername;

  @Value("#{hsql['password']}")
  private String hsqlPassword;

  @Value("#{postgresql['driverClassName']}")
  private String postgresqlDriver;

  @Value("#{postgresql['url']}")
  private String postgresqlUrl;

  @Value("#{postgresql['username']}")
  private String postgresqlUsername;

  @Value("#{postgresql['password']}")
  private String postgresqlPassword;

  @Value("#{mariadb['driverClassName']}")
  private String mariadbDriver;

  @Value("#{mariadb['url']}")
  private String mariadbUrl;

  @Value("#{mariadb['username']}")
  private String mariadbUsername;

  @Value("#{mariadb['password']}")
  private String mariadbPassword;

  @Autowired
  private VariableRepository variableRepository;

  @Override
  public BenchmarkResult process(BenchmarkItem item) throws Exception {

    SessionFactory sessionFactory = getSessionFactory(item.getFlavor());

    int nbEntities = item.getNbEntities();
    Datasource datasource = new HibernateDatasource(item.getDatasource() + "-" + item.getFlavor() + "-" + nbEntities,
        sessionFactory);

    BenchmarkResult result = new BenchmarkResult();
    result.withNbVariables(variableRepository.getNbVariables()) //
        .withDatasource(item.getDatasource()) //
        .withFlavor(item.getFlavor()) //
        .withNbEntities(nbEntities);

    importData(nbEntities, datasource, sessionFactory, result);

    readVector(datasource, result);

    deleteDatasource(datasource.getName(), sessionFactory, result);

    return result;
  }

  private void importData(int nbEntities, Datasource datasource, SessionFactory sessionFactory, BenchmarkResult result)
      throws IOException {
    long importStart = System.currentTimeMillis();
    ValueTable generatedValueTable = new GeneratedValueTable(datasource, variableRepository.getVariables(), nbEntities);
    sessionFactory.getCurrentSession().beginTransaction();
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(generatedValueTable, "Table1", datasource);
    sessionFactory.getCurrentSession().getTransaction().commit();
    long importEnd = System.currentTimeMillis();
    result.withImportDuration(importEnd - importStart);
  }

  private void readVector(Datasource datasource, BenchmarkResult result) {
    long readStart = System.currentTimeMillis();
    AbstractDatasourceProcessor.readVector(datasource);
    long readEnd = System.currentTimeMillis();
    result.withVectorReadDuration(readEnd - readStart);
  }

  public void deleteDatasource(String name, SessionFactory sessionFactory, BenchmarkResult result)
      throws NoSuchDatasourceException {
    long start = System.currentTimeMillis();
    sessionFactory.getCurrentSession().beginTransaction();
    Datasource datasource = MagmaEngine.get().getDatasource(name);
    MagmaEngine.get().removeDatasource(datasource);
    datasource.drop();

    // delete all entities
    sessionFactory.getCurrentSession().createQuery("delete from VariableEntityState").executeUpdate();

    sessionFactory.getCurrentSession().getTransaction().commit();
    long end = System.currentTimeMillis();
    result.withDeleteDuration(end - start);
  }

  @SuppressWarnings("IfStatementWithTooManyBranches")
  private SessionFactory getSessionFactory(String flavor) throws IOException {
    LocalSessionFactoryProvider provider = null;
    if("mysql".equalsIgnoreCase(flavor)) {
      provider = createMySqlSessionFactoryProvider();
    } else if("hsql".equalsIgnoreCase(flavor)) {
      provider = createHsqlSessionFactoryProvider();
    } else if("postgresql".equalsIgnoreCase(flavor)) {
      provider = createPostgreSqlSessionFactoryProvider();
    } else if("mariadb".equalsIgnoreCase(flavor)) {
      provider = createMariaDbSessionFactoryProvider();
    } else {
      throw new IllegalArgumentException(
          "Unknown hibernate dialect " + flavor + ". Supports [mysql, hsql, postgresql, mariadb]");
    }

    Properties p = new Properties();
    p.setProperty(Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider");
    provider.setProperties(p);
    provider.initialise();
    return provider.getSessionFactory();
  }

  private LocalSessionFactoryProvider createMySqlSessionFactoryProvider() throws IOException {
    return new LocalSessionFactoryProvider(mysqlDriver, mysqlUrl, mysqlUsername, mysqlPassword,
        "org.hibernate.dialect.MySQL5InnoDBDialect");
  }

  private LocalSessionFactoryProvider createHsqlSessionFactoryProvider() throws IOException {
    return new LocalSessionFactoryProvider(hsqlDriver, hsqlUrl, hsqlUsername, hsqlPassword,
        "org.hibernate.dialect.HSQLDialect");
  }

  private LocalSessionFactoryProvider createPostgreSqlSessionFactoryProvider() throws IOException {
    return new LocalSessionFactoryProvider(postgresqlDriver, postgresqlUrl, postgresqlUsername, postgresqlPassword,
        "org.hibernate.dialect.PostgreSQLDialect");
  }

  private LocalSessionFactoryProvider createMariaDbSessionFactoryProvider() throws IOException {
    return new LocalSessionFactoryProvider(mariadbDriver, mariadbUrl, mariadbUsername, mariadbPassword,
        "org.hibernate.dialect.MySQL5InnoDBDialect");
  }

}
