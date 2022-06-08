import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IGrupo } from '@core/models/csp/grupo';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { GRUPO_ROUTE_PARAMS } from './grupo-route-params';
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
            grupo: { id: grupoId } as IGrupo,
            isInvestigador: this.authService.hasAnyAuthority(['CSP-GIN-INV-V'])
          } as IGrupoData
        );
      }),
      switchMap(data => this.isReadonly(data, grupoId))
    );
  }

  private isReadonly(data: IGrupoData, grupoId: number): Observable<IGrupoData> {
    if (grupoId) {
      return this.service.modificable(grupoId)
        .pipe(
          map((value: boolean) => {
            data.readonly = !value;
            return data;
          })
        );
    } else {
      data.readonly = false;
      return of(data);
    }
  }


}
