package org.obiba.magma.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class MagmaBenchmark {

  private static final Logger log = LoggerFactory.getLogger(MagmaBenchmark.class);

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  private Job benchmarkJob;

  public void run(long nbVariables) throws JobExecutionException {
    jobLauncher.run(benchmarkJob, new JobParametersBuilder().addLong("nbVariables", nbVariables).toJobParameters());
  }

  public static void main(String... args) throws Exception {
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("/application-context.xml");
    MagmaBenchmark benchmark = applicationContext.getBean(MagmaBenchmark.class);
    benchmark.run(100);
  }
}
