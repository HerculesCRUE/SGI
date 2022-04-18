import { Level, SgiError, SgiProblem } from './sgi-error';

export interface SgiHttpProblem extends SgiProblem {
  readonly type: string;
  readonly status: number;
  readonly instance: string;
}

export enum HttpProblemType {
  ACCESS_DENIED = 'urn:problem-type:access-denied',
  AUTHENTICACION = 'urn:problem-type:authentication',
  BAD_REQUEST = 'urn:problem-type:bad-request',
  ILLEGAL_ARGUMENT = 'urn:problem-type:illegal-argument',
  METHOD_NOT_ALLOWED = 'urn:problem-type:method-not-allowed',
  MISSING_MAIN_RESEARCHER = 'urn:problem-type:missing-main-researcher',
  MISSING_PATH_VARIABLE = 'urn:problem-type:missing-path-variable',
  MISSING_REQUEST_PARAMETER = 'urn:problem-type:missing-request-parameter',
  NOT_ACCEPTABLE = 'urn:problem-type:not-acceptable',
  NOT_FOUND = 'urn:problem-type:not-found',
  PERCENTAGE_IVA_ZERO = 'urn:problem-type:percentage-iva-zero',
  TEAM_MEMBER_OVERLAP = 'urn:problem-type:team-member-overlap',
  TOO_MANY_RESULTS = 'urn:problem-type:too-many-results',
  TYPE_MISMATCH = 'urn:problem-type:type-mismatch',
  UNKNOWN = 'urn:problem-type:unknown',
  VALIDATION = 'urn:problem-type:validation'
}

export class SgiHttpError extends SgiError implements SgiHttpProblem {
  readonly type: string;
  readonly status: number;
  readonly instance: string;

  constructor(responseBody: SgiHttpProblem, level: Level = 'error') {
    super(responseBody.title, responseBody.detail, level);

    Object.setPrototypeOf(this, new.target.prototype);

    this.type = responseBody.type;
    this.status = responseBody.status;
    this.instance = responseBody.instance;
  }
}

export class AccessDeniedHttpError extends SgiHttpError {
  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class AuthenticationHttpError extends SgiHttpError {
  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class BadRequestHttpError extends SgiHttpError {
  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class IllegalArgumentHttpError extends SgiHttpError {
  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class MethodNotAllowedHttpError extends SgiHttpError {
  readonly supported: string[];

  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.supported = responseBody.supported ?? [];
  }
}

export class MissingMainResearcherHttpError extends SgiHttpError {
  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class MissingPathVariableHttpError extends SgiHttpError {
  readonly variableName: string;

  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.variableName = responseBody.variableName;
  }
}

export class MissingRequestParameterHttpError extends SgiHttpError {
  readonly parameterName: string;
  readonly parameterType: string;

  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.parameterName = responseBody.parameterName;
    this.parameterType = responseBody.parameterType;
  }
}

export class NotAcceptableHttpError extends SgiHttpError {
  readonly supported: string[];

  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.supported = responseBody.supported ?? [];
  }
}

export class NotFoundHttpError extends SgiHttpError {
  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class PercentageIvaZeroHttpError extends SgiHttpError {
  readonly parameter: string;
  readonly parameterType: string;

  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.parameter = responseBody.name;
    this.parameterType = responseBody.type;
  }
}

export class TeamMemberOverlapHttpError extends SgiHttpError {
  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class TooManyResultsHttpError extends SgiHttpError {
  constructor(responseBody: SgiHttpProblem) {
    super(responseBody, 'warning');
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class TypeMismatchHttpError extends SgiHttpError {
  readonly propertyName: string;
  readonly propertyType: string;

  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.propertyName = responseBody.propertyName;
    this.propertyType = responseBody.propertyType;
  }
}

export class UnknownHttpError extends SgiHttpError {
  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class ValidationHttpError extends SgiHttpError {
  readonly errors: {
    field: string;
    error: string;
  }[];

  constructor(responseBody: SgiHttpProblem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.errors = responseBody.errors ?? [];
  }
}
