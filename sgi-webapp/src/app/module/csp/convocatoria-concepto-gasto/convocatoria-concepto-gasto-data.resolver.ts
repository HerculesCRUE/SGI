import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { IConvocatoriaConceptoGastoCodigoEc } from '@core/models/csp/convocatoria-concepto-gasto-codigo-ec';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CONVOCATORIA_DATA_KEY } from '../convocatoria/convocatoria-data.resolver';
import { CONVOCATORIA_ROUTE_PARAMS } from '../convocatoria/convocatoria-route-params';
import { IConvocatoriaData } from '../convocatoria/convocatoria.action.service';
import { CONVOCATORIA_CONCEPTO_GASTO_ROUTE_PARAMS } from './convocatoria-concepto-gasto-route-params';
import { IConvocatoriaConceptoGastoData } from './convocatoria-concepto-gasto.action.service';

const MSG_NOT_FOUND = marker('error.load');

const CONVOCATORIA_CONCEPTO_GASTO_PERMITIDO_KEY = marker('csp.convocatoria-concepto-gasto-permitido');
const CONVOCATORIA_CONCEPTO_GASTO_NO_PERMITIDO_KEY = marker('csp.convocatoria-concepto-gasto-no-permitido');
const MSG_NEW_TITLE = marker('title.new.entity');

export const CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY = 'convocatoriaConceptoGastoData';

