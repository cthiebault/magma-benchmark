package org.obiba.magma.benchmark.generated;

import org.obiba.magma.benchmark.BenchmarkItem;

import com.google.common.base.Strings;

@SuppressWarnings("ParameterHidesMemberVariable")
public class GeneratedDatasourceBenchmarkItem extends BenchmarkItem {

  private int nbEntities;

  @Override
  public String getUid() {
    StringBuilder sb = new StringBuilder();
    if(!Strings.isNullOrEmpty(getFlavor())) {
      sb.append(getFlavor()).append("-");
    }
    return sb.append(getNbEntities()).toString();
  }

  public int getNbEntities() {
    return nbEntities;
  }

  public void setNbEntities(int nbEntities) {
    this.nbEntities = nbEntities;
  }

  public GeneratedDatasourceBenchmarkItem withNbEntities(int nbEntities) {
    this.nbEntities = nbEntities;
    return this;
  }

}
