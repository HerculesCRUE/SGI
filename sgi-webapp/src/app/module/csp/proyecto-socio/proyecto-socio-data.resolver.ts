import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoSocioService } from '@core/services/csp/proyecto-socio.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PROYECTO_DATA_KEY } from '../proyecto/proyecto-data.resolver';
import { IProyectoData } from '../proyecto/proyecto.action.service';
import { PROYECTO_SOCIO_ROUTE_PARAMS } from './proyecto-socio-route-params';
import { IProyectoSocioData } from './proyecto-socio.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const PROYECTO_SOCIO_DATA_KEY = 'proyectoSocioData';

@Injectable()
export class ProyectoSocioDataResolver extends SgiResolverResolver<IProyectoSocioData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoSocioService,
    private proyectoService: ProyectoService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoSocioData> {
    const proyectoData: IProyectoData = route.parent.data[PROYECTO_DATA_KEY];
    const proyectoSocioId = Number(route.paramMap.get(PROYECTO_SOCIO_ROUTE_PARAMS.ID));
    if (proyectoSocioId) {
      return this.service.exists(proyectoSocioId).pipe(
        switchMap((exists) => {
          if (!exists) {
            return throwError('NOT_FOUND');
          }
          return this.loadProyectoSocioData(proyectoData);
        })
      );
    }
    return this.loadProyectoSocioData(proyectoData);
  }

  private loadProyectoSocioData(proyectoData: IProyectoData): Observable<IProyectoSocioData> {
    return this.proyectoService.findAllProyectoSocioProyecto(proyectoData.proyecto.id).pipe(
      map(socios => {
        return {
          proyecto: proyectoData.proyecto,
          proyectoSocios: socios.items,
          readonly: proyectoData.readonly
        };
      })
    );
  }
}
