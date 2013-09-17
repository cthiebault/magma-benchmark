package org.obiba.magma.benchmark.writer;

import java.io.IOException;
import java.io.Writer;

import org.obiba.magma.benchmark.BenchmarkResult;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class CsvHeaderCallback implements FlatFileHeaderCallback {

  @Override
  public void writeHeader(Writer writer) throws IOException {
    writer.write(BenchmarkResult.FIELDS_HEADER);
  }

}
