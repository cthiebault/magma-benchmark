package org.obiba.magma.benchmark;

public class MagmaJotmTomcatJdbcBenchmark extends AbstractMagmaBenchmark {

  @Override
  protected String getContextXml() {
    return "/jotm-tomcat-jdbc-context.xml";
  }
}
