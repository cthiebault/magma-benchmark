package org.obiba.magma.benchmark.writer;

import org.obiba.magma.benchmark.BenchmarkResult;
import org.springframework.batch.item.file.transform.LineAggregator;

public class CsvLineAggregator implements LineAggregator<BenchmarkResult> {

  @Override
  public String aggregate(BenchmarkResult result) {
    return result.toCsv();
  }

}
