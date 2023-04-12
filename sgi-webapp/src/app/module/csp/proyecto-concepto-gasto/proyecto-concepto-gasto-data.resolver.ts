import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { IProyectoConceptoGastoCodigoEc } from '@core/models/csp/proyecto-concepto-gasto-codigo-ec';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PROYECTO_DATA_KEY } from '../proyecto/proyecto-data.resolver';
import { PROYECTO_ROUTE_PARAMS } from '../proyecto/proyecto-route-params';
import { IProyectoData } from '../proyecto/proyecto.action.service';
import { PROYECTO_CONCEPTO_GASTO_ROUTE_PARAMS } from './proyecto-concepto-gasto-route-params';
import { IProyectoConceptoGastoData } from './proyecto-concepto-gasto.action.service';

const MSG_NOT_FOUND = marker('error.load');

const PROYECTO_CONCEPTO_GASTO_PERMITIDO_KEY = marker('csp.proyecto-concepto-gasto-permitido');
const PROYECTO_CONCEPTO_GASTO_NO_PERMITIDO_KEY = marker('csp.proyecto-concepto-gasto-no-permitido');
const MSG_NEW_TITLE = marker('title.new.entity');

export const PROYECTO_CONCEPTO_GASTO_DATA_KEY = 'proyectoConceptoGastoData';

