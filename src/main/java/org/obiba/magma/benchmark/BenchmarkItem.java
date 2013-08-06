package org.obiba.magma.benchmark;

@SuppressWarnings("ParameterHidesMemberVariable")
public class BenchmarkItem {

  private String datasource;

  private String flavor;

  private int nbEntities;

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

  public int getNbEntities() {
    return nbEntities;
  }

  public void setNbEntities(int nbEntities) {
    this.nbEntities = nbEntities;
  }

  public BenchmarkItem withDatasource(String datasource) {
    this.datasource = datasource;
    return this;
  }

  public BenchmarkItem withFlavor(String flavor) {
    this.flavor = flavor;
    return this;
  }

  public BenchmarkItem withNbEntities(int nbEntities) {
    this.nbEntities = nbEntities;
    return this;
  }
}
