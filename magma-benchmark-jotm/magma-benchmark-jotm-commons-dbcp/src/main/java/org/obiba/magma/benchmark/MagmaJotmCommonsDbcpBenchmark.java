package org.obiba.magma.benchmark;

public class MagmaJotmCommonsDbcpBenchmark extends AbstractMagmaBenchmark {

  @Override
  protected String getContextXml() {
    return "/jotm-commons-dbcp-context.xml";
  }

}
