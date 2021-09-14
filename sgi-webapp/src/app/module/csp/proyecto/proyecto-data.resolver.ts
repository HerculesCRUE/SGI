import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { PROYECTO_ROUTE_PARAMS } from './proyecto-route-params';
import { IProyectoData } from './proyecto.action.service';

const MSG_NOT_FOUND = marker('csp.proyecto.editar.no-encontrado');

export const PROYECTO_DATA_KEY = 'proyectoData';

@Injectable()
export class ProyectoDataResolver extends SgiResolverResolver<IProyectoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProyectoService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoData> {
    return this.service.findById(Number(route.paramMap.get(PROYECTO_ROUTE_PARAMS.ID))).pipe(
      map((proyecto) => {
        return {
          proyecto,
          disableCoordinadorExterno: false,
          hasAnyProyectoSocioCoordinador: false,
          isVisor: this.authService.hasAuthorityForAnyUO('CSP-PRO-V')
        } as IProyectoData;
      }),
      switchMap(data => {
        return forkJoin([this.service.hasPeriodosPago(data.proyecto.id), this.service.hasPeriodosJustificacion(data.proyecto.id)])
          .pipe(
            map((response) => {
              data.disableCoordinadorExterno = response[0] || response[1];
              return data;
            })
          );
      }),
      switchMap(data => this.hasAnyProyectoSocioWithRolCoordinador(data)),
      switchMap(data => this.isReadonly(data))
    );
  }

  private isReadonly(data: IProyectoData): Observable<IProyectoData> {
    if (data?.proyecto?.id) {
      return this.service.modificable(data?.proyecto?.id)
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

  private hasAnyProyectoSocioWithRolCoordinador(data: IProyectoData): Observable<IProyectoData> {
    if (data?.proyecto?.id) {
      return this.service.hasAnyProyectoSocioWithRolCoordinador(data?.proyecto?.id)
        .pipe(
          map((value: boolean) => {
            data.hasAnyProyectoSocioCoordinador = value;
            return data;
          })
        );
    } else {
      data.hasAnyProyectoSocioCoordinador = false;
      return of(data);
    }
  }

}
