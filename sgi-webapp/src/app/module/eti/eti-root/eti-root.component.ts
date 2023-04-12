import { Component } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { Observable } from 'rxjs';
import { ETI_ROUTE_NAMES } from '../eti-route-names';

@Component({
  selector: 'sgi-eti-root',
  templateUrl: './eti-root.component.html',
  styleUrls: ['./eti-root.component.scss']
})
export class EtiRootComponent {

  get ETI_ROUTE_NAMES() {
    return ETI_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  // tslint:disable-next-line: variable-name
  _isEvaluador$: Observable<boolean>;
  get isEvaluador$(): Observable<boolean> {
    return this._isEvaluador$;
  }

  constructor(
    private evaluadorService: EvaluadorService
  ) {
    this._isEvaluador$ = this.evaluadorService.isEvaluador();
  }

}
