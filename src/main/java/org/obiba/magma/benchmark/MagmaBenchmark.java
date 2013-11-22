package org.obiba.magma.benchmark;

public class MagmaBenchmark {

  private MagmaBenchmark() {}

  public static void main(String... args) throws Exception {
//    MagmaAtomikosBenchmark.runWithGeneratedData(100);
    new MagmaAtomikosBenchmark().startFsDatasourceJobs();
  }
}