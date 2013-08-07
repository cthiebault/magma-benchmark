package org.obiba.magma.benchmark;

import java.util.List;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

@SuppressWarnings("ParameterHidesMemberVariable")
public class BenchmarkResult extends BenchmarkItem {

  private static final List<String> FIELDS = Lists.newArrayList();

  static {
    FIELDS.add("datasource");
    FIELDS.add("flavor");
    FIELDS.add("nb entities");
    FIELDS.add("nb variables");
    FIELDS.add("import duration in ms");
    FIELDS.add("import duration");
    FIELDS.add("vector read duration in ms");
    FIELDS.add("vector read duration");
    FIELDS.add("delete duration in ms");
    FIELDS.add("delete duration");
  }

  public static final String FIELDS_HEADER = StringUtils.collectionToCommaDelimitedString(FIELDS);

  private long importDuration;

  private long vectorReadDuration;

  private long deleteDuration;

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
  }

  public long getVectorReadDuration() {
    return vectorReadDuration;
  }

  public void setVectorReadDuration(long vectorReadDuration) {
    this.vectorReadDuration = vectorReadDuration;
  }

  public long getDeleteDuration() {
    return deleteDuration;
  }

  public void setDeleteDuration(long deleteDuration) {
    this.deleteDuration = deleteDuration;
  }

  public BenchmarkResult withNbVariables(int nbVariables) {
    this.nbVariables = nbVariables;
    return this;
  }

  public BenchmarkResult withImportDuration(long importDuration) {
    this.importDuration = importDuration;
    return this;
  }

  public BenchmarkResult withVectorReadDuration(long vectorReadDuration) {
    this.vectorReadDuration = vectorReadDuration;
    return this;
  }

  public BenchmarkResult withDeleteDuration(long deleteDuration) {
    this.deleteDuration = deleteDuration;
    return this;
  }

  public String formatImportDuration() {
    return PeriodFormat.getDefault().print(new Period(importDuration));
  }

  public String formatVectorReadDuration() {
    return PeriodFormat.getDefault().print(new Period(vectorReadDuration));
  }

  public String formatDeleteDuration() {
    return PeriodFormat.getDefault().print(new Period(deleteDuration));
  }

  @Override
  public String toString() {
    List<Object> values = Lists.newArrayList();
    values.add(getDatasource());
    values.add(getFlavor());
    values.add(getNbEntities());
    values.add(getNbVariables());
    values.add(getImportDuration());
    values.add(formatImportDuration());
    values.add(formatVectorReadDuration());
    values.add(getVectorReadDuration());
    values.add(getDeleteDuration());
    values.add(formatDeleteDuration());
    return StringUtils.collectionToCommaDelimitedString(values);
  }

}
