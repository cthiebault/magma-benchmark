package org.obiba.magma.benchmark;

import org.obiba.magma.Datasource;
import org.obiba.magma.benchmark.tasks.AbstractTransactionalTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class BenchmarkProcessor implements ItemProcessor<BenchmarkItem, BenchmarkResult> {

  private static final Logger log = LoggerFactory.getLogger(BenchmarkProcessor.class);

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private VariableRepository variableRepository;

  @Override
  public BenchmarkResult process(BenchmarkItem item) throws Exception {
    AbstractTransactionalTasks tasks = applicationContext
        .getBean(item.getDatasource() + "Tasks", AbstractTransactionalTasks.class);

    BenchmarkResult result = new BenchmarkResult();
    result.withNbVariables(variableRepository.getNbVariables()) //
        .withDatasource(item.getDatasource()) //
        .withFlavor(item.getFlavor()) //
        .withNbEntities(item.getNbEntities());

    Datasource datasource = tasks.createDatasource(item);
    tasks.importData(item.getNbEntities(), datasource, result);
    tasks.readVector(datasource, result);
    tasks.deleteDatasource(datasource, result);
    return result;
  }

  @BeforeProcess
  public void beforeProcess(BenchmarkItem item) {
    log.info("Start benchmark of {} {}", item.getDatasource(), item.getFlavor());
  }

  @AfterProcess
  public void afterProcess(BenchmarkItem item, BenchmarkResult result) {
    log.info("Finished benchmark of {} - Generated data ({} variables, {} entities) in {}", result.getDatasource(),
        result.getNbVariables(), result.getNbEntities(), result.getImportDurationFormatted());
  }

}
