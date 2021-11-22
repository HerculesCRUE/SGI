import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IPeticionEvaluacion } from '@core/models/eti/peticion-evaluacion';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { PeticionEvaluacionService } from '@core/services/eti/peticion-evaluacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_NOT_FOUND = marker('error.load');

@Injectable()
export class PeticionEvaluacionResolver extends SgiResolverResolver<IPeticionEvaluacion> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: PeticionEvaluacionService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IPeticionEvaluacion> {
    const peticion = this.service.findById(Number(route.paramMap.get('id')));
    if (this.authService.hasAuthorityForAnyUO('ETI-PEV-INV-ER')) {
      return this.service.isResponsableOrCreador(Number(route.paramMap.get('id'))).pipe(
        switchMap(response => {
          if (response) {
            return peticion;
          } else {
            return throwError('NOT_FOUND');
          }
        }));
    } else {
      return peticion;
    }
  }
}
