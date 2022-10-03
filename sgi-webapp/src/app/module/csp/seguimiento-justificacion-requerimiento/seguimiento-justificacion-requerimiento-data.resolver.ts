import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IIncidenciaDocumentacionRequerimiento } from '@core/models/csp/incidencia-documentacion-requerimiento';
import { IRequerimientoJustificacion } from '@core/models/csp/requerimiento-justificacion';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoPeriodoJustificacionService } from '@core/services/csp/proyecto-periodo-justificacion/proyecto-periodo-justificacion.service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { RequerimientoJustificacionService } from '@core/services/csp/requerimiento-justificacion/requerimiento-justificacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS } from './seguimiento-justificacion-requerimiento-route-params';
import { IRequerimientoJustificacionData } from './seguimiento-justificacion-requerimiento.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const REQUERIMIENTO_JUSTIFICACION_DATA_KEY = 'requerimientoJustificacionData';

@Injectable()
export class SeguimientoJustificacionRequerimientoDataResolver extends SgiResolverResolver<IRequerimientoJustificacionData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: RequerimientoJustificacionService,
    private authService: SgiAuthService,
    private readonly proyectoProyectoSgeService: ProyectoProyectoSgeService,
    private readonly proyectoPeriodoJustificacionService: ProyectoPeriodoJustificacionService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IRequerimientoJustificacionData> {
    const requerimientoJustificacionId = Number(route.paramMap.get(SEGUIMIENTO_JUSTIFICACION_REQUERIMIENTO_ROUTE_PARAMS.ID));

    return forkJoin({
      requerimientoJustificacion: this.findRequerimientoJustificacion(requerimientoJustificacionId),
      incidenciasDocumentacion: this.findIncidenciasDocumentacion(requerimientoJustificacionId)
    }).pipe(
      map(({ requerimientoJustificacion, incidenciasDocumentacion }) => {
        if (!requerimientoJustificacion) {
          throwError('NOT_FOUND');
        }
        return {
          canEdit: this.authService.hasAuthority('CSP-SJUS-E'),
          requerimientoJustificacion,
          incidenciasDocumentacion
        };
      })
    );
  }

  private findRequerimientoJustificacion(requerimientoJustificacionId: number): Observable<IRequerimientoJustificacion> {
    return this.service.findById(requerimientoJustificacionId).pipe(
      switchMap((requerimientoJustificacion) => {
        if (requerimientoJustificacion?.proyectoProyectoSge?.id) {
          return this.proyectoProyectoSgeService.findById(requerimientoJustificacion.proyectoProyectoSge.id)
            .pipe(
              map((proyectoProyectoSge) => {
                requerimientoJustificacion.proyectoProyectoSge = proyectoProyectoSge;
                return requerimientoJustificacion;
              })
            );
        } else {
          return of(requerimientoJustificacion);
        }
      }),
      switchMap((requerimientoJustificacion) => {
        if (requerimientoJustificacion?.proyectoPeriodoJustificacion?.id) {
          return this.proyectoPeriodoJustificacionService.findById(requerimientoJustificacion.proyectoPeriodoJustificacion.id)
            .pipe(
              map((proyectoPeriodoJustificacion) => {
                requerimientoJustificacion.proyectoPeriodoJustificacion = proyectoPeriodoJustificacion;
                return requerimientoJustificacion;
              })
            );
        } else {
          return of(requerimientoJustificacion);
        }
      })
    );
  }

  private findIncidenciasDocumentacion(requerimientoJustificacionId: number): Observable<IIncidenciaDocumentacionRequerimiento[]> {
    return this.service.findIncidenciasDocumentacion(requerimientoJustificacionId)
      .pipe(
        map(({ items }) => items)
      );
  }
}
