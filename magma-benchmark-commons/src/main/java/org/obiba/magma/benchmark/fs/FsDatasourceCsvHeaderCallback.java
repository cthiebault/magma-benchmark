package org.obiba.magma.benchmark.fs;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class FsDatasourceCsvHeaderCallback implements FlatFileHeaderCallback {

  @Override
  public void writeHeader(Writer writer) throws IOException {
    writer.write(FsDatasourceBenchmarkResult.FIELDS_HEADER);
  }

}
