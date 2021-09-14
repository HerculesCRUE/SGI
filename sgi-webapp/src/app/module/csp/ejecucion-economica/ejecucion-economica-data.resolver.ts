import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { from, Observable } from 'rxjs';
import { map, mergeMap, switchMap, takeLast } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';
import { IEjecucionEconomicaData } from './ejecucion-economica.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const EJECUCION_ECONOMICA_DATA_KEY = 'ejecucionEconomicaData';

@Injectable()
export class EjecucionEconomicaDataResolver extends SgiResolverResolver<IEjecucionEconomicaData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoProyectoSgeService,
    private proyectoService: ProyectoService,
    private proyectoSgeService: ProyectoSgeService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IEjecucionEconomicaData> {

    return this.service.findById(Number(route.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID))).pipe(
      map(proyectoProyectoSge => {
        return {
          proyectoSge: proyectoProyectoSge.proyectoSge,
          readonly: false,
          proyectosRelacionados: []
        } as IEjecucionEconomicaData;
      }),
      switchMap(data => {
        return this.proyectoSgeService.findById(data.proyectoSge.id).pipe(
          map(proyectoSge => {
            data.proyectoSge = proyectoSge;
            return data;
          })
        );
      }),
      switchMap(data => {
        const options: SgiRestFindOptions = {
          filter: new RSQLSgiRestFilter('proyectoSgeRef', SgiRestFilterOperator.EQUALS, data.proyectoSge.id)
        };
        return this.service.findAll(options).pipe(
          map(response => response.items.map(item => item.proyecto.id)),
          switchMap(idsProyecto => from(idsProyecto)),
          mergeMap(id => {
            return this.proyectoService.findById(id).pipe(
              map(proyecto => {
                data.proyectosRelacionados.push(proyecto);
                return data;
              })
            );
          }),
          takeLast(1)
        );
      })
    );
  }
}
