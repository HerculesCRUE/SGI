export interface SgiProblem {
  readonly title: string;
  readonly detail: string;
  managed: boolean;
  readonly level: Level;
  [name: string]: any;
}

export type Level = 'error' | 'warning' | 'info';

export class SgiError extends Error implements SgiProblem {
  readonly title: string;
  readonly detail: string;
  managed = false;
  readonly level: Level;

  constructor(title: string, detail: string = '', level: Level = 'error') {
    super(detail);
    this.name = title;
    this.level = level;

    Object.setPrototypeOf(this, new.target.prototype);

    this.title = title;
    this.detail = detail;
  }
}

export function toSgiProblem(error: Error, level: Level = 'error'): SgiProblem {
  if (error instanceof SgiError) {
    return error;
  }
  return new SgiError(error.name, error.message, level);
}
