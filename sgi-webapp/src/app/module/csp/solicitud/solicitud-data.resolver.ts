import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SOLICITUD_ROUTE_PARAMS } from './solicitud-route-params';
import { ISolicitudData } from './solicitud.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const SOLICITUD_DATA_KEY = 'solicitudData';

@Injectable()
export class SolicitudDataResolver extends SgiResolverResolver<ISolicitudData> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: SolicitudService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<ISolicitudData> {

    return this.service.findById(Number(route.paramMap.get(SOLICITUD_ROUTE_PARAMS.ID))).pipe(
      map(solicitud => {
        return {
          solicitud
        } as ISolicitudData;
      }),
      switchMap(data => {
        return this.service.existsSolictudProyecto(data.solicitud.id).pipe(
          map(exists => {
            data.hasSolicitudProyecto = exists;
            return data;
          })
        );
      }),
      switchMap(data => {
        return this.service.modificable(data.solicitud.id).pipe(
          map(value => {
            data.readonly = !value;
            return data;
          })
        );
      })
    );
  }
}
