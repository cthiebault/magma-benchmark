package org.obiba.magma.benchmark;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

public class Results {

  private static final Collection<Stat> stats = Lists.newArrayList();

  private Results() {}

  public static void addStat(String datasource, int nbVariables, int nbEntities, long start, long end) {
    stats.add(new Stat(datasource, nbVariables, nbEntities, end - start,
        PeriodFormat.getDefault().print(new Period(start, end))));
  }

  public static void dump() throws IOException {
    Writer writer = new FileWriter("results.json");
    new Gson().toJson(stats, writer);
    writer.close();
  }

  @SuppressWarnings("UnusedDeclaration")
  public static class Stat {

    private final String datasource;

    private final int nbVariables;

    private final int nbEntities;

    private final long executionTimeMillis;

    private final String executionTime;

    Stat(String datasource, int nbVariables, int nbEntities, long executionTimeMillis, String executionTime) {
      this.datasource = datasource;
      this.nbVariables = nbVariables;
      this.nbEntities = nbEntities;
      this.executionTimeMillis = executionTimeMillis;
      this.executionTime = executionTime;
    }

    public String getDatasource() {
      return datasource;
    }

    public int getNbVariables() {
      return nbVariables;
    }

    public int getNbEntities() {
      return nbEntities;
    }

    public long getExecutionTimeMillis() {
      return executionTimeMillis;
    }

    public String getExecutionTime() {
      return executionTime;
    }
  }

}
