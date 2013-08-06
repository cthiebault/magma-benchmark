package org.obiba.magma.benchmark;

import java.io.IOException;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
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

  @Autowired
  private VariableRepository variableRepository;

  @Override
  public BenchmarkResult process(BenchmarkItem item) throws Exception {

    SessionFactory sessionFactory = getSessionFactory(item.getFlavor());

    int nbEntities = item.getNbEntities();
    HibernateDatasource datasource = new HibernateDatasource(
        item.getDatasource() + "-" + item.getFlavor() + "-" + nbEntities, sessionFactory);

    log.info("Generate Data for {}", datasource.getName());
    long start = System.currentTimeMillis();
    ValueTable generatedValueTable = new GeneratedValueTable(datasource, variableRepository.getVariables(), nbEntities);
    sessionFactory.getCurrentSession().beginTransaction();
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(generatedValueTable, "NewTable", datasource);
    sessionFactory.getCurrentSession().getTransaction().commit();
    long end = System.currentTimeMillis();

    BenchmarkResult result = new BenchmarkResult();
    int nbVariables = variableRepository.getNbVariables();
    result.withStart(start).withEnd(end).withNbVariables(nbVariables).withDatasource(item.getDatasource())
        .withFlavor(item.getFlavor()).withNbEntities(nbEntities);

    log.info("{} - Generated data ({} variables, {} entities) in {}", datasource.getName(), nbVariables, nbEntities,
        PeriodFormat.getDefault().print(new Period(start, end)));

    return result;
  }

  private SessionFactory getSessionFactory(String flavor) throws IOException {
    SessionFactory sessionFactory = null;
    if("mysql".equalsIgnoreCase(flavor)) {
      sessionFactory = createMySqlSessionFactory();
    } else if("hsql".equalsIgnoreCase(flavor)) {
      sessionFactory = createHsqlSessionFactory();
    } else {
      throw new IllegalArgumentException("Unknown hibernate dialect " + flavor + ". Supports [mysql, hsql]");
    }
    return sessionFactory;
  }

  private SessionFactory createMySqlSessionFactory() throws IOException {
    LocalSessionFactoryProvider provider = new LocalSessionFactoryProvider(mysqlDriver, mysqlUrl, mysqlUsername,
        mysqlPassword, "org.hibernate.dialect.MySQL5InnoDBDialect");
    Properties p = new Properties();
    p.setProperty(Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider");
    provider.setProperties(p);
    provider.initialise();
    return provider.getSessionFactory();
  }

  private SessionFactory createHsqlSessionFactory() throws IOException {
    LocalSessionFactoryProvider provider = new LocalSessionFactoryProvider(hsqlDriver, hsqlUrl, hsqlUsername,
        hsqlPassword, "org.hibernate.dialect.HSQLDialect");
    Properties p = new Properties();
    p.setProperty(Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider");
    provider.setProperties(p);
    provider.initialise();
    return provider.getSessionFactory();
  }

}
