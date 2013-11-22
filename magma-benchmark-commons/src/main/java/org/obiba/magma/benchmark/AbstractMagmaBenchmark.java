package org.obiba.magma.benchmark;

import org.obiba.magma.MagmaEngine;
import org.obiba.magma.xstream.MagmaXStreamExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecutionException;

public abstract class AbstractMagmaBenchmark {

  private static final Logger log = LoggerFactory.getLogger(AbstractMagmaBenchmark.class);

  public AbstractMagmaBenchmark() {
    log.info("Start MagmaEngine");
    new MagmaEngine().extend(new MagmaXStreamExtension());
  }

  public abstract void startGeneratedDatasourceJobs(long nbVariables) throws JobExecutionException;

  public abstract void startFsDatasourceJobs() throws JobExecutionException;
}
