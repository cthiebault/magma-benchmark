package org.obiba.magma.benchmark;

public class MagmaBenchmark {

  public static void main(String... args) {
//    new HibernateDatasourceBenchmark(100, 3000).run();
//    new Neo4jDatasourceBenchmark(100, 3000).run();

    HibernateDatasourceBenchmark.getMySqlInstance(100, 300).run();

  }
}
