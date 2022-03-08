import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { GRUPO_ROUTE_PARAMS } from './autorizacion-route-params';
import { IGrupoData } from './grupo.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const GRUPO_DATA_KEY = 'grupoData';

@Injectable()
export class GrupoDataResolver extends SgiResolverResolver<IGrupoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: GrupoService,
    private authService: SgiAuthService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IGrupoData> {
    const grupoId = Number(route.paramMap.get(GRUPO_ROUTE_PARAMS.ID));

    return this.service.exists(grupoId).pipe(
      switchMap(value => {
        if (!value) {
          return throwError('NOT_FOUND');
        }
        return of(
          {
            isInvestigador: this.authService.hasAnyAuthority(['CSP-GIN-INV-V'])
          } as IGrupoData
        );
      })
    );
  }


}
