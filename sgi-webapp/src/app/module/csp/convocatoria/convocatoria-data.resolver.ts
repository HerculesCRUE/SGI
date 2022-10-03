import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Estado, IConvocatoria } from '@core/models/csp/convocatoria';
import { Module } from '@core/module';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ConfiguracionSolicitudService } from '@core/services/csp/configuracion-solicitud.service';
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
    private configuracionSolicitudService: ConfiguracionSolicitudService,
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

        if (route.data.module === Module.CSP && this.hasViewAuthorityUO(convocatoria)) {
          return this.service.modificable(convocatoriaId).pipe(
            map(response => {
              return {
                canEdit: this.hasEditAuthorityUO(convocatoria),
                readonly: !response,
                estado: convocatoria?.estado
              } as IConvocatoriaData;
            }),
            switchMap(data => this.hasSolicitudesReferences(data, convocatoriaId)),
            switchMap(data => this.hasProyectosReferences(data, convocatoriaId))
          );
        } else if (route.data.module === Module.INV && this.hasViewAuthorityInv() && convocatoria.estado === Estado.REGISTRADA) {
          return this.configuracionSolicitudService.findByConvocatoriaId(convocatoria.id).pipe(
            switchMap(configuracion => {
              if (!configuracion.tramitacionSGI) {
                return throwError('NOT_FOUND');
              }

              return of({
                readonly: true,
                canEdit: false
              } as IConvocatoriaData);
            })
          );
        } else {
          return throwError('NOT_FOUND');
        }

      }),
    );
  }

  private hasViewAuthorityInv(): boolean {
    return this.authService.hasAuthority('CSP-CON-INV-V');
  }

  private hasViewAuthorityUO(convocatoria: IConvocatoria): boolean {
    return this.authService.hasAnyAuthority(
      [
        'CSP-CON-E',
        'CSP-CON-E_' + convocatoria.unidadGestion.id,
        'CSP-CON-V',
        'CSP-CON-V_' + convocatoria.unidadGestion.id
      ]
    );
  }

  private hasEditAuthorityUO(convocatoria: IConvocatoria): boolean {
    return this.authService.hasAnyAuthority(
      [
        'CSP-CON-E',
        'CSP-CON-E_' + convocatoria.unidadGestion.id
      ]
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
