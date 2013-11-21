package org.obiba.magma.benchmark.writer;

import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;

public class MultipleItemWriter<T> extends AbstractItemStreamItemWriter<T> {

  private List<AbstractItemStreamItemWriter<T>> writers;

  @Override
  public void write(List<? extends T> ts) throws Exception {
    for(AbstractItemStreamItemWriter<T> writer : writers) {
      writer.write(ts);
    }
  }

  @Override
  public void close() {
    for(AbstractItemStreamItemWriter<T> writer : writers) {
      writer.close();
    }
  }

  @Override
  public void open(ExecutionContext executionContext) throws ItemStreamException {
    for(AbstractItemStreamItemWriter<T> writer : writers) {
      writer.open(executionContext);
    }
  }

  @Override
  public void update(ExecutionContext executionContext) {
    for(AbstractItemStreamItemWriter<T> writer : writers) {
      writer.update(executionContext);
    }
  }

  public void setWriters(List<AbstractItemStreamItemWriter<T>> writers) {
    this.writers = writers;
  }
}
