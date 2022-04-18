import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConvocatoriaBaremacionService } from '@core/services/prc/convocatoria-baremacion/convocatoria-baremacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { CONVOCATORIA_BAREMACION_ROUTE_PARAMS } from './convocatoria-baremacion-params';
import { IConvocatoriaBaremacionData } from './convocatoria-baremacion.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const CONVOCATORIA_BAREMACION_DATA_KEY = 'convocatoriaBaremacionData';

export function isConvocatoriaBaremacionEditable(convocatoriaBaremacion: IConvocatoriaBaremacion): boolean {
  return convocatoriaBaremacion.activo && convocatoriaBaremacion.fechaInicioEjecucion === null;
}

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaBaremacionResolver extends SgiResolverResolver<IConvocatoriaBaremacionData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ConvocatoriaBaremacionService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IConvocatoriaBaremacionData> {

    const convocatoriaBaremacionId = Number(route.paramMap.get(CONVOCATORIA_BAREMACION_ROUTE_PARAMS.ID));

    return this.service.findById(convocatoriaBaremacionId).pipe(
      map(convocatoriaBaremacion => {
        if (!convocatoriaBaremacion) {
          throwError('NOT_FOUND');
        }
        return {
          canEdit: this.authService.hasAuthority('PRC-CON-E') && isConvocatoriaBaremacionEditable(convocatoriaBaremacion),
          convocatoriaBaremacion
        };
      }));
  }
}
