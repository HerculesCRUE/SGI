import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_ROUTE_PARAMS } from './ejecucion-economica-route-params';
import { IEjecucionEconomicaData } from './ejecucion-economica.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const EJECUCION_ECONOMICA_DATA_KEY = 'ejecucionEconomicaData';

@Injectable()
export class EjecucionEconomicaDataResolver extends SgiResolverResolver<IEjecucionEconomicaData> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: ProyectoProyectoSgeService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IEjecucionEconomicaData> {

    return this.service.findById(Number(route.paramMap.get(EJECUCION_ECONOMICA_ROUTE_PARAMS.ID))).pipe(
      map(proyectoProyectoSge => {
        return {
          proyectoProyectoSge
        } as IEjecucionEconomicaData;
      })
    );
  }
}
