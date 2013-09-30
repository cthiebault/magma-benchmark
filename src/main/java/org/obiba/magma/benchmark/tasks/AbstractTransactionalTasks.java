package org.obiba.magma.benchmark.tasks;

import java.io.IOException;
import java.util.SortedSet;

import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.Variable;
import org.obiba.magma.VariableEntity;
import org.obiba.magma.VariableValueSource;
import org.obiba.magma.VectorSource;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.benchmark.VariableRepository;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.support.DatasourceCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@Component
@Transactional
public abstract class AbstractTransactionalTasks {

  public static final String TABLE_NAME = "Table1";

  @Autowired
  private VariableRepository variableRepository;

  public abstract Datasource createDatasource(BenchmarkItem item) throws Exception;

  public void importData(int nbEntities, Datasource datasource) throws IOException {
    ValueTable srcTable = new GeneratedValueTable(datasource, variableRepository.getVariables(), nbEntities);
    MagmaEngine.get().addDatasource(datasource);
    DatasourceCopier.Builder.newCopier().build().copy(srcTable, TABLE_NAME, datasource);
  }

  public void readVector(Datasource datasource) {
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

  public void deleteDatasource(Datasource datasource) throws Exception {
    if(datasource.canDrop()) datasource.drop();
    MagmaEngine.get().removeDatasource(datasource);
  }
}
