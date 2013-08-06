package org.obiba.magma.benchmark;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class BenchmarkProcessor implements ItemProcessor<BenchmarkItem, BenchmarkResult> {

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

}
