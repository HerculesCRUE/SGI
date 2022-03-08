import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { NotificacionProyectoExternoCvnService } from '@core/services/csp/notificacion-proyecto-externo-cvn/notificacion-proyecto-externo-cvn.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { NOTIFICACION_CVN_ROUTE_PARAMS } from './notificacion-cvn-route-params';

const MSG_NOT_FOUND = marker('error.load');

export const NOTIFICACION_DATA_KEY = 'notificacionData';

@Injectable()
export class NotificacionCvnDataResolver extends SgiResolverResolver<INotificacionProyectoExternoCVN> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: NotificacionProyectoExternoCvnService,
    private authService: SgiAuthService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<INotificacionProyectoExternoCVN> {
    const notificacionId = Number(route.paramMap.get(NOTIFICACION_CVN_ROUTE_PARAMS.ID));

    return this.service.findById(notificacionId).pipe(
      switchMap(value => {
        if (!value) {
          return throwError('NOT_FOUND');
        }
        return of(value);
      }),
    );
  }
}
