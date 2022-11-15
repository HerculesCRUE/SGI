import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { SolicitudPublicService } from '@core/services/csp/solicitud-public.service';
import { SolicitudRrhhService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SOLICITUD_PUBLIC_ROUTE_PARAMS } from './solicitud-public-route-params';
import { ISolicitudPublicData } from './solicitud-public.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const SOLICITUD_PUBLIC_DATA_KEY = 'solicitudData';

@Injectable()
export class SolicitudPublicDataResolver extends SgiResolverResolver<ISolicitudPublicData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: SolicitudPublicService,
    private solicitudRrhhService: SolicitudRrhhService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<ISolicitudPublicData> {

    const solicitudPublicId = route.paramMap.get(SOLICITUD_PUBLIC_ROUTE_PARAMS.ID);

    return this.service.findById(solicitudPublicId).pipe(
      switchMap(solicitud => {
        if (!solicitud) {
          return throwError('NOT_FOUND');
        }

        return of({
          readonly: false,
          estadoAndDocumentosReadonly: false,
          solicitud,
          publicKey: solicitudPublicId,
          hasSolicitudProyecto: false,
          hasPopulatedPeriodosSocios: false,
          solicitudProyecto: null,
          hasAnySolicitudProyectoSocioWithRolCoordinador: false,
          proyectosIds: [],
          modificableEstadoAsTutor: false,
          isTutor: false,
          isInvestigador: true
        });
      }),
      switchMap(data => {
        return this.service.modificable(solicitudPublicId).pipe(
          map(value => {
            data.readonly = !value;
            return data;
          })
        );
      }),
      switchMap(data => {
        return this.service.modificableEstadoAndDocumentosByInvestigador(solicitudPublicId).pipe(
          map(value => {
            data.estadoAndDocumentosReadonly = !value;
            return data;
          })
        );
      })
    );
  }

}
