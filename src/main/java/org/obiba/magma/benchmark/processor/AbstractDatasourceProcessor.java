package org.obiba.magma.benchmark.processor;

import java.util.SortedSet;

import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.Variable;
import org.obiba.magma.VariableEntity;
import org.obiba.magma.VariableValueSource;
import org.obiba.magma.VectorSource;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.benchmark.BenchmarkResult;
import org.obiba.magma.benchmark.VariableRepository;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.support.DatasourceCopier;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public abstract class AbstractDatasourceProcessor implements ItemProcessor<BenchmarkItem, BenchmarkResult> {

  public static final String TABLE_NAME = "Table1";

  @Autowired
  private VariableRepository variableRepository;

  protected abstract Datasource createDatasource(BenchmarkItem item) throws Exception;

  @Override
  public BenchmarkResult process(BenchmarkItem item) throws Exception {

    Datasource datasource = createDatasource(item);

    long importStart = System.currentTimeMillis();
    ValueTable srcTable = new GeneratedValueTable(datasource, variableRepository.getVariables(), item.getNbEntities());
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(srcTable, TABLE_NAME, datasource);
    long importEnd = System.currentTimeMillis();

    BenchmarkResult result = new BenchmarkResult();
    result.withImportDuration(importEnd - importStart).withNbVariables(variableRepository.getNbVariables())
        .withDatasource(item.getDatasource()).withFlavor(item.getFlavor()).withNbEntities(item.getNbEntities());

    long readStart = System.currentTimeMillis();
    readVector(datasource);
    long readEnd = System.currentTimeMillis();
    result.withVectorReadDuration(readEnd - readStart);

    long deleteStart = System.currentTimeMillis();
    deleteDatasource(datasource.getName(), result);
    long deleteEnd = System.currentTimeMillis();
    result.withDeleteDuration(deleteEnd - deleteStart);

    return result;
  }

  static void readVector(Datasource datasource) {

    ValueTable table = datasource.getValueTable(TABLE_NAME);
    Variable variable = Iterables.getFirst(table.getVariables(), null);
    if(variable == null) throw new IllegalStateException("variable should not be null");

    VariableEntity entity = Iterables.getFirst(table.getVariableEntities(), null);
    if(entity == null) throw new IllegalStateException("entity should not be null");

    VariableValueSource valueSource = table.getVariableValueSource(variable.getName());
    VectorSource vectorSource = valueSource.asVectorSource();
    if(vectorSource == null) throw new IllegalStateException("vectorSource should not be null");

    SortedSet<VariableEntity> set = Sets.newTreeSet();
    set.add(entity);
    vectorSource.getValues(set);
  }

  protected void deleteDatasource(String name, BenchmarkResult result) throws Exception {
    Datasource datasource = MagmaEngine.get().getDatasource(name);
    if(datasource.canDrop()) datasource.drop();
    MagmaEngine.get().removeDatasource(datasource);
  }

}
