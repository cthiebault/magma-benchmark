package org.obiba.magma.benchmark.importer;

import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.benchmark.BenchmarkResult;
import org.obiba.magma.benchmark.VariableRepository;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.support.DatasourceCopier;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDatasourceProcessor implements ItemProcessor<BenchmarkItem, BenchmarkResult> {

  @Autowired
  private VariableRepository variableRepository;

  protected abstract Datasource createDatasource(BenchmarkItem item) throws Exception;

  @Override
  public BenchmarkResult process(BenchmarkItem item) throws Exception {

    Datasource datasource = createDatasource(item);

    long start = System.currentTimeMillis();
    ValueTable valueTable = new GeneratedValueTable(datasource, variableRepository.getVariables(),
        item.getNbEntities());
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(valueTable, "Table1", datasource);
    long end = System.currentTimeMillis();

    BenchmarkResult result = new BenchmarkResult();
    result.withStart(start).withEnd(end).withNbVariables(variableRepository.getNbVariables())
        .withDatasource(item.getDatasource()).withFlavor(item.getFlavor()).withNbEntities(item.getNbEntities());

    return result;
  }
}
