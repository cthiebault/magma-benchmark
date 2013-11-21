package org.obiba.magma.benchmark;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MagmaAtomikosBenchmark {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job benchmarkJob;

  public void startJobs(long nbVariables) throws JobExecutionException {
    jobLauncher.run(benchmarkJob, new JobParametersBuilder().addLong("nbVariables", nbVariables).toJobParameters());
  }

  public static void run(long nbVariables) throws Exception {
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/atomikos-context.xml");
    MagmaAtomikosBenchmark benchmark = applicationContext.getBean(MagmaAtomikosBenchmark.class);
    benchmark.startJobs(nbVariables);
  }
}
