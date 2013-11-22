package org.obiba.magma.benchmark.fs;

import org.obiba.magma.benchmark.BenchmarkItem;

@SuppressWarnings("ParameterHidesMemberVariable")
public class FsDatasourceBenchmarkItem extends BenchmarkItem {

  private String srcFsDatasource;

  @Override
  public String getUid() {
    return srcFsDatasource;
  }

  public String getSrcFsDatasource() {
    return srcFsDatasource;
  }

  public void setSrcFsDatasource(String srcFsDatasource) {
    this.srcFsDatasource = srcFsDatasource;
  }

  public FsDatasourceBenchmarkItem withSrcFsDatasource(String srcFsDatasource) {
    this.srcFsDatasource = srcFsDatasource;
    return this;
  }

}
