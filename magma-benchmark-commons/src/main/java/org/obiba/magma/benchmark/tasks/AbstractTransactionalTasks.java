package org.obiba.magma.benchmark.tasks;

import java.io.File;
import java.io.IOException;
import java.util.SortedSet;
import java.util.concurrent.ThreadFactory;

import javax.annotation.Nonnull;

import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTable;
import org.obiba.magma.Variable;
import org.obiba.magma.VariableEntity;
import org.obiba.magma.VariableValueSource;
import org.obiba.magma.VectorSource;
import org.obiba.magma.benchmark.BenchmarkItem;
import org.obiba.magma.benchmark.generated.VariableRepository;
import org.obiba.magma.datasource.fs.support.FsDatasourceFactory;
import org.obiba.magma.datasource.generated.GeneratedValueTable;
import org.obiba.magma.support.DatasourceCopier;
import org.obiba.magma.support.MultithreadedDatasourceCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

@Component
@Transactional
public abstract class AbstractTransactionalTasks {

  private static final Logger log = LoggerFactory.getLogger(AbstractTransactionalTasks.class);

  @Autowired
  private VariableRepository variableRepository;

  protected AbstractTransactionalTasks() {
  }

  public abstract Datasource createDatasource(BenchmarkItem item) throws Exception;

  public void importGeneratedData(int nbEntities, Datasource destination) throws IOException {
    ValueTable srcTable = new GeneratedValueTable(destination, variableRepository.getVariables(), nbEntities);
    MagmaEngine.get().addDatasource(destination);
    DatasourceCopier.Builder.newCopier().build().copy(srcTable, "Table1", destination);
  }

  public void importFsDatasource(File srcFile, Datasource destination) throws IOException {

    FsDatasourceFactory factory = new FsDatasourceFactory();
    factory.setName("transient-" + srcFile.getName());
    factory.setFile(srcFile);
    String uid = MagmaEngine.get().addTransientDatasource(factory);
    Datasource fsDatasource = MagmaEngine.get().getTransientDatasourceInstance(uid);
    MagmaEngine.get().addDatasource(destination);
//    DatasourceCopier.Builder.newCopier().build().copy(fsDatasource, destination);

    for(ValueTable valueTable : fsDatasource.getValueTables()) {
      MultithreadedDatasourceCopier.Builder.newCopier() //
          .withThreads(new ThreadFactory() {
            @Nonnull
            @Override
            public Thread newThread(@Nonnull final Runnable runnable) {
              return new Thread("thread-" + System.currentTimeMillis()) {
                @Override
                public void run() {
                  log.debug("Run {} from {}", runnable, getName());
                  runnable.run();
                }
              };
            }
          }) //
          .withCopier(DatasourceCopier.Builder.newCopier()/*.withLoggingListener()*/.withThroughtputListener()) //
          .from(valueTable) //
          .to(destination).build() //
          .copy();
    }
  }

  public void readVector(Datasource datasource) {
    for(ValueTable table : datasource.getValueTables()) {

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
  }

  public void deleteDatasource(Datasource datasource) throws Exception {
//    if(datasource.canDrop()) datasource.drop();
//    MagmaEngine.get().removeDatasource(datasource);
  }
}
