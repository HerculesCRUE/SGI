import { HttpProblemType, SgiHttpProblem, ValidationHttpError } from '@core/errors/http-problem';
import { Level, SgiError, SgiProblem, ValidationError } from '@core/errors/sgi-error';

export class ErrorUtils {

  private constructor() { }

  public static toSgiProblem(error: Error, level: Level = 'error'): SgiProblem {
    if (error instanceof SgiError) {
      return error;
    }
    return new SgiError(error.name, error.message, level);
  }

  public static toValidationProblem(title: string, errors: ValidationError[]): SgiProblem {
    const problem: SgiHttpProblem = {
      title,
      detail: '',
      level: 'error',
      type: HttpProblemType.VALIDATION,
      status: 400,
      instance: '',
      managed: false,
      errors
    };
    return new ValidationHttpError(problem);
  }

}
