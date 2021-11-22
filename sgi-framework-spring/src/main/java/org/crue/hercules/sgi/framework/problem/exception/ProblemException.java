package org.crue.hercules.sgi.framework.problem.exception;

import org.crue.hercules.sgi.framework.problem.Problem;

/**
 * Custom {@link RuntimeException} wrapping a {@link Problem}.
 */
public class ProblemException extends RuntimeException {

  /**
   * The wrapped {@link Problem}.
   */
  private final Problem problem;

  /**
   * Creates a new {@link ProblemException} wrapping the provided {@link Problem}.
   * 
   * @param problem the {@link Problem}
   */
  public ProblemException(Problem problem) {
    super(emptyIfNull(problem.getDetail()));
    this.problem = problem;
  }

  /**
   * Creates a new {@link ProblemException} wrapping the provided {@link Problem}.
   * 
   * @param problem the {@link Problem}
   * @param cause   the original cause for this exception
   */
  public ProblemException(Problem problem, Throwable cause) {
    super(emptyIfNull(problem.getDetail()), cause);
    this.problem = problem;
  }

  /**
   * Gets the wrapped {@link Problem}
   * 
   * @return the {@link Problem}
   */
  public Problem getProblem() {
    return problem;
  }

  private static String emptyIfNull(String str) {
    return str != null ? str : "";
  }

}
