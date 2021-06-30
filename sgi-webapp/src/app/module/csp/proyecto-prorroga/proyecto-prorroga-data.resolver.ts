import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoProrrogaService } from '@core/services/csp/proyecto-prorroga.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PROYECTO_DATA_KEY } from '../proyecto/proyecto-data.resolver';
import { IProyectoData } from '../proyecto/proyecto.action.service';
import { PROYECTO_PRORROGA_ROUTE_PARAMS } from './proyecto-prorroga-route-params';
import { IProyectoProrrogaData } from './proyecto-prorroga.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const PROYECTO_PRORROGA_DATA_KEY = 'proyectoProrrogaData';

@Injectable()
export class ProyectoProrrogaDataResolver extends SgiResolverResolver<IProyectoProrrogaData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoProrrogaService,
    private proyectoService: ProyectoService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoProrrogaData> {
    const proyectoData: IProyectoData = route.parent.data[PROYECTO_DATA_KEY];
    const proyectoProrrogaId = Number(route.paramMap.get(PROYECTO_PRORROGA_ROUTE_PARAMS.ID));
    if (proyectoProrrogaId) {
      return this.service.exists(proyectoProrrogaId).pipe(
        switchMap((exists) => {
          if (!exists) {
            return throwError('NOT_FOUND');
          }
          return this.loadProyectoProrrogaData(proyectoProrrogaId, proyectoData);
        })
      );
    }
    return this.loadProyectoProrrogaData(proyectoProrrogaId, proyectoData);
  }

  private loadProyectoProrrogaData(proyectoProrrogaId: number, proyectoData: IProyectoData): Observable<IProyectoProrrogaData> {
    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('numProrroga', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllProyectoProrrogaProyecto(proyectoData.proyecto.id, options).pipe(
      map(prorrogas => {
        return {
          proyecto: proyectoData.proyecto,
          proyectoProrrogas: prorrogas.items,
          readonly: this.isReadonly(proyectoProrrogaId, proyectoData, prorrogas.items)
        };
      })
    );
  }

  private isReadonly(proyectoProrrogaId: number, proyectoData: IProyectoData, proyectoProrrogas: IProyectoProrroga[]): boolean {
    if (proyectoData.readonly) {
      return true;
    }
    if (!proyectoProrrogaId || !proyectoProrrogas.length) {
      return false;
    }
    const lastProyectoProrrogaId = proyectoProrrogas[proyectoProrrogas.length - 1].id;
    return proyectoProrrogaId !== lastProyectoProrrogaId;
  }
}
