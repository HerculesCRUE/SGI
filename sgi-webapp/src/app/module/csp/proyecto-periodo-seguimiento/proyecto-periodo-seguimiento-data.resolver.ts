import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoPeriodoSeguimientoService } from '@core/services/csp/proyecto-periodo-seguimiento.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PROYECTO_DATA_KEY } from '../proyecto/proyecto-data.resolver';
import { IProyectoData } from '../proyecto/proyecto.action.service';
import { PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_PARAMS } from './proyecto-periodo-seguimiento-route-params';
import { IProyectoPeriodoSeguimientoData } from './proyecto-periodo-seguimiento.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const PROYECTO_PERIODO_SEGUIMIENTO_DATA_KEY = 'proyectoPeriodoSeguimientoData';

@Injectable()
export class ProyectoPeriodoSeguimientoDataResolver extends SgiResolverResolver<IProyectoPeriodoSeguimientoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoPeriodoSeguimientoService,
    private proyectoService: ProyectoService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoPeriodoSeguimientoData> {
    const proyectoData: IProyectoData = route.parent.data[PROYECTO_DATA_KEY];
    const proyectoPeriodoSeguimientoId = Number(route.paramMap.get(PROYECTO_PERIODO_SEGUIMIENTO_ROUTE_PARAMS.ID));
    if (proyectoPeriodoSeguimientoId) {
      return this.service.findById(proyectoPeriodoSeguimientoId).pipe(
        switchMap((proyectoPeriodoSeguimiento) => {
          if (!proyectoPeriodoSeguimiento) {
            return throwError('NOT_FOUND');
          }
          return this.loadProyectoPeriodoSeguimientoData(proyectoData, proyectoPeriodoSeguimiento.id,
            proyectoPeriodoSeguimiento.convocatoriaPeriodoSeguimientoId);
        })
      );
    }
    return this.loadProyectoPeriodoSeguimientoData(proyectoData, proyectoPeriodoSeguimientoId, null);
  }

  private loadProyectoPeriodoSeguimientoData(
    proyectoData: IProyectoData,
    proyectoPeriodoSeguimientoId: number,
    convocatoriaPeriodoSeguimientoId: number
  ): Observable<IProyectoPeriodoSeguimientoData> {
    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('numPeriodo', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllProyectoPeriodoSeguimientoProyecto(proyectoData.proyecto.id, options).pipe(
      map(periodos => {
        return {
          proyecto: proyectoData.proyecto,
          proyectoPeriodosSeguimiento: periodos.items.filter(element => element.id !== proyectoPeriodoSeguimientoId),
          convocatoriaPeriodoSeguimientoId,
          readonly: proyectoData.readonly
        };
      })
    );
  }
}
