package org.obiba.magma.benchmark;

@SuppressWarnings("ParameterHidesMemberVariable")
public abstract class BenchmarkItem {

  private String datasource;

  private String flavor;

  public abstract String getUid();

  public String getDatasource() {
    return datasource;
  }

  public void setDatasource(String datasource) {
    this.datasource = datasource;
  }

  public String getFlavor() {
    return flavor;
  }

  public void setFlavor(String flavor) {
    this.flavor = flavor;
  }

  public BenchmarkItem withDatasource(String datasource) {
    this.datasource = datasource;
    return this;
  }

  public BenchmarkItem withFlavor(String flavor) {
    this.flavor = flavor;
    return this;
  }

}
