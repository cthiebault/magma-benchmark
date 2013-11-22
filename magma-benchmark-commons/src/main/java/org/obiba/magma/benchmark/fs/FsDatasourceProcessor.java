package org.obiba.magma.benchmark.fs;

import java.io.File;
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
import org.springframework.util.ResourceUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

public class FsDatasourceProcessor implements ItemProcessor<FsDatasourceBenchmarkItem, FsDatasourceBenchmarkResult> {

  private static final Logger log = LoggerFactory.getLogger(FsDatasourceProcessor.class);

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public FsDatasourceBenchmarkResult process(FsDatasourceBenchmarkItem item) throws Exception {
    AbstractTransactionalTasks tasks = applicationContext
        .getBean(item.getDatasource() + "Tasks", AbstractTransactionalTasks.class);

    FsDatasourceBenchmarkResult result = new FsDatasourceBenchmarkResult();
    result.withSrcFsDatasource(item.getSrcFsDatasource()) //
        .withDatasource(item.getDatasource()) //
        .withFlavor(item.getFlavor());

    Datasource datasource = tasks.createDatasource(item);

    Stopwatch stopwatch = Stopwatch.createStarted();
    File file = ResourceUtils.getFile("classpath:" + item.getSrcFsDatasource());
    Preconditions.checkArgument(file.exists(), "File " + file.getAbsolutePath() + " does not exists");
    tasks.importFsDatasource(file, datasource);
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
  public void beforeProcess(FsDatasourceBenchmarkItem item) {
    log.info("Start benchmark of {} {}", item.getDatasource(), item.getFlavor());
  }

  @AfterProcess
  public void afterProcess(FsDatasourceBenchmarkItem item, FsDatasourceBenchmarkResult result) {
    log.info("Finished benchmark of {} imported from {} in {}", result.getDatasource(), result.getSrcFsDatasource(),
        result.getImportDurationFormatted());
  }

}
