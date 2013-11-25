package org.obiba.magma.benchmark;

public class MagmaJBossTsTomcatJdbcBenchmark extends AbstractMagmaBenchmark {

  @Override
  protected String getContextXml() {
    return "/jbossts-tomcat-jdbc-context.xml";
  }

}
