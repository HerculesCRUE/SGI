export interface SgiProblem {
  readonly title: string;
  readonly detail: string;
  managed: boolean;
  readonly level: Level;
  [name: string]: any;
}

export type Level = 'error' | 'warning' | 'info';

export interface ValidationError {
  field: string;
  error: string;
}

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
