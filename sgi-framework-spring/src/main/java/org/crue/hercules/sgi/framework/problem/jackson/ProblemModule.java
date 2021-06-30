package org.crue.hercules.sgi.framework.problem.jackson;

import java.util.Collections;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

import org.crue.hercules.sgi.framework.problem.Problem;

public class ProblemModule extends SimpleModule {
  public ProblemModule() {
    super(ProblemModule.class.getSimpleName());
    setSerializers(new SimpleSerializers(Collections.singletonList(new ProblemSerializer())));
    setDeserializers(new SimpleDeserializers(Collections.singletonMap(Problem.class, new ProblemDeserializer())));
  }
}