@Injectable()
export class ConvocatoriaConceptoGastoDataResolver extends SgiResolverResolver<IConvocatoriaConceptoGastoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ConvocatoriaConceptoGastoService,
    private convocatoriaService: ConvocatoriaService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IConvocatoriaConceptoGastoData> {
    const convocatoriaData: IConvocatoriaData = route.parent.data[CONVOCATORIA_DATA_KEY];
    const convocatoriaId = Number(route.paramMap.get(CONVOCATORIA_ROUTE_PARAMS.ID));
    const convocatoriaConceptoGastoId = Number(route.paramMap.get(CONVOCATORIA_CONCEPTO_GASTO_ROUTE_PARAMS.ID));
    const permitido = Boolean(route.data.permitido);

    this.setTitle(route, !Boolean(convocatoriaConceptoGastoId), permitido);

    if (convocatoriaConceptoGastoId) {
      return this.service.exists(convocatoriaConceptoGastoId).pipe(
        switchMap((exist) => {
          if (!exist) {
            return throwError('NOT_FOUND');
          }
          // TODO: Fusionar con el exists
          return this.service.findById(convocatoriaConceptoGastoId).pipe(
            switchMap(convocatoriaConceptoGasto => {
              if (convocatoriaConceptoGasto.permitido !== permitido) {
                return throwError('NOT_FOUND');
              }
              return this.loadConvocatoriaConceptoGastoData(
                convocatoriaId, convocatoriaConceptoGastoId, permitido, convocatoriaData.readonly, convocatoriaData.canEdit
              );
            })
          );
        })
      );
    }
    return this.loadConvocatoriaConceptoGastoData(convocatoriaId, convocatoriaConceptoGastoId, permitido, convocatoriaData.readonly, convocatoriaData.canEdit);
  }

  private loadConvocatoriaConceptoGastoData(
    convocatoriaId: number,
    convocatoriaConceptoGastoId: number,
    permitido: boolean,
    readonly: boolean,
    canEdit: boolean
  ): Observable<IConvocatoriaConceptoGastoData> {
    return this.convocatoriaService.findById(convocatoriaId).pipe(
      map(convocatoria => {
        return {
          convocatoria,
          selectedConvocatoriaConceptoGastosPermitidos: [],
          selectedConvocatoriaConceptoGastosNoPermitidos: [],
          selectedConvocatoriaConceptoGastoCodigosEc: [],
          permitido,
          readonly,
          canEdit
        };
      }),
      switchMap(data =>
        forkJoin(
          {
            selectedConvocatoriaConceptoGastosPermitidos: this.getConceptoGastosPermitidos(convocatoriaId, convocatoriaConceptoGastoId),
            selectedConvocatoriaConceptoGastosNoPermitidos: this.getConceptoGastosNoPermitidos(convocatoriaId, convocatoriaConceptoGastoId),
            selectedConvocatoriaConceptoGastoCodigosEcPermitidos: this.getCodigosEconomicosPermitidos(convocatoriaId, convocatoriaConceptoGastoId),
            selectedConvocatoriaConceptoGastoCodigosEcNoPermitidos: this.getCodigosEconomicosNoPermitidos(convocatoriaId, convocatoriaConceptoGastoId),
          }
        ).pipe(
          map(({
            selectedConvocatoriaConceptoGastosPermitidos,
            selectedConvocatoriaConceptoGastosNoPermitidos,
            selectedConvocatoriaConceptoGastoCodigosEcPermitidos,
            selectedConvocatoriaConceptoGastoCodigosEcNoPermitidos
          }) => {
            data.selectedConvocatoriaConceptoGastosPermitidos = selectedConvocatoriaConceptoGastosPermitidos;
            data.selectedConvocatoriaConceptoGastosNoPermitidos = selectedConvocatoriaConceptoGastosNoPermitidos;
            data.selectedConvocatoriaConceptoGastoCodigosEc = selectedConvocatoriaConceptoGastoCodigosEcPermitidos.concat(selectedConvocatoriaConceptoGastoCodigosEcNoPermitidos);
            return data;
          })
        ),
      )
    );
  }

  private getConceptoGastosPermitidos(convocatoriaId: number, currentConvocatoriaConceptoGastoId: number): Observable<IConvocatoriaConceptoGasto[]> {
    return this.convocatoriaService.findAllConvocatoriaConceptoGastosPermitidos(convocatoriaId).pipe(
      map(response => response.items.filter(concepto => concepto.id !== currentConvocatoriaConceptoGastoId)),
    );
  }

  private getConceptoGastosNoPermitidos(convocatoriaId: number, currentConvocatoriaConceptoGastoId: number): Observable<IConvocatoriaConceptoGasto[]> {
    return this.convocatoriaService.findAllConvocatoriaConceptoGastosNoPermitidos(convocatoriaId).pipe(
      map(response => response.items.filter(concepto => concepto.id !== currentConvocatoriaConceptoGastoId)),
    );
  }

  private getCodigosEconomicosPermitidos(convocatoriaId: number, currentConvocatoriaConceptoGastoId: number): Observable<IConvocatoriaConceptoGastoCodigoEc[]> {
    return this.convocatoriaService.findAllConvocatoriaConceptoGastoCodigoEcsPermitidos(convocatoriaId).pipe(
      map(response => response.items.filter(codigoEconomico => codigoEconomico.convocatoriaConceptoGastoId !== currentConvocatoriaConceptoGastoId)),
    );
  }

  private getCodigosEconomicosNoPermitidos(convocatoriaId: number, currentConvocatoriaConceptoGastoId: number): Observable<IConvocatoriaConceptoGastoCodigoEc[]> {
    return this.convocatoriaService.findAllConvocatoriaConceptoGastoCodigoEcsNoPermitidos(convocatoriaId).pipe(
      map(response => response.items.filter(codigoEconomico => codigoEconomico.convocatoriaConceptoGastoId !== currentConvocatoriaConceptoGastoId)),
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
    const entityKey = permitido ? CONVOCATORIA_CONCEPTO_GASTO_PERMITIDO_KEY : CONVOCATORIA_CONCEPTO_GASTO_NO_PERMITIDO_KEY;
    if (nuevo) {
      return { entity: entityKey, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR };
    }
    return MSG_PARAMS.CARDINALIRY.SINGULAR;
  }

  private getRouteTitle(nuevo: boolean, permitido: boolean): string {
    if (nuevo) {
      return MSG_NEW_TITLE;
    }
    return permitido ? CONVOCATORIA_CONCEPTO_GASTO_PERMITIDO_KEY : CONVOCATORIA_CONCEPTO_GASTO_NO_PERMITIDO_KEY;
  }

}
