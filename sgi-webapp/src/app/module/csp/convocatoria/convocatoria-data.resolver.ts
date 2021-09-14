import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { CONVOCATORIA_ROUTE_PARAMS } from './convocatoria-route-params';
import { IConvocatoriaData } from './convocatoria.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const CONVOCATORIA_DATA_KEY = 'convocatoriaData';

@Injectable()
export class ConvocatoriaDataResolver extends SgiResolverResolver<IConvocatoriaData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ConvocatoriaService,
    private authService: SgiAuthService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IConvocatoriaData> {
    const convocatoriaId = Number(route.paramMap.get(CONVOCATORIA_ROUTE_PARAMS.ID));

    return this.service.findById(convocatoriaId).pipe(
      switchMap((convocatoria: IConvocatoria) => {
        if (!convocatoria) {
          return throwError('NOT_FOUND');
        }
        if (this.authService.hasAnyAuthorityForAnyUO(['CSP-CON-V', 'CSP-CON-E'])) {
          return this.service.modificable(convocatoriaId).pipe(
            map(response => {
              return {
                canEdit: this.authService.hasAnyAuthorityForAnyUO(['CSP-CON-E']),
                readonly: !response,
                estado: convocatoria?.estado
              } as IConvocatoriaData;
            }),
            switchMap(data => this.hasSolicitudesReferences(data, convocatoriaId)),
            switchMap(data => this.hasProyectosReferences(data, convocatoriaId))
          );
        } else {
          return of({
            readonly: this.authService.hasAuthorityForAnyUO('CSP-CON-INV-V'),
            canEdit: false
          } as IConvocatoriaData);
        }
      }),
    );
  }

  private hasSolicitudesReferences(data: IConvocatoriaData, convocatoriaId: number): Observable<IConvocatoriaData> {
    return this.service.hasSolicitudesReferenced(convocatoriaId)
      .pipe(
        map((hasAny: boolean) => {
          return {
            ...data,
            showSolicitudesLink: hasAny
          };
        })
      );
  }

  private hasProyectosReferences(data: IConvocatoriaData, convocatoriaId: number): Observable<IConvocatoriaData> {
    return this.service.hasProyectosReferenced(convocatoriaId)
      .pipe(
        map((hasAny: boolean) => {
          return {
            ...data,
            showProyectosLink: hasAny
          };
        })
      );
  }
}
