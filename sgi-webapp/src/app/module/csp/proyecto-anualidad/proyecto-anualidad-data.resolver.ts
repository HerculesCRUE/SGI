import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfigService } from '@core/services/csp/config.service';
import { ProyectoAnualidadService } from '@core/services/csp/proyecto-anualidad/proyecto-anualidad.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { RSQLSgiRestSort, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PROYECTO_DATA_KEY } from '../proyecto/proyecto-data.resolver';
import { IProyectoData } from '../proyecto/proyecto.action.service';
import { PROYECTO_ANUALIDAD_ROUTE_PARAMS } from './proyecto-anualidad-route-params';
import { IProyectoAnualidadData } from './proyecto-anualidad.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const PROYECTO_ANUALIDAD_DATA_KEY = 'proyectoAnualidadData';

@Injectable()
export class ProyectoAnualidadDataResolver extends SgiResolverResolver<IProyectoAnualidadData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoAnualidadService,
    private proyectoService: ProyectoService,
    private configService: ConfigService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoAnualidadData> {
    const proyectoData: IProyectoData = route.parent.data[PROYECTO_DATA_KEY];
    const proyectoAnualidadId = Number(route.paramMap.get(PROYECTO_ANUALIDAD_ROUTE_PARAMS.ID));
    if (proyectoAnualidadId) {
      return this.service.exists(proyectoAnualidadId).pipe(
        switchMap((exist) => {
          if (!exist) {
            return throwError('NOT_FOUND');
          }
          return this.loadProyectoAnualidadData(proyectoData, proyectoAnualidadId);
        })
      );
    }
    return this.loadProyectoAnualidadData(proyectoData, proyectoAnualidadId);
  }

  private loadProyectoAnualidadData(
    proyectoData: IProyectoData,
    proyectoAnualiadId: number
  ): Observable<IProyectoAnualidadData> {
    const options: SgiRestFindOptions = {
      sort: new RSQLSgiRestSort('anio', SgiRestSortDirection.ASC)
    };
    return this.proyectoService.findAllProyectoAnualidades(proyectoData.proyecto.id, options).pipe(
      map(anualidades => {
        const proyectoAnualidad = proyectoAnualiadId ? anualidades.items.find(anualidad => anualidad.id === proyectoAnualiadId) : null;
        return {
          proyecto: proyectoData.proyecto,
          proyectoAnualidadResumen: anualidades.items,
          readonly: proyectoAnualidad?.enviadoSge ?? proyectoData.readonly
        } as IProyectoAnualidadData;
      }),
      switchMap(data => this.configService.getCardinalidadRelacionSgiSge().pipe(
        map(cardinalidadRelacionSgiSge => {
          data.cardinalidadRelacionSgiSge = cardinalidadRelacionSgiSge;
          return data;
        })
      ))
    );
  }
}
