package org.obiba.magma.benchmark.writer;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class JsonHeaderFooterCallback implements FlatFileHeaderCallback, FlatFileFooterCallback {

  @Override
  public void writeHeader(Writer writer) throws IOException {
    writer.write("var data = [");
  }

  @Override
  public void writeFooter(Writer writer) throws IOException {
    writer.write("];");
  }
}
