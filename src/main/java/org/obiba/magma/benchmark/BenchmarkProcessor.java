package org.obiba.magma.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class BenchmarkProcessor implements ItemProcessor<BenchmarkItem, BenchmarkResult> {

  private static final Logger log = LoggerFactory.getLogger(BenchmarkProcessor.class);

  @Autowired
  private JdbcProcessor jdbcProcessor;

  @Autowired
  private HibernateProcessor hibernateProcessor;

  @Autowired
  private MongoDbProcessor mongoDbProcessor;

  @Override
  public BenchmarkResult process(BenchmarkItem item) throws Exception {
    String datasource = item.getDatasource();
    if("jdbc".equalsIgnoreCase(datasource)) {
      return jdbcProcessor.process(item);
    }
    if("hibernate".equalsIgnoreCase(datasource)) {
      return hibernateProcessor.process(item);
    }
    if("mongo".equalsIgnoreCase(datasource)) {
      return mongoDbProcessor.process(item);
    }
    throw new IllegalArgumentException("Datasource " + datasource + " is not supported");
  }

  @BeforeProcess
  public void beforeProcess(BenchmarkItem item) {
    log.info("Generate Data for {} {}", item.getDatasource(), item.getFlavor());
  }

  @AfterProcess
  public void afterProcess(BenchmarkItem item, BenchmarkResult result) {
    log.info("{} - Generated data ({} variables, {} entities) in {}", result.getDatasource(), result.getNbVariables(),
        result.getNbEntities(), result.getDuration());
  }

}
