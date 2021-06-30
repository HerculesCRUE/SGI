import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IEvaluador } from '@core/models/eti/evaluador';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { EvaluadorService } from '@core/services/eti/evaluador.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('error.load');

@Injectable()
export class EvaluadorResolver extends SgiResolverResolver<IEvaluador> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: EvaluadorService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IEvaluador> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
