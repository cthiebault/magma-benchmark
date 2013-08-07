package org.obiba.magma.benchmark.processor;

import java.io.InputStream;
import java.util.Properties;

import org.obiba.magma.Datasource;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.benchmark.VariableRepository;
import org.obiba.magma.datasource.jdbc.JdbcDatasourceFactory;
import org.obiba.magma.datasource.jdbc.JdbcDatasourceSettings;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class JdbcProcessor extends AbstractDatasourceProcessor {

  @Override
  protected Datasource createDatasource(BenchmarkItem item) throws Exception {
    Properties prop = new Properties();
    InputStream in = getClass()
        .getResourceAsStream("/" + item.getDatasource() + "-" + item.getFlavor() + ".properties");
    prop.load(in);
    in.close();

    JdbcDatasourceFactory factory = new JdbcDatasourceFactory();
    factory.setName("jdbc-" + item.getFlavor() + "-" + item.getNbEntities());
    factory.setJdbcProperties(prop);
    factory.setDatasourceSettings(new JdbcDatasourceSettings(VariableRepository.PARTICIPANT, null, null, false));
    return factory.create();
  }

}
