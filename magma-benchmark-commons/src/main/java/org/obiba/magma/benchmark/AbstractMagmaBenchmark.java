package org.obiba.magma.benchmark;

import org.obiba.magma.MagmaEngine;
import org.obiba.magma.xstream.MagmaXStreamExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class AbstractMagmaBenchmark {

  private static final Logger log = LoggerFactory.getLogger(AbstractMagmaBenchmark.class);

  public AbstractMagmaBenchmark() {
    log.info("Start MagmaEngine");
    new MagmaEngine().extend(new MagmaXStreamExtension());

    // JBossTS config
    System.setProperty("com.arjuna.ats.arjuna.objectstore.objectStoreDir", "build/jbossts");
    System.setProperty("ObjectStoreEnvironmentBean.objectStoreDir", "build/jbossts");
    System.setProperty("org.jboss.logging.provider", "slf4j");
  }

  protected abstract String getContextXml();

  public void startGeneratedDatasourceJobs(long nbVariables) throws JobExecutionException {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(getContextXml());
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean("generatedDatasourceJob", Job.class);
    jobLauncher.run(job, new JobParametersBuilder().addLong("nbVariables", nbVariables).toJobParameters());
  }

  public void startFsDatasourceJobs() throws JobExecutionException {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(getContextXml());
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean("fsDatasourceJob", Job.class);
    jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
  }
}
