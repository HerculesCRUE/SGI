import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Service } from '@core/service';
import { from, Observable, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import {
  AccessDeniedHttpError,
  AuthenticationHttpError,
  BadRequestHttpError,
  HttpProblemType,
  IllegalArgumentHttpError,
  MethodNotAllowedHttpError,
  MissingMainResearcherHttpError,
  MissingPathVariableHttpError,
  NotAcceptableHttpError,
  NotFoundHttpError,
  PercentageIvaZeroHttpError,
  SgiHttpError,
  TeamMemberOverlapHttpError,
  TooManyResultsHttpError,
  TypeMismatchHttpError,
  UncaughtHttpError,
  UnknownHttpError,
  ValidationHttpError
} from './errors/http-problem';

@Injectable()
export class SgiErrorHttpInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        const service = Service.fromUrl(req.url);

        if (this.isProblem(error)) {
          if (error.error instanceof Blob) {
            return this.toJSONHttpErrorResponse(error).pipe(
              map(jsonError => this.resolveHttpProblem(jsonError, service)),
              switchMap(problem => throwError(problem))
            );
          }
          else {
            return throwError(this.resolveHttpProblem(error, service));
          }
        }
        return throwError(new UncaughtHttpError(error, service));
      })
    );
  }

  private resolveHttpProblem(error: HttpErrorResponse, service: Service): SgiHttpError {
    const type: HttpProblemType = error.error.type;
    switch (type) {
      case HttpProblemType.ACCESS_DENIED:
        return new AccessDeniedHttpError(error.error, service);
      case HttpProblemType.AUTHENTICACION:
        return new AuthenticationHttpError(error.error, service);
      case HttpProblemType.BAD_REQUEST:
        return new BadRequestHttpError(error.error, service);
      case HttpProblemType.ILLEGAL_ARGUMENT:
        return new IllegalArgumentHttpError(error.error, service);
      case HttpProblemType.METHOD_NOT_ALLOWED:
        return new MethodNotAllowedHttpError(error.error, service);
      case HttpProblemType.MISSING_MAIN_RESEARCHER:
        return new MissingMainResearcherHttpError(error.error, service);
      case HttpProblemType.MISSING_PATH_VARIABLE:
        return new MissingPathVariableHttpError(error.error, service);
      case HttpProblemType.NOT_ACCEPTABLE:
        return new NotAcceptableHttpError(error.error, service);
      case HttpProblemType.NOT_FOUND:
        return new NotFoundHttpError(error.error, service);
      case HttpProblemType.PERCENTAGE_IVA_ZERO:
        return new PercentageIvaZeroHttpError(error.error, service);
      case HttpProblemType.TEAM_MEMBER_OVERLAP:
        return new TeamMemberOverlapHttpError(error.error, service);
      case HttpProblemType.TOO_MANY_RESULTS:
        return new TooManyResultsHttpError(error.error, service);
      case HttpProblemType.TYPE_MISMATCH:
        return new TypeMismatchHttpError(error.error, service);
      case HttpProblemType.UNKNOWN:
        return new UnknownHttpError(error.error, service);
      case HttpProblemType.VALIDATION:
        return new ValidationHttpError(error.error, service);
      default:
        return new SgiHttpError(error.error, service);
    }
  }

  private isProblem(error: HttpErrorResponse): boolean {
    return error.headers.get('content-type') === 'application/problem+json';
  }

  private toJSONHttpErrorResponse(blobError: HttpErrorResponse): Observable<HttpErrorResponse> {
    const blob = blobError.error as Blob;
    return from(blob.text()).pipe(
      map(body => {
        return new HttpErrorResponse({
          error: JSON.parse(body),
          headers: blobError.headers,
          status: blobError.status,
          statusText: blobError.statusText,
          url: blobError.url
        });
      })
    );
  }
}
