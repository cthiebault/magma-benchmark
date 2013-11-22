package org.obiba.magma.benchmark.generated;

import java.util.concurrent.TimeUnit;

import org.obiba.magma.Datasource;
import org.obiba.magma.benchmark.tasks.AbstractTransactionalTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.common.base.Stopwatch;

public class GeneratedDatasourceProcessor
    implements ItemProcessor<GeneratedDatasourceBenchmarkItem, GeneratedDatasourceBenchmarkResult> {

  private static final Logger log = LoggerFactory.getLogger(GeneratedDatasourceProcessor.class);

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private VariableRepository variableRepository;

  @Override
  public GeneratedDatasourceBenchmarkResult process(GeneratedDatasourceBenchmarkItem item) throws Exception {
    AbstractTransactionalTasks tasks = applicationContext
        .getBean(item.getDatasource() + "Tasks", AbstractTransactionalTasks.class);

    GeneratedDatasourceBenchmarkResult result = new GeneratedDatasourceBenchmarkResult();
    result.withNbVariables(variableRepository.getNbVariables()) //
        .withNbEntities(item.getNbEntities()) //
        .withDatasource(item.getDatasource()) //
        .withFlavor(item.getFlavor());

    Datasource datasource = tasks.createDatasource(item);

    Stopwatch stopwatch = Stopwatch.createStarted();
    tasks.importGeneratedData(item.getNbEntities(), datasource);
    result.withImportDuration(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));

    stopwatch.start();
    tasks.readVector(datasource);
    result.withVectorReadDuration(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));

    stopwatch.start();
    tasks.deleteDatasource(datasource);
    result.withDeleteDuration(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));

    return result;
  }

  @BeforeProcess
  public void beforeProcess(GeneratedDatasourceBenchmarkItem item) {
    log.info("Start benchmark of {} {}", item.getDatasource(), item.getFlavor());
  }

  @AfterProcess
  public void afterProcess(GeneratedDatasourceBenchmarkItem item, GeneratedDatasourceBenchmarkResult result) {
    log.info("Finished benchmark of {} - Generated data ({} variables, {} entities) in {}", result.getDatasource(),
        result.getNbVariables(), result.getNbEntities(), result.getImportDurationFormatted());
  }

}
