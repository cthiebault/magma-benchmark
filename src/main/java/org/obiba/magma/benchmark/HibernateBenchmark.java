package org.obiba.magma.benchmark;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import org.hibernate.cfg.Environment;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.Variable;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.datasource.hibernate.HibernateDatasource;
import org.obiba.magma.datasource.hibernate.SessionFactoryProvider;
import org.obiba.magma.datasource.hibernate.support.LocalSessionFactoryProvider;
import org.obiba.magma.support.DatasourceCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HibernateBenchmark {

  private static final Logger log = LoggerFactory.getLogger(HibernateBenchmark.class);

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

  public void benchmarkMySql(Collection<Variable> variables, int nbEntities) throws IOException {
    LocalSessionFactoryProvider provider = new LocalSessionFactoryProvider(mysqlDriver, mysqlUrl, mysqlUsername,
        mysqlPassword, "org.hibernate.dialect.MySQL5InnoDBDialect");
    Properties p = new Properties();
    p.setProperty(Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider");
    provider.setProperties(p);
    provider.initialise();

    benchmarkHibernate(variables, nbEntities, provider, "hibernate-mysql");
  }

  public void benchmarkHsql(Collection<Variable> variables, int nbEntities) throws IOException {
    LocalSessionFactoryProvider provider = new LocalSessionFactoryProvider(hsqlDriver, hsqlUrl, hsqlUsername,
        hsqlPassword, "org.hibernate.dialect.HSQLDialect");
    Properties p = new Properties();
    p.setProperty(Environment.CACHE_PROVIDER, "org.hibernate.cache.HashtableCacheProvider");
    provider.setProperties(p);
    provider.initialise();

    benchmarkHibernate(variables, nbEntities, provider, "hibernate-hsql");
  }

  private void benchmarkHibernate(Collection<Variable> variables, int nbEntities, SessionFactoryProvider provider,
      String name) throws IOException {

    HibernateDatasource datasource = new HibernateDatasource(name + "-" + nbEntities, provider.getSessionFactory());

    log.info("Generate Data for {}", datasource.getName());
    long start = System.currentTimeMillis();
    ValueTable generatedValueTable = new GeneratedValueTable(datasource, variables, nbEntities);
    provider.getSessionFactory().getCurrentSession().beginTransaction();
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(generatedValueTable, "NewTable", datasource);
    provider.getSessionFactory().getCurrentSession().getTransaction().commit();
    long end = System.currentTimeMillis();
    Results.addStat(name, variables.size(), nbEntities, start, end);

    log.info("{} - Generated data ({} variables, {} entities) in {}", datasource.getName(), variables.size(),
        nbEntities, PeriodFormat.getDefault().print(new Period(start, end)));
  }

}
