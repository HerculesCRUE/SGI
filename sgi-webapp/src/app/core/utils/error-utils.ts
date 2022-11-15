import { Level, SgiError, SgiProblem, ValidationError } from '@core/errors/sgi-error';
import { SgiValidationError } from '@core/errors/sgi-validation-error';

export class ErrorUtils {

  private constructor() { }

  public static toSgiProblem(error: Error, level: Level = 'error'): SgiProblem {
    if (error instanceof SgiError) {
      return error;
    }
    return new SgiError(error.name, error.message, level);
  }

  public static toValidationProblem(title: string, errors: ValidationError[]): SgiProblem {
    return new SgiValidationError(title, errors)
  }

}
