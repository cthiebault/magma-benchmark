package org.obiba.magma.benchmark;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public class MagmaAtomikosBenchmark {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job generatedDatasourceJob;

  @Autowired
  private Job fsDatasourceJob;

  public void startGeneratedDatasourceJobs(long nbVariables) throws JobExecutionException {
    jobLauncher
        .run(generatedDatasourceJob, new JobParametersBuilder().addLong("nbVariables", nbVariables).toJobParameters());
  }

  public void startFsDatasourceJobs() throws JobExecutionException {
    jobLauncher.run(fsDatasourceJob, new JobParametersBuilder().toJobParameters());
  }

  public static void runWithGeneratedDatasource(long nbVariables) throws Exception {
    new ClassPathXmlApplicationContext("/atomikos-context.xml") //
        .getBean(MagmaAtomikosBenchmark.class) //
        .startGeneratedDatasourceJobs(nbVariables);
  }

  public static void runWithFsDatasource() throws Exception {
    new ClassPathXmlApplicationContext("/atomikos-context.xml") //
        .getBean(MagmaAtomikosBenchmark.class) //
        .startFsDatasourceJobs();
  }

}
