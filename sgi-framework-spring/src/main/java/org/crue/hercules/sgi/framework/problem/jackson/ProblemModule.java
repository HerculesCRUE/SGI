package org.crue.hercules.sgi.framework.problem.jackson;

import java.util.Collections;

import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

import org.crue.hercules.sgi.framework.problem.Problem;

/**
 * Jackson {@link com.fasterxml.jackson.databind.Module} for {@link Problem}
 * serialization.
 */
public class ProblemModule extends SimpleModule {
  /**
   * Creates a new Jackson {@link com.fasterxml.jackson.databind.Module} for
   * {@link Problem} serialization.
   */
  public ProblemModule() {
    super(ProblemModule.class.getSimpleName());
    setSerializers(new SimpleSerializers(Collections.singletonList(new ProblemSerializer())));
    setDeserializers(new SimpleDeserializers(Collections.singletonMap(Problem.class, new ProblemDeserializer())));
  }
}
