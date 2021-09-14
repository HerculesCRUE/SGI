import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ISolicitudProyecto } from '@core/models/csp/solicitud-proyecto';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SOLICITUD_ROUTE_PARAMS } from './solicitud-route-params';
import { ISolicitudData } from './solicitud.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const SOLICITUD_DATA_KEY = 'solicitudData';

@Injectable()
export class SolicitudDataResolver extends SgiResolverResolver<ISolicitudData> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: SolicitudService,
    private authService: SgiAuthService,
    private solicitudProyectoService: SolicitudProyectoService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<ISolicitudData> {

    return this.service.findById(Number(route.paramMap.get(SOLICITUD_ROUTE_PARAMS.ID))).pipe(
      map(solicitud => {
        return {
          solicitud
        } as ISolicitudData;
      }),
      switchMap(data => {
        return this.service.existsSolictudProyecto(data.solicitud.id).pipe(
          map(exists => {
            data.hasSolicitudProyecto = exists;
            return data;
          })
        );
      }),
      switchMap(data => {
        return this.service.modificable(data.solicitud.id).pipe(
          map(value => {
            data.readonly = !value;
            return data;
          })
        );
      }),
      switchMap(data => {
        if (data.hasSolicitudProyecto) {
          return this.service.findSolicitudProyecto(data.solicitud.id)
            .pipe(
              map(solicitudProyecto => {
                return { ...data, solicitudProyecto };
              })
            );
        }
        return of(data);
      }),
      switchMap(data => this.verifyIfWhenProyectoCoordinadoAndCoordinadorExternoHasAnySocioCoordinador(data)),
      switchMap(data => {
        if (data.hasSolicitudProyecto) {
          return this.checkIfSolicitudProyectoSocioHasPeriodosPagoAndJustificacion(data);
        }
        return of(data);
      }),
    );
  }

  private checkIfSolicitudProyectoSocioHasPeriodosPagoAndJustificacion(data: ISolicitudData):
    Observable<ISolicitudData> {

    if (!data.solicitudProyecto?.id) {
      return of(data);
    }

    return forkJoin([this.solicitudProyectoService.hasPeriodosPago(data.solicitudProyecto.id),
    this.solicitudProyectoService.hasPeriodosJustificacion(data.solicitudProyecto.id)])
      .pipe(
        map(response => {
          data.hasPopulatedPeriodosSocios = response[0] || response[1];
          return data;
        })
      );
  }

  private verifyIfWhenProyectoCoordinadoAndCoordinadorExternoHasAnySocioCoordinador(data: ISolicitudData): Observable<ISolicitudData> {
    if (data?.solicitudProyecto) {
      return this.solicitudProyectoService.hasAnySolicitudProyectoSocioWithRolCoordinador(data?.solicitudProyecto.id).pipe(
        map((value: boolean) => {
          return {
            ...data,
            hasAnySolicitudProyectoSocioWithRolCoordinador: data.solicitudProyecto?.coordinadorExterno ? value : true
          };
        }));
    } else {
      return of(data);
    }
  }
}