@Injectable()
export class ProyectoConceptoGastoDataResolver extends SgiResolverResolver<IProyectoConceptoGastoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoConceptoGastoService,
    private proyectoService: ProyectoService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoConceptoGastoData> {
    const proyectoData: IProyectoData = route.parent.data[PROYECTO_DATA_KEY];
    const proyectoId = Number(route.paramMap.get(PROYECTO_ROUTE_PARAMS.ID));
    const proyectoConceptoGastoId = Number(route.paramMap.get(PROYECTO_CONCEPTO_GASTO_ROUTE_PARAMS.ID));
    const permitido = Boolean(route.data.permitido);

    this.setTitle(route, !Boolean(proyectoConceptoGastoId), permitido);

    if (proyectoConceptoGastoId) {
      return this.service.exists(proyectoConceptoGastoId).pipe(
        switchMap((exist) => {
          if (!exist) {
            return throwError('NOT_FOUND');
          }
          // TODO: Fusionar con el exists
          return this.service.findById(proyectoConceptoGastoId).pipe(
            switchMap(proyectoConceptoGasto => {
              if (proyectoConceptoGasto.permitido !== permitido) {
                return throwError('NOT_FOUND');
              }
              return this.loadProyectoConceptoGastoData(proyectoId, proyectoConceptoGastoId,
                proyectoConceptoGasto.convocatoriaConceptoGastoId, permitido, proyectoData.readonly);
            })
          );
        })
      );
    }
    return this.loadProyectoConceptoGastoData(proyectoId, proyectoConceptoGastoId, null, permitido, proyectoData.readonly);
  }

  private loadProyectoConceptoGastoData(
    proyectoId: number,
    proyectoConceptoGastoId: number,
    convocatoriaConceptoGastoId: number,
    permitido: boolean,
    readonly: boolean
  ): Observable<IProyectoConceptoGastoData> {
    return this.proyectoService.findById(proyectoId).pipe(
      map(proyecto => {
        return {
          proyecto,
          selectedProyectoConceptosGastoCodigosEc: [],
          selectedProyectoConceptosGastoPermitidos: [],
          selectedProyectoConceptosGastoNoPermitidos: [],
          convocatoriaConceptoGastoId,
          permitido,
          readonly
        };
      }),
      switchMap(data =>
        forkJoin(
          {
            selectedProyectoConceptosGastoPermitidos: this.getProyectoConceptosGastoPermitidos(proyectoId, proyectoConceptoGastoId),
            selectedProyectoConceptosGastoNoPermitidos: this.getProyectoConceptosGastoNoPermitidos(proyectoId, proyectoConceptoGastoId),
            selectedProyectoConceptosGastoCodigosEcPermitidos: this.getCodigosEconomicosPermitidos(proyectoId, proyectoConceptoGastoId),
            selectedProyectoConceptosGastoCodigosEcNoPermitidos: this.getCodigosEconomicosNoPermitidos(proyectoId, proyectoConceptoGastoId),
          }
        ).pipe(
          map(({
            selectedProyectoConceptosGastoPermitidos,
            selectedProyectoConceptosGastoNoPermitidos,
            selectedProyectoConceptosGastoCodigosEcPermitidos,
            selectedProyectoConceptosGastoCodigosEcNoPermitidos
          }) => {
            data.selectedProyectoConceptosGastoPermitidos = selectedProyectoConceptosGastoPermitidos;
            data.selectedProyectoConceptosGastoNoPermitidos = selectedProyectoConceptosGastoNoPermitidos;
            data.selectedProyectoConceptosGastoCodigosEc = selectedProyectoConceptosGastoCodigosEcPermitidos.concat(selectedProyectoConceptosGastoCodigosEcNoPermitidos);
            return data;
          })
        ),
      )
    );
  }

  private getProyectoConceptosGastoPermitidos(convocatoriaId: number, currentProyectoConceptoGastoId: number): Observable<IProyectoConceptoGasto[]> {
    return this.proyectoService.findAllProyectoConceptosGastoPermitidos(convocatoriaId).pipe(
      map(response => response.items.filter(concepto => concepto.id !== currentProyectoConceptoGastoId)),
    );
  }

  private getProyectoConceptosGastoNoPermitidos(convocatoriaId: number, currentProyectoConceptoGastoId: number): Observable<IProyectoConceptoGasto[]> {
    return this.proyectoService.findAllProyectoConceptosGastoNoPermitidos(convocatoriaId).pipe(
      map(response => response.items.filter(concepto => concepto.id !== currentProyectoConceptoGastoId)),
    );
  }

  private getCodigosEconomicosPermitidos(convocatoriaId: number, currentProyectoConceptoGastoId: number): Observable<IProyectoConceptoGastoCodigoEc[]> {
    return this.proyectoService.findAllProyectoConceptoGastoCodigosEcsPermitidos(convocatoriaId).pipe(
      map(response => response.items.filter(codigoEconomico => codigoEconomico.proyectoConceptoGasto?.id !== currentProyectoConceptoGastoId)),
    );
  }

  private getCodigosEconomicosNoPermitidos(convocatoriaId: number, currentProyectoConceptoGastoId: number): Observable<IProyectoConceptoGastoCodigoEc[]> {
    return this.proyectoService.findAllProyectoConceptoGastoCodigosEcsNoPermitidos(convocatoriaId).pipe(
      map(response => response.items.filter(codigoEconomico => codigoEconomico.proyectoConceptoGasto?.id !== currentProyectoConceptoGastoId)),
    );
  }

  private setTitle(route: ActivatedRouteSnapshot, nuevo: boolean, permitido: boolean): void {
    if (!route.routeConfig.data) {
      route.routeConfig.data = {};
    }
    route.routeConfig.data.title = this.getRouteTitle(nuevo, permitido);
    route.routeConfig.data.titleParams = this.getRouteTitleParams(nuevo, permitido);
  }

  private getRouteTitleParams(nuevo: boolean, permitido: boolean): { [key: string]: any } {
    const entityKey = permitido ? PROYECTO_CONCEPTO_GASTO_PERMITIDO_KEY : PROYECTO_CONCEPTO_GASTO_NO_PERMITIDO_KEY;
    if (nuevo) {
      return { entity: entityKey, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR };
    }
    return MSG_PARAMS.CARDINALIRY.SINGULAR;
  }

  private getRouteTitle(nuevo: boolean, permitido: boolean): string {
    if (nuevo) {
      return MSG_NEW_TITLE;
    }
    return permitido ? PROYECTO_CONCEPTO_GASTO_PERMITIDO_KEY : PROYECTO_CONCEPTO_GASTO_NO_PERMITIDO_KEY;
  }
}
