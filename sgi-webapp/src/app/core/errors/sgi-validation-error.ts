import { SgiError, ValidationError } from './sgi-error';

export class SgiValidationError extends SgiError {
  readonly errors: ValidationError[];

  constructor(title: string, errors: ValidationError[]) {
    super(title);
    Object.setPrototypeOf(this, new.target.prototype);

    this.errors = errors ?? [];
  }
}
