import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { SolicitudProyectoSocioService } from '@core/services/csp/solicitud-proyecto-socio.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SOLICITUD_DATA_KEY } from '../solicitud/solicitud-data.resolver';
import { ISolicitudData } from '../solicitud/solicitud.action.service';
import { SOLICITUD_PROYECTO_SOCIO_ROUTE_PARAMS } from './solicitud-proyecto-socio-route-params';
import { ISolicitudProyectoSocioData } from './solicitud-proyecto-socio.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const SOLICITUD_PROYECTO_SOCIO_DATA_KEY = 'solicitudProyectoSocioData';

@Injectable()
export class SolicitudProyectoSocioDataResolver extends SgiResolverResolver<ISolicitudProyectoSocioData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: SolicitudProyectoSocioService,
    private solicitudService: SolicitudService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<ISolicitudProyectoSocioData> {
    const solicitudData: ISolicitudData = route.parent.data[SOLICITUD_DATA_KEY];
    const solicitudProyectoSocioId = Number(route.paramMap.get(SOLICITUD_PROYECTO_SOCIO_ROUTE_PARAMS.ID));
    if (solicitudProyectoSocioId) {
      return this.service.exists(solicitudProyectoSocioId).pipe(
        map(exists => {
          if (!exists) {
            return throwError('NOT_FOUND');
          }
        }),
        switchMap(() => {
          return this.loadSolicitudProyectoSocioData(solicitudData);
        })
      );
    }
    return this.loadSolicitudProyectoSocioData(solicitudData);
  }

  private loadSolicitudProyectoSocioData(solicitudData: ISolicitudData): Observable<ISolicitudProyectoSocioData> {
    return this.solicitudService.findAllSolicitudProyectoSocio(solicitudData.solicitud.id).pipe(
      map(socios => {
        return {
          readonly: solicitudData.readonly,
          solicitudProyecto: solicitudData.solicitudProyecto,
          solicitudProyectoSocios: socios.items
        };
      })
    );
  }

}
