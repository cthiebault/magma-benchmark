package org.obiba.magma.benchmark.generated;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class GeneratedDatasourceCsvHeaderCallback implements FlatFileHeaderCallback {

  @Override
  public void writeHeader(Writer writer) throws IOException {
    writer.write(GeneratedDatasourceBenchmarkResult.FIELDS_HEADER);
  }

}
