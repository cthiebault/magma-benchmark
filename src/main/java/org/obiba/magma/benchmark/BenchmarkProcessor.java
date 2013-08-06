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

  @Override
  public BenchmarkResult process(BenchmarkItem item) throws Exception {
    if("jdbc".equalsIgnoreCase(item.getDatasource())) {
      return jdbcProcessor.process(item);
    }
    if("hibernate".equalsIgnoreCase(item.getDatasource())) {
      return hibernateProcessor.process(item);
    }
    return null;
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
