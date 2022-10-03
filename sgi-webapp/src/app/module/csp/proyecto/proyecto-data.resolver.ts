import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProyecto } from '@core/models/csp/proyecto';
import { Module } from '@core/module';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of, throwError } from 'rxjs';
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
    private solicitudService: SolicitudService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoData> {
    return this.service.findById(Number(route.paramMap.get(PROYECTO_ROUTE_PARAMS.ID))).pipe(
      map((proyecto) => {
        return {
          proyecto,
          solicitanteRefSolicitud: null,
          solicitudFormularioSolicitud: null,
          disableCoordinadorExterno: false,
          hasAnyProyectoSocioCoordinador: false,
          isVisor: this.hasVisorAuthority(proyecto) && route.data.module === Module.CSP,
          isInvestigador: this.hasViewAuthorityInv() && route.data.module === Module.INV
        } as IProyectoData;
      }),
      switchMap(data => {
        if (!data.isInvestigador && !data.isVisor && !this.hasViewAuthorityUO(data.proyecto)) {
          return throwError('NOT_FOUND');
        }

        return of(data);
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
      switchMap(data => this.fillDatosSolicitud(data)),
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

  private fillDatosSolicitud(data: IProyectoData): Observable<IProyectoData> {
    if (data?.proyecto?.solicitudId) {
      return this.solicitudService.findById(data.proyecto.solicitudId)
        .pipe(
          map(solicitud => {
            data.solicitanteRefSolicitud = solicitud.solicitante.id;
            data.solicitudFormularioSolicitud = solicitud.formularioSolicitud;
            return data;
          })
        );
    } else {
      return of(data);
    }
  }

  private hasViewAuthorityInv(): boolean {
    return this.authService.hasAuthority('CSP-PRO-INV-VR');
  }

  private hasViewAuthorityUO(proyecto: IProyecto): boolean {
    return this.authService.hasAnyAuthority(
      [
        'CSP-PRO-E',
        'CSP-PRO-E_' + proyecto.unidadGestion.id,
        'CSP-PRO-V',
        'CSP-PRO-V_' + proyecto.unidadGestion.id
      ]
    );
  }

  private hasEditAuthorityUO(proyecto: IProyecto): boolean {
    return this.authService.hasAnyAuthority(
      [
        'CSP-PRO-E',
        'CSP-PRO-E_' + proyecto.unidadGestion.id
      ]
    );
  }

  private hasVisorAuthority(proyecto: IProyecto): boolean {
    return !this.hasEditAuthorityUO(proyecto) && this.authService.hasAnyAuthority(
      [
        'CSP-PRO-V',
        'CSP-PRO-V_' + proyecto.unidadGestion.id
      ]
    );
  }

}
