import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { AUTORIZACION_ROUTE_PARAMS } from './autorizacion-route-params';
import { IAutorizacionData } from './autorizacion.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const AUTORIZACION_DATA_KEY = 'autorizacionData';

@Injectable()
export class AutorizacionDataResolver extends SgiResolverResolver<IAutorizacionData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: AutorizacionService,
    private authService: SgiAuthService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IAutorizacionData> {
    const autorizacionId = Number(route.paramMap.get(AUTORIZACION_ROUTE_PARAMS.ID));

    return this.service.findById(autorizacionId).pipe(
      switchMap(value => {
        if (!value) {
          return throwError('NOT_FOUND');
        }
        return this.service.presentable(autorizacionId).pipe(
          map(presentable => {
            return {
              presentable,
              isInvestigador: this.authService.hasAnyAuthority(['CSP-AUT-INV-C', 'CSP-AUT-INV-ER', 'CSP-AUT-INV-BR']),
              autorizacion: value,
            } as IAutorizacionData;
          })
        );
      }),
    );
  }


}
