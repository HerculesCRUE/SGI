import { Directive } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiError, SgiProblem } from '@core/errors/sgi-error';
import { BehaviorSubject } from 'rxjs';

const MSG_GENERIC_ERROR_TITLE = marker('error.generic.title');
const MSG_GENERIC_ERROR_CONTENT = marker('error.generic.message');

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class AbstractMenuContentComponent {

  readonly problems$: BehaviorSubject<SgiProblem[]>;

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

  protected constructor() {
    this.problems$ = new BehaviorSubject<SgiProblem[]>([]);
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

}
