import { Directive } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { HttpProblemType, SgiHttpError, UncaughtHttpError } from '@core/errors/http-problem';
import { SgiProblem } from '@core/errors/sgi-error';
import { Service } from '@core/service';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';

const SERVICE_GENERIC_ERROR_MAP: Map<Service, string> = new Map([
  [Service.CNF, marker(`error.generic.message.CNF`)],
  [Service.COM, marker(`error.generic.message.COM`)],
  [Service.CSP, marker(`error.generic.message.CSP`)],
  [Service.EER, marker(`error.generic.message.EER`)],
  [Service.ETI, marker(`error.generic.message.ETI`)],
  [Service.PII, marker(`error.generic.message.PII`)],
  [Service.PRC, marker(`error.generic.message.PRC`)],
  [Service.REL, marker(`error.generic.message.REL`)],
  [Service.REP, marker(`error.generic.message.REP`)],
  [Service.SGDOC, marker(`error.generic.message.SGDOC`)],
  [Service.SGE, marker(`error.generic.message.SGE`)],
  [Service.SGEMP, marker(`error.generic.message.SGEMP`)],
  [Service.SGEPII, marker(`error.generic.message.SGEPII`)],
  [Service.SGO, marker(`error.generic.message.SGO`)],
  [Service.SGP, marker(`error.generic.message.SGP`)],
  [Service.TP, marker(`error.generic.message.TP`)],
  [Service.USR, marker(`error.generic.message.USR`)],
]);

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class ProblemComponent {

  constructor(
    readonly translate: TranslateService
  ) { }

  errorMsg(problem: SgiProblem): Observable<string> {
    if (problem instanceof SgiHttpError) {
      switch (problem.type) {
        case HttpProblemType.UNCAUGHT:
          return this.translate.get(SERVICE_GENERIC_ERROR_MAP.get((problem as UncaughtHttpError).service));
        case HttpProblemType.ACCESS_DENIED:
        case HttpProblemType.AUTHENTICACION:
        case HttpProblemType.BAD_REQUEST:
        case HttpProblemType.ILLEGAL_ARGUMENT:
        case HttpProblemType.METHOD_NOT_ALLOWED:
        case HttpProblemType.MISSING_MAIN_RESEARCHER:
        case HttpProblemType.MISSING_PATH_VARIABLE:
        case HttpProblemType.NOT_ACCEPTABLE:
        case HttpProblemType.NOT_FOUND:
        case HttpProblemType.PERCENTAGE_IVA_ZERO:
        case HttpProblemType.TEAM_MEMBER_OVERLAP:
        case HttpProblemType.TOO_MANY_RESULTS:
        case HttpProblemType.TYPE_MISMATCH:
        case HttpProblemType.UNKNOWN:
        case HttpProblemType.VALIDATION:
        default:
          return of(problem.title + (problem.detail ? (': ' + problem.detail) : ''));
      }
    }

    return of(problem.title + (problem.detail ? (': ' + problem.detail) : ''));
  }


}

