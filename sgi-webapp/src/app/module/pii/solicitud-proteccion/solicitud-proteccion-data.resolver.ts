import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SolicitudProteccionService } from '@core/services/pii/invencion/solicitud-proteccion/solicitud-proteccion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { IInvencionData } from '../invencion/invencion.action.service';
import { INVENCION_DATA_KEY } from '../invencion/invencion.resolver';
import { SOLICITUD_PROTECCION_ROUTE_PARAMS } from './solicitud-proteccion-route-params';
import { ISolicitudProteccionData } from './solicitud-proteccion.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const SOLICITUD_PROTECCION_DATA_KEY = 'solicitudProteccionData';

@Injectable()
export class SolicitudProteccionDataResolver extends SgiResolverResolver<ISolicitudProteccionData>{

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private invencionService: InvencionService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<ISolicitudProteccionData> {
    const invencionData: IInvencionData = route.parent.data[INVENCION_DATA_KEY];
    const solicitudProteccionId = Number(route.paramMap.get(SOLICITUD_PROTECCION_ROUTE_PARAMS.ID));

    if (solicitudProteccionId) {
      return this.invencionService.exists(solicitudProteccionId).pipe(
        switchMap((exists) => {
          if (!exists) {
            return throwError('NOT_FOUND');
          }
          return this.loadSolicitudProteccionData(invencionData);
        })
      );
    }
    return this.loadSolicitudProteccionData(invencionData);
  }

  private loadSolicitudProteccionData(invencionData: IInvencionData): Observable<ISolicitudProteccionData> {
    return this.invencionService.findAllSolicitudesProteccion(invencionData.invencion.id).pipe(
      map(solicitud => {
        return {
          invencion: invencionData.invencion,
          solicitudesProteccion: solicitud.items,
          readonly: !invencionData.canEdit
        };
      })
    );
  }
}
