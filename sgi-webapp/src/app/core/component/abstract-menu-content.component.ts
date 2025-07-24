import { Directive, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiError, SgiProblem } from '@core/errors/sgi-error';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Subscription } from 'rxjs';

const MSG_GENERIC_ERROR_TITLE = marker('error.generic.title');
const MSG_GENERIC_ERROR_CONTENT = marker('error.generic.message');

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class AbstractMenuContentComponent implements OnInit, OnDestroy {

  readonly problems$: BehaviorSubject<SgiProblem[]>;
  suscripciones: Subscription[] = [];

  public readonly processError: (error: Error) => void = (error: Error) => {
    if (error instanceof SgiError) {
      if (!error.managed) {
        error.managed = true;
        this.pushProblems(error);
      }
    }
    else {
      // Error incontrolado
      const sgiError = new SgiError(MSG_GENERIC_ERROR_TITLE, MSG_GENERIC_ERROR_CONTENT);
      sgiError.managed = true;
      this.pushProblems(sgiError);
    }
  }

  protected constructor(translateService?: TranslateService) {
    this.problems$ = new BehaviorSubject<SgiProblem[]>([]);

    if (translateService) {
      this.suscripciones.push(translateService.onLangChange.subscribe(() => {
        this.setupI18N();
      }));
    }
  }

  pushProblems(problem: SgiProblem | SgiProblem[]): void {
    const current = this.problems$.value;

    if (Array.isArray(problem)) {
      const newProblems = problem.filter(p => !this.isDuplicatedProblem(p, current));
      this.problems$.next([...current, ...newProblems]);
    }
    else if (problem) {
      if (!this.isDuplicatedProblem(problem, current)) {
        this.problems$.next([...current, problem]);
      }
    }
  }

  clearProblems(): void {
    this.problems$.next([]);
  }

  private isDuplicatedProblem(problem: SgiProblem, problems: SgiProblem[]) {
    return problems.some(p =>
      p.title === problem.title
      && p.detail === problem.detail
      && p.service === problem.service
      && p.level === problem.level
    );
  }

  ngOnInit(): void {
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.suscripciones.forEach(x => x.unsubscribe());
  }

  protected abstract setupI18N(): void;
}
