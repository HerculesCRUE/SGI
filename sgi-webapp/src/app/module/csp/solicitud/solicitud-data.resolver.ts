import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { Estado } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { Module } from '@core/module';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { SolicitudProyectoService } from '@core/services/csp/solicitud-proyecto.service';
import { SolicitudRrhhService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SOLICITUD_ROUTE_PARAMS } from './solicitud-route-params';
import { ISolicitudData } from './solicitud.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const SOLICITUD_DATA_KEY = 'solicitudData';

const ALLOWED_PROYECTO_LINK_ESTADOS = [
  Estado.CONCEDIDA,
  Estado.CONCEDIDA_PROVISIONAL,
  Estado.CONCEDIDA_PROVISIONAL_ALEGADA,
  Estado.CONCEDIDA_PROVISIONAL_NO_ALEGADA];

@Injectable()
export class SolicitudDataResolver extends SgiResolverResolver<ISolicitudData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: SolicitudService,
    private authService: SgiAuthService,
    private solicitudProyectoService: SolicitudProyectoService,
    private solicitudRrhhService: SolicitudRrhhService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<ISolicitudData> {
    const isInvestigador = route.data.module === Module.INV && this.hasViewAuthorityInv();
    const currentUser = this.authService.authStatus$?.getValue()?.userRefId;


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
        const modificable$ = isInvestigador
          ? this.service.modificableByInvestigador(data.solicitud.id)
          : this.service.modificableByUO(data.solicitud.id);
        return modificable$.pipe(
          map(value => {
            data.readonly = !value;
            return data;
          })
        );
      }),
      switchMap(data => {
        data.isInvestigador = isInvestigador;

        if (!isInvestigador) {
          data.estadoAndDocumentosReadonly = data.readonly;

          if (route.data.module === Module.CSP && this.hasViewAuthorityUO(data.solicitud)) {
            return of(data);
          }

          return throwError('NOT_FOUND');
        }

        return forkJoin(
          {
            modificableEstadoAndDocumentos: this.service.modificableEstadoAndDocumentosByInvestigador(data.solicitud.id),
            modificableEstadoAsTutor: data.solicitud.formularioSolicitud === FormularioSolicitud.RRHH
              ? this.service.modificableEstadoAsTutor(data.solicitud.id) : of(false),
            tutorRrhh: data.solicitud.formularioSolicitud === FormularioSolicitud.RRHH
              ? this.solicitudRrhhService.findTutor(data.solicitud.id) : of({} as ISolicitudRrhhTutor)
          }
        ).pipe(
          switchMap(({ modificableEstadoAndDocumentos, modificableEstadoAsTutor, tutorRrhh }) => {
            data.estadoAndDocumentosReadonly = !modificableEstadoAndDocumentos;
            data.modificableEstadoAsTutor = modificableEstadoAsTutor;
            data.isTutor = tutorRrhh?.tutor?.id === currentUser;

            if (data.isTutor || data.solicitud.solicitante?.id === currentUser) {
              return of(data);
            }

            return throwError('NOT_FOUND');
          }),

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
      switchMap(data => {
        if (data.solicitud && ALLOWED_PROYECTO_LINK_ESTADOS.includes(data.solicitud.estado.estado)) {
          return this.service.findIdsProyectosBySolicitudId(data.solicitud.id).pipe(
            map(response => {
              return {
                ...data,
                proyectosIds: response
              };
            })
          );
        }
        return of(data);
      })
    );
  }

  private hasViewAuthorityInv(): boolean {
    return this.authService.hasAuthority('CSP-SOL-INV-ER');
  }

  private hasViewAuthorityUO(solicitud: ISolicitud): boolean {
    return this.authService.hasAnyAuthority(
      [
        'CSP-SOL-E',
        'CSP-SOL-E_' + solicitud.unidadGestion.id,
        'CSP-SOL-V',
        'CSP-SOL-V_' + solicitud.unidadGestion.id
      ]
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
