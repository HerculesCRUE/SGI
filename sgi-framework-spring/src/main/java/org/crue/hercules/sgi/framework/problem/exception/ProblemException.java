package org.crue.hercules.sgi.framework.problem.exception;

import org.crue.hercules.sgi.framework.problem.Problem;

public class ProblemException extends RuntimeException {

  private final Problem problem;

  public ProblemException(Problem problem) {
    super(emptyIfNull(problem.getDetail()));
    this.problem = problem;
  }

  private static String emptyIfNull(String str) {
    return str != null ? str : "";
  }

  public ProblemException(Problem problem, Throwable cause) {
    super(emptyIfNull(problem.getDetail()), cause);
    this.problem = problem;
  }

  public Problem getProblem() {
    return problem;
  }
}
