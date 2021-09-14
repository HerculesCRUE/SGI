import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {
  AccessDeniedHttpProblem,
  AuthenticationHttpProblem,
  BadRequestHttpProblem,
  HttpProblem,
  IllegalArgumentHttpProblem,
  MethodNotAllowedHttpProblem,
  MissingMainResearcherHttpProblem,
  MissingPathVariableHttpProblem,
  NotAcceptableHttpProblem,
  NotFoundHttpProblem,
  PercentageIvaZeroHttpProblem,
  ProblemType,
  TeamMemberOverlapHttpProblem,
  TypeMismatchHttpProblem,
  ValidationHttpProblem
} from './errors/http-problem';

@Injectable()
export class SgiErrorHttpInterceptor implements HttpInterceptor {

  constructor() { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (this.isProblem(error)) {
          return throwError(this.resolveHttpProblem(error));
        }
        return throwError(error);
      })
    );
  }

  private resolveHttpProblem(error: HttpErrorResponse): HttpProblem {
    const type: ProblemType = error.error.type;
    switch (type) {
      case ProblemType.ACCESS_DENIED:
        return new AccessDeniedHttpProblem(error.error);
      case ProblemType.AUTHENTICACION:
        return new AuthenticationHttpProblem(error.error);
      case ProblemType.BAD_REQUEST:
        return new BadRequestHttpProblem(error.error);
      case ProblemType.ILLEGAL_ARGUMENT:
        return new IllegalArgumentHttpProblem(error.error);
      case ProblemType.METHOD_NOT_ALLOWED:
        return new MethodNotAllowedHttpProblem(error.error);
      case ProblemType.MISSING_MAIN_RESEARCHER:
        return new MissingMainResearcherHttpProblem(error.error);
      case ProblemType.MISSING_PATH_VARIABLE:
        return new MissingPathVariableHttpProblem(error.error);
      case ProblemType.NOT_ACCEPTABLE:
        return new NotAcceptableHttpProblem(error.error);
      case ProblemType.NOT_FOUND:
        return new NotFoundHttpProblem(error.error);
      case ProblemType.PERCENTAGE_IVA_ZERO:
        return new PercentageIvaZeroHttpProblem(error.error);
      case ProblemType.TEAM_MEMBER_OVERLAP:
        return new TeamMemberOverlapHttpProblem(error.error);
      case ProblemType.TYPE_MISMATCH:
        return new TypeMismatchHttpProblem(error.error);
      case ProblemType.UNKNOWN:
        return new TypeMismatchHttpProblem(error.error);
      case ProblemType.VALIDATION:
        return new ValidationHttpProblem(error.error);
      default:
        return new HttpProblem(error.error);
    }
  }

  private isProblem(error: HttpErrorResponse): boolean {
    return error.headers.get('content-type') === 'application/problem+json';
  }
}
