package org.obiba.magma.benchmark;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

@SuppressWarnings("ParameterHidesMemberVariable")
public class BenchmarkResult extends BenchmarkItem {

  private long start;

  private long end;

  private int nbVariables;

  public long getStart() {
    return start;
  }

  public void setStart(long start) {
    this.start = start;
  }

  public long getEnd() {
    return end;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  public int getNbVariables() {
    return nbVariables;
  }

  public void setNbVariables(int nbVariables) {
    this.nbVariables = nbVariables;
  }

  public BenchmarkResult withStart(long start) {
    this.start = start;
    return this;
  }

  public BenchmarkResult withEnd(long end) {
    this.end = end;
    return this;
  }

  public BenchmarkResult withNbVariables(int nbVariables) {
    this.nbVariables = nbVariables;
    return this;
  }

  public String getDuration() {
    return PeriodFormat.getDefault().print(new Period(getStart(), getEnd()));
  }

  @Override
  public String toString() {
    return getDatasource() + ", " + getFlavor() + ", " + getNbEntities() + ", " + getNbVariables() + ", " + getStart() +
        ", " + getEnd() + ", " + getDuration();
  }
}
