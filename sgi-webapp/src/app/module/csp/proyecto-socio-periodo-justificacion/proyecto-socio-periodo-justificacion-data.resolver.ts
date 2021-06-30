import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyecto } from '@core/models/csp/proyecto';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoSocioPeriodoJustificacionService } from '@core/services/csp/proyecto-socio-periodo-justificacion.service';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PROYECTO_SOCIO_ROUTE_PARAMS } from '../proyecto-socio/proyecto-socio-route-params';
import { PROYECTO_DATA_KEY } from '../proyecto/proyecto-data.resolver';
import { IProyectoData } from '../proyecto/proyecto.action.service';
import { PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_PARAMS } from './proyecto-socio-periodo-justificacion-route-params';
import { IProyectoSocioPeriodoJustificacionData } from './proyecto-socio-periodo-justificacion.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const PROYECTO_SOCIO_PERIODO_JUSTIFICACION_DATA_KEY = 'proyectoSocioPeriodoJustificacionData';

@Injectable()
export class ProyectoSocioPeriodoJustificacionDataResolver extends SgiResolverResolver<IProyectoSocioPeriodoJustificacionData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoSocioPeriodoJustificacionService,
    private proyectoSocioService: ProyectoSocioService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoSocioPeriodoJustificacionData> {
    const proyectoData: IProyectoData = route.parent.parent.data[PROYECTO_DATA_KEY];
    const proyectoSocioId = Number(route.paramMap.get(PROYECTO_SOCIO_ROUTE_PARAMS.ID));
    const proyectoSocioPeriodoJustificacionId = Number(route.paramMap.get(PROYECTO_SOCIO_PERIODO_JUSTIFICACION_ROUTE_PARAMS.ID));
    if (proyectoSocioPeriodoJustificacionId) {
      return this.service.exists(proyectoSocioPeriodoJustificacionId).pipe(
        switchMap((exists) => {
          if (!exists) {
            return throwError('NOT_FOUNTD');
          }
          return this.loadProyectoSocioPeriodoJustificacionData(
            proyectoData.proyecto, proyectoSocioId, proyectoSocioPeriodoJustificacionId
          );
        })
      );
    }
    return this.loadProyectoSocioPeriodoJustificacionData(proyectoData.proyecto, proyectoSocioId, proyectoSocioPeriodoJustificacionId);
  }

  private loadProyectoSocioPeriodoJustificacionData(
    proyecto: IProyecto,
    proyectoSocioId: number,
    proyectoSocioPeriodoJustificacionId: number
  ): Observable<IProyectoSocioPeriodoJustificacionData> {
    return this.proyectoSocioService.findById(proyectoSocioId).pipe(
      map(socio => {
        return {
          proyecto,
          proyectoSocio: socio,
          proyectoSocioPeriodosJustificacion: []
        };
      }),
      switchMap(data => {
        const options: SgiRestFindOptions = {
          sort: new RSQLSgiRestSort('numPeriodo', SgiRestSortDirection.ASC)
        };
        return this.proyectoSocioService.findAllProyectoSocioPeriodoJustificacion(proyectoSocioId, options).pipe(
          map(periodosJustificacion => {
            data.proyectoSocioPeriodosJustificacion = periodosJustificacion.items
              .filter(periodo => periodo.id !== proyectoSocioPeriodoJustificacionId);
            return data;
          })
        );
      })
    );
  }
}
