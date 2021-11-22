package org.crue.hercules.sgi.framework.problem.exception;

import org.assertj.core.api.Assertions;
import org.crue.hercules.sgi.framework.problem.Problem;
import org.junit.jupiter.api.Test;

class ProblemExceptionTest {
  @Test
  void testProblemException_problemConstructor() {
    Problem problem = Problem.builder().detail("My message").build();
    Assertions.assertThatThrownBy(() -> {
      throw new ProblemException(problem);
    }).isInstanceOf(ProblemException.class).hasMessage("My message").extracting("problem")
        .isEqualToComparingFieldByField(problem);
  }

  @Test
  void testProblemException_allFieldConstructor() {
    Problem problem = Problem.builder().detail("My message").build();
    RuntimeException cause = new RuntimeException();
    Assertions.assertThatThrownBy(() -> {
      throw new ProblemException(problem, cause);
    }).isInstanceOf(ProblemException.class).hasCauseInstanceOf(RuntimeException.class).hasMessage("My message")
        .extracting("problem").isEqualToComparingFieldByField(problem);
  }

}
