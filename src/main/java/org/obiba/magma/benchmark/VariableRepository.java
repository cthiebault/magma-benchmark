package org.obiba.magma.benchmark;

import java.util.Collection;
import java.util.List;

import org.obiba.magma.ValueType;
import org.obiba.magma.Variable;
import org.obiba.magma.type.DecimalType;
import org.obiba.magma.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

@Component("variableRepository")
public class VariableRepository implements Tasklet {

  private static final Logger log = LoggerFactory.getLogger(MagmaBenchmark.class);

  public static final String PARTICIPANT = "Participant";

  private static final List<ValueType> types = Lists.newArrayList();

  static {
    types.add(DecimalType.get());
    types.add(IntegerType.get());
//    types.add(TextType.get());
//    types.add(LocaleType.get());
//    types.add(BooleanType.get());
//    types.add(BinaryType.get());
//    types.add(DateTimeType.get());
//    types.add(DateType.get());
  }

  private int nbVariables = -1;

  private Collection<Variable> variables;

  public void generateVariables(@SuppressWarnings("ParameterHidesMemberVariable") int nbVariables) {
    this.nbVariables = nbVariables;
    createVariables();
  }

  public Collection<Variable> getVariables() {
    if(variables == null) {
      throw new IllegalStateException("Cannot access variables yet. Need to generateVariables() first");
    }
    return variables;
  }

  public int getNbVariables() {
    return nbVariables;
  }

  private void createVariables() {
    variables = Lists.newArrayList();
    log.info("Create {} variables", nbVariables);
    for(int i = 0; i < nbVariables; i++) {
      Variable variable = createVariable("Variable " + i, getValueType(i), Math.random() < 0.5);
      variables.add(variable);
      log.debug("{}: {}, repeatable: {}", variable.getName(), variable.getValueType(), variable.isRepeatable());
    }
  }

  private ValueType getValueType(int i) {
    int nbTypes = types.size();
    return types.get(i % nbTypes);
  }

  private Variable createVariable(String name, ValueType valueType, boolean repeatable) {
    Variable.Builder builder = Variable.Builder.newVariable(name, valueType, PARTICIPANT);
    if(repeatable) builder.repeatable();
    return builder.build();
  }

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    generateVariables(Ints.checkedCast((Long) chunkContext.getStepContext().getJobParameters().get("nbVariables")));
    return RepeatStatus.FINISHED;
  }
}
