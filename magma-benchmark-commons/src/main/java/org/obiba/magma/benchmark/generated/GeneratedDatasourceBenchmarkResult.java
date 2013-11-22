package org.obiba.magma.benchmark.generated;

import java.util.List;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.obiba.magma.benchmark.BenchmarkResult;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;

@SuppressWarnings("ParameterHidesMemberVariable")
public class GeneratedDatasourceBenchmarkResult extends GeneratedDatasourceBenchmarkItem implements BenchmarkResult {

  private static final List<String> FIELDS = Lists.newArrayList();

  static {
    FIELDS.add("datasource");
    FIELDS.add("flavor");
    FIELDS.add("nb entities");
    FIELDS.add("nb variables");
    FIELDS.add("src datasource");
    FIELDS.add("import duration in ms");
    FIELDS.add("import duration");
    FIELDS.add("vector read duration in ms");
    FIELDS.add("vector read duration");
    FIELDS.add("delete duration in ms");
    FIELDS.add("delete duration");
  }

  public static final String FIELDS_HEADER = StringUtils.collectionToCommaDelimitedString(FIELDS);

  private long importDuration;

  private String importDurationFormatted;

  private long vectorReadDuration;

  private String vectorReadDurationFormatted;

  private long deleteDuration;

  private String deleteDurationFormatted;

  private int nbVariables;

  public int getNbVariables() {
    return nbVariables;
  }

  public void setNbVariables(int nbVariables) {
    this.nbVariables = nbVariables;
  }

  public long getImportDuration() {
    return importDuration;
  }

  public void setImportDuration(long importDuration) {
    this.importDuration = importDuration;
    setImportDurationFormatted(PeriodFormat.getDefault().print(new Period(importDuration)));
  }

  public long getVectorReadDuration() {
    return vectorReadDuration;
  }

  public void setVectorReadDuration(long vectorReadDuration) {
    this.vectorReadDuration = vectorReadDuration;
    setVectorReadDurationFormatted(PeriodFormat.getDefault().print(new Period(vectorReadDuration)));
  }

  public long getDeleteDuration() {
    return deleteDuration;
  }

  public void setDeleteDuration(long deleteDuration) {
    this.deleteDuration = deleteDuration;
    setDeleteDurationFormatted(PeriodFormat.getDefault().print(new Period(deleteDuration)));
  }

  public String getImportDurationFormatted() {
    return importDurationFormatted;
  }

  public void setImportDurationFormatted(String importDurationFormatted) {
    this.importDurationFormatted = importDurationFormatted;
  }

  public String getDeleteDurationFormatted() {
    return deleteDurationFormatted;
  }

  public void setDeleteDurationFormatted(String deleteDurationFormatted) {
    this.deleteDurationFormatted = deleteDurationFormatted;
  }

  public String getVectorReadDurationFormatted() {
    return vectorReadDurationFormatted;
  }

  public void setVectorReadDurationFormatted(String vectorReadDurationFormatted) {
    this.vectorReadDurationFormatted = vectorReadDurationFormatted;
  }

  public GeneratedDatasourceBenchmarkResult withNbVariables(int nbVariables) {
    setNbVariables(nbVariables);
    return this;
  }

  public GeneratedDatasourceBenchmarkResult withImportDuration(long importDuration) {
    setImportDuration(importDuration);
    return this;
  }

  public GeneratedDatasourceBenchmarkResult withVectorReadDuration(long vectorReadDuration) {
    setVectorReadDuration(vectorReadDuration);
    return this;
  }

  public GeneratedDatasourceBenchmarkResult withDeleteDuration(long deleteDuration) {
    setDeleteDuration(deleteDuration);
    return this;
  }

  @Override
  public String toJson() {
    return new GsonBuilder().create().toJson(this);
  }

  @Override
  public String toCsv() {
    List<Object> values = Lists.newArrayList();
    values.add(getDatasource());
    values.add(getFlavor());
    values.add(getNbEntities());
    values.add(getNbVariables());
    values.add(getImportDuration());
    values.add(getImportDurationFormatted());
    values.add(getVectorReadDuration());
    values.add(getVectorReadDurationFormatted());
    values.add(getDeleteDuration());
    values.add(getDeleteDurationFormatted());
    return StringUtils.collectionToCommaDelimitedString(values);
  }

  @Override
  public String toString() {
    return toCsv();
  }

}
