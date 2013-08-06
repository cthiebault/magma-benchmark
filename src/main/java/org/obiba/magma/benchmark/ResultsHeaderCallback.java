package org.obiba.magma.benchmark;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class ResultsHeaderCallback implements FlatFileHeaderCallback {

  @Override
  public void writeHeader(Writer writer) throws IOException {
    writer.write("datasource, flavor, nbEntities, nbVariables, start, end, duration");
  }

}
