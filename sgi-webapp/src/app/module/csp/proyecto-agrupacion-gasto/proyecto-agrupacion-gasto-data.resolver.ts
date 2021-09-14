import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoAgrupacionGastoService } from '@core/services/csp/proyecto-agrupacion-gasto/proyecto-agrupacion-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PROYECTO_DATA_KEY } from '../proyecto/proyecto-data.resolver';
import { IProyectoData } from '../proyecto/proyecto.action.service';
import { PROYECTO_AGRUPACION_GASTO_ROUTE_PARAMS } from './proyecto-agrupacion-gasto-route-params';
import { IProyectoAgrupacionGastoData } from './proyecto-agrupacion-gasto.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const PROYECTO_AGRUPACION_GASTO_DATA_KEY = 'proyectoAgrupacionGastoData';
export const AGRUPACION_GASTO_CONCEPTO_DATA_KEY = 'agrupacionGastoConceptoData';

@Injectable()
export class ProyectoAgrupacionGastoDataResolver extends SgiResolverResolver<IProyectoAgrupacionGastoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoAgrupacionGastoService,
    private proyectoService: ProyectoService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoAgrupacionGastoData> {
    const proyectoData: IProyectoData = route.parent.data[PROYECTO_DATA_KEY];
    const proyectoAgrupacionGastoId = Number(route.paramMap.get(PROYECTO_AGRUPACION_GASTO_ROUTE_PARAMS.ID));
    if (proyectoAgrupacionGastoId) {
      return this.service.exists(proyectoAgrupacionGastoId).pipe(
        switchMap((exists) => {
          if (!exists) {
            return throwError('NOT_FOUND');
          }
          return this.loadProyectoAgrupacionGastoData(proyectoData);
        })
      );
    }
    return this.loadProyectoAgrupacionGastoData(proyectoData);
  }

  private loadProyectoAgrupacionGastoData(proyectoData: IProyectoData): Observable<IProyectoAgrupacionGastoData> {
    return this.proyectoService.findAllAgrupacionesGasto(proyectoData.proyecto.id).pipe(
      map(values => {
        return {
          proyecto: proyectoData.proyecto,
          proyectoAgrupacionesGasto: values.items,
          readonly: proyectoData.readonly
        };
      })
    );
  }
}
