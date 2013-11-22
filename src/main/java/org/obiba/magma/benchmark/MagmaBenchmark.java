package org.obiba.magma.benchmark;

public class MagmaBenchmark {

  private MagmaBenchmark() {}

  public static void main(String... args) throws Exception {
//    MagmaAtomikosBenchmark.runWithGeneratedData(100);
    MagmaAtomikosBenchmark.runWithFsDatasource(MagmaBenchmark.class.getResource("/5-onyx-data.zip").toString());
  }
}