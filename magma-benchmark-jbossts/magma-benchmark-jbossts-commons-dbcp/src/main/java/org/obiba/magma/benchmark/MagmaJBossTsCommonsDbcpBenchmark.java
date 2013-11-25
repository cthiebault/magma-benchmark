package org.obiba.magma.benchmark;

public class MagmaJBossTsCommonsDbcpBenchmark extends AbstractMagmaBenchmark {

  @Override
  protected String getContextXml() {
    return "/jbossts-commons-dbcp-context.xml";
  }

}
