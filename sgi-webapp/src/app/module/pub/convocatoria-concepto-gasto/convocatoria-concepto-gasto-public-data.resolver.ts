import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConvocatoriaConceptoGastoPublicService } from '@core/services/csp/convocatoria-concepto-gasto-public.service';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CONVOCATORIA_PUBLIC_ROUTE_PARAMS } from '../convocatoria/convocatoria-public-route-params';
import { CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_PARAMS } from './convocatoria-concepto-gasto-public-route-params';
import { IConvocatoriaConceptoGastoPublicData } from './convocatoria-concepto-gasto-public.action.service';

const MSG_NOT_FOUND = marker('error.load');

const CONVOCATORIA_CONCEPTO_GASTO_PERMITIDO_KEY = marker('csp.convocatoria-concepto-gasto-permitido');
const CONVOCATORIA_CONCEPTO_GASTO_NO_PERMITIDO_KEY = marker('csp.convocatoria-concepto-gasto-no-permitido');

export const CONVOCATORIA_CONCEPTO_GASTO_DATA_KEY = 'convocatoriaConceptoGastoData';

@Injectable()
export class ConvocatoriaConceptoGastoPublicDataResolver extends SgiResolverResolver<IConvocatoriaConceptoGastoPublicData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ConvocatoriaConceptoGastoPublicService,
    private convocatoriaService: ConvocatoriaPublicService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IConvocatoriaConceptoGastoPublicData> {
    const convocatoriaId = Number(route.paramMap.get(CONVOCATORIA_PUBLIC_ROUTE_PARAMS.ID));
    const convocatoriaConceptoGastoId = Number(route.paramMap.get(CONVOCATORIA_CONCEPTO_GASTO_PUBLIC_ROUTE_PARAMS.ID));
    const permitido = Boolean(route.data.permitido);

    this.setTitle(route, permitido);

    if (convocatoriaConceptoGastoId) {
      return this.service.findById(convocatoriaConceptoGastoId).pipe(
        switchMap(convocatoriaConceptoGasto => {
          if (!convocatoriaConceptoGasto || convocatoriaConceptoGasto.permitido !== permitido) {
            return throwError('NOT_FOUND');
          }
          return this.loadConvocatoriaConceptoGastoData(convocatoriaId, permitido);
        })
      );

    }
    return this.loadConvocatoriaConceptoGastoData(convocatoriaId, permitido);
  }

  private loadConvocatoriaConceptoGastoData(
    convocatoriaId: number,
    permitido: boolean
  ): Observable<IConvocatoriaConceptoGastoPublicData> {
    return this.convocatoriaService.findById(convocatoriaId).pipe(
      map(convocatoria => {
        return {
          convocatoria,
          permitido
        };
      })
    );
  }

  private setTitle(route: ActivatedRouteSnapshot, permitido: boolean): void {
    if (!route.routeConfig.data) {
      route.routeConfig.data = {};
    }
    route.routeConfig.data.title = this.getRouteTitle(permitido);
    route.routeConfig.data.titleParams = this.getRouteTitleParams();
  }

  private getRouteTitleParams(): { [key: string]: any } {
    return MSG_PARAMS.CARDINALIRY.SINGULAR;
  }

  private getRouteTitle(permitido: boolean): string {
    return permitido ? CONVOCATORIA_CONCEPTO_GASTO_PERMITIDO_KEY : CONVOCATORIA_CONCEPTO_GASTO_NO_PERMITIDO_KEY;
  }
}
