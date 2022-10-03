import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConvocatoriaPublicService } from '@core/services/csp/convocatoria-public.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_PUBLIC_ROUTE_PARAMS } from './convocatoria-public-route-params';

const MSG_NOT_FOUND = marker('error.load');

export const CONVOCATORIA_PUBLIC_DATA_KEY = 'convocatoriaData';

@Injectable()
export class ConvocatoriaPublicDataResolver extends SgiResolverResolver<boolean> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ConvocatoriaPublicService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<boolean> {
    const convocatoriaId = Number(route.paramMap.get(CONVOCATORIA_PUBLIC_ROUTE_PARAMS.ID));

    return this.service.exists(convocatoriaId).pipe(
      switchMap(exists => {
        if (!exists) {
          return throwError('NOT_FOUND');
        }

        return of(exists);
      }),
    );
  }

}
