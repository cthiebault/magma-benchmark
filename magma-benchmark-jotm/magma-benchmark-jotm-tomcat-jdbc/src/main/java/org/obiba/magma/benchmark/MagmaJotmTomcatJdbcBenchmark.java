package org.obiba.magma.benchmark;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MagmaJotmTomcatJdbcBenchmark extends AbstractMagmaBenchmark {

  @Override
  public void startGeneratedDatasourceJobs(long nbVariables) throws JobExecutionException {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
        "/jotm-tomcat-jdbc-context.xml");
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean("generatedDatasourceJob", Job.class);
    jobLauncher.run(job, new JobParametersBuilder().addLong("nbVariables", nbVariables).toJobParameters());
  }

  @Override
  public void startFsDatasourceJobs() throws JobExecutionException {
    ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
        "/jotm-tomcat-jdbc-context.xml");
    JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
    Job job = applicationContext.getBean("fsDatasourceJob", Job.class);
    jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
  }

}
