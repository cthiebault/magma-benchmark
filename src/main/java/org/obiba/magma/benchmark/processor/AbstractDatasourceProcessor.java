package org.obiba.magma.benchmark.processor;

import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.NoSuchDatasourceException;
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

    long importStart = System.currentTimeMillis();
    ValueTable srcTable = new GeneratedValueTable(datasource, variableRepository.getVariables(), item.getNbEntities());
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(srcTable, "Table1", datasource);
    long importEnd = System.currentTimeMillis();

    BenchmarkResult result = new BenchmarkResult();
    result.withImportDuration(importEnd - importStart).withNbVariables(variableRepository.getNbVariables())
        .withDatasource(item.getDatasource()).withFlavor(item.getFlavor()).withNbEntities(item.getNbEntities());

    //TODO retrieve vector
//    ValueTable table = datasource.getValueTable("Table1");
//    Variable variable = Iterables.getFirst(table.getVariables(), null);
//    if(variable == null) throw new IllegalStateException("variable should not be null");
//
//    VariableEntity entity = Iterables.getFirst(table.getVariableEntities(), null);
//    if(entity == null) throw new IllegalStateException("entity should not be null");
//
//    ValueSet valueSet = table.getValueSet(entity);
//    VariableValueSource valueSource = table.getVariableValueSource(variable.getName());
//    VectorSource vectorSource = valueSource.asVectorSource();
//    SortedSet<VariableEntity> set = Sets.newTreeSet();
//    set.add(entity);
//    Iterable<Value> values = vectorSource.getValues(set);

    deleteDatasource(datasource.getName(), result);

    return result;
  }

  private void deleteDatasource(String name, BenchmarkResult result) throws NoSuchDatasourceException {
    long deleteStart = System.currentTimeMillis();
    Datasource datasource = MagmaEngine.get().getDatasource(name);
    MagmaEngine.get().removeDatasource(datasource);
    long deleteEnd = System.currentTimeMillis();
    result.withDeleteDuration(deleteEnd - deleteStart);
  }
}
