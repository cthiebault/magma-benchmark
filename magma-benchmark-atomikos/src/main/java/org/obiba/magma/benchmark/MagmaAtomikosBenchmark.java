package org.obiba.magma.benchmark;

public class MagmaAtomikosBenchmark extends AbstractMagmaBenchmark {

  @Override
  protected String getContextXml() {
    return "/atomikos-context.xml";
  }

}
