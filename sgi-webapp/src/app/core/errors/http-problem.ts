export enum ProblemType {
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
  TYPE_MISMATCH = 'urn:problem-type:type-mismatch',
  UNKNOWN = 'urn:problem-type:unknown',
  VALIDATION = 'urn:problem-type:validation'
}

export interface Problem {
  readonly type: string;
  readonly title: string;
  readonly status: number;
  readonly detail: string;
  readonly instance: string;
  managed: boolean;
  [name: string]: any;
}

export class HttpProblem extends Error implements Problem {
  readonly type: string;
  readonly title: string;
  readonly status: number;
  readonly detail: string;
  readonly instance: string;
  managed = false;

  constructor(responseBody: Problem) {
    super(responseBody.detail);
    this.name = responseBody.title;

    Object.setPrototypeOf(this, new.target.prototype);

    this.type = responseBody.type;
    this.title = responseBody.title;
    this.status = responseBody.status;
    this.detail = responseBody.detail;
    this.instance = responseBody.instance;
  }
}

export class AccessDeniedHttpProblem extends HttpProblem {
  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class AuthenticationHttpProblem extends HttpProblem {
  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class BadRequestHttpProblem extends HttpProblem {
  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class IllegalArgumentHttpProblem extends HttpProblem {
  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class MethodNotAllowedHttpProblem extends HttpProblem {
  readonly supported: string[];

  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.supported = responseBody.supported ?? [];
  }
}

export class MissingMainResearcherHttpProblem extends HttpProblem {
  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class MissingPathVariableHttpProblem extends HttpProblem {
  readonly variableName: string;

  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.variableName = responseBody.variableName;
  }
}

export class MissingRequestParameterHttpProblem extends HttpProblem {
  readonly parameterName: string;
  readonly parameterType: string;

  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.parameterName = responseBody.parameterName;
    this.parameterType = responseBody.parameterType;
  }
}

export class NotAcceptableHttpProblem extends HttpProblem {
  readonly supported: string[];

  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.supported = responseBody.supported ?? [];
  }
}

export class NotFoundHttpProblem extends HttpProblem {
  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class PercentageIvaZeroHttpProblem extends HttpProblem {
  readonly parameter: string;
  readonly parameterType: string;

  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.parameter = responseBody.name;
    this.parameterType = responseBody.type;
  }
}

export class TeamMemberOverlapHttpProblem extends HttpProblem {
  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class TypeMismatchHttpProblem extends HttpProblem {
  readonly propertyName: string;
  readonly propertyType: string;

  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.propertyName = responseBody.propertyName;
    this.propertyType = responseBody.propertyType;
  }
}

export class UnknownHttpProblem extends HttpProblem {
  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);
  }
}

export class ValidationHttpProblem extends HttpProblem {
  readonly errors: {
    field: string;
    error: string;
  }[];

  constructor(responseBody: Problem) {
    super(responseBody);
    Object.setPrototypeOf(this, new.target.prototype);

    this.errors = responseBody.errors ?? [];
  }
}
