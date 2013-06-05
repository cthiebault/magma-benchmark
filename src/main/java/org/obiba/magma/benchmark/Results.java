package org.obiba.magma.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class Results {

  private static final Multimap<String, Stat> stats = ArrayListMultimap.create();

  private Results() {}

  public static void addStat(String datasource, int nbVariables, int nbEntities, long start, long end) {
    stats.put(datasource, new Stat(datasource, nbVariables, nbEntities, end - start,
        PeriodFormat.getDefault().print(new Period(start, end))));
  }

  public static void dump() throws IOException {
    Set<String> datasources = stats.keySet();
    long[][] data = new long[datasources.size()][];
    int i = 0;
    for(String datasource : datasources) {
      int j = 0;
      Collection<Stat> collection = stats.get(datasource);
      data[i] = new long[collection.size()];
      for(Stat execTime : collection) {
        data[i][j++] = execTime.getExecutionTimeMillis();
      }
      i++;
    }
    String json = "var data = " + new Gson().toJson(data) + ";\n";
    Files.write(json, new File("results-data.js"), Charsets.UTF_8);
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
