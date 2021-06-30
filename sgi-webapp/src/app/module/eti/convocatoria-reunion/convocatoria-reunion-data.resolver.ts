import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConvocatoriaReunionService } from '@core/services/eti/convocatoria-reunion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CONVOCATORIA_REUNION_ROUTE_PARAMS } from './convocatoria-reunion-route-params';
import { IConvocatoriaReunionData } from './convocatoria-reunion.action.service';

const MSG_NOT_FOUND = marker('error.load');
export const CONVOCATORIA_REUNION_DATA_KEY = 'convocatoriaReunionData';

@Injectable()
export class ConvocatoriaReunionDataResolver extends SgiResolverResolver<IConvocatoriaReunionData>{

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: ConvocatoriaReunionService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IConvocatoriaReunionData> {
    return this.service.findById(Number(route.paramMap.get(CONVOCATORIA_REUNION_ROUTE_PARAMS.ID))).pipe(
      map(convocatoriaReunion => {
        return {
          convocatoriaReunion
        } as IConvocatoriaReunionData;
      }),
      switchMap(data => {
        return this.service.modificable(data.convocatoriaReunion.id).pipe(
          map(value => {
            data.readonly = !value;
            return data;
          })
        );
      })
    );
  }

}
