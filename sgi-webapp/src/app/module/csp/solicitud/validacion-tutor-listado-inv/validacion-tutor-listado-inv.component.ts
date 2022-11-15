import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-solicitud';
import { ISolicitud } from '@core/models/csp/solicitud';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { SolicitudRrhhService } from '@core/services/csp/solicitud-rrhh/solicitud-rrhh.service';
import { SolicitudService } from '@core/services/csp/solicitud.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { from, merge, Observable, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';

interface SolicitudListado extends ISolicitud {
  tituloConvocatoria: string;
  nombreSolicitante: string;
  tituloTrabajo: string;
}

@Component({
  selector: 'sgi-validacion-tutor-listado',
  templateUrl: './validacion-tutor-listado-inv.component.html',
  styleUrls: ['./validacion-tutor-listado-inv.component.scss']
})
export class ValidacionTutorListadoInvComponent extends AbstractTablePaginationComponent<SolicitudListado> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  solicitudes$: Observable<SolicitudListado[]>;

  get ESTADO() {
    return Estado;
  }

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private solicitudService: SolicitudService,
    private solicitudRrhhService: SolicitudRrhhService,
    private personaService: PersonaService
  ) {
    super();
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.formGroup = new FormGroup({
      pendientes: new FormControl(true),
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<SolicitudListado>> {
    return this.solicitudService.findAllTutor(this.getFindOptions(reset)).pipe(
      map(response => {
        return response as SgiRestListResult<SolicitudListado>;
      }),
      switchMap(response => {
        const requestsConvocatoria: Observable<SolicitudListado>[] = [];
        response.items.forEach(solicitud => {
          if (solicitud.convocatoriaId) {
            requestsConvocatoria.push(this.solicitudService.findConvocatoria(solicitud.id).pipe(
              map(convocatoria => {
                solicitud.tituloConvocatoria = convocatoria?.titulo;
                return solicitud;
              })
            ));
          }
          else {
            requestsConvocatoria.push(of(solicitud));
          }
        });
        return of(response).pipe(
          tap(() => merge(...requestsConvocatoria).subscribe())
        );
      }),
      switchMap((response) => {
        if (response.total === 0) {
          return of(response);
        }

        const solicitudes = response.items;
        const personaIdsSolicitantes = new Set<string>(
          solicitudes.filter(solicitud => !!solicitud.solicitante?.id).map((solicitud) => solicitud.solicitante.id)
        );

        if (personaIdsSolicitantes.size === 0) {
          return of(response);
        }

        return this.personaService.findAllByIdIn([...personaIdsSolicitantes]).pipe(
          map((result) => {
            const personas = result.items;

            solicitudes.forEach((solicitud) => {
              solicitud.solicitante = personas.find((persona) => solicitud.solicitante?.id === persona.id);

            });
            return response;
          }),
          catchError((error) => {
            this.processError(error);
            return of(response);
          })
        );

      }),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(solicitud => {
            if (!!!solicitud.solicitante?.id) {
              return this.solicitudService.findSolicitanteExterno(solicitud.id).pipe(
                map(solicitanteExterno => {
                  solicitud.nombreSolicitante = `${solicitanteExterno?.nombre ?? ''} ${solicitanteExterno?.apellidos ?? ''}`;
                  return solicitud;
                })
              );
            }

            solicitud.nombreSolicitante = `${solicitud.solicitante.nombre ?? ''} ${solicitud.solicitante.apellidos ?? ''}`;
            return of(solicitud);
          }),
          toArray(),
          map(() => {
            return response;
          })
        )
      ),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(solicitud => {
            return this.solicitudRrhhService.findMemoria(solicitud.id).pipe(
              map(solicitudRrhh => {
                solicitud.tituloTrabajo = solicitudRrhh?.tituloTrabajo;
                return solicitud;
              })
            );
          }),
          toArray(),
          map(() => {
            return response;
          })
        )
      )
    );
  }

  protected initColumns(): void {
    this.columnas = [
      'codigoRegistroInterno',
      'solicitante',
      'solicitudRrhh.tituloTrabajo',
      'convocatoria.titulo',
      'estado.estado',
      'estado.fechaEstado',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.solicitudes$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    return new RSQLSgiRestFilter('pendiente', SgiRestFilterOperator.EQUALS, controls.pendientes.value?.toString());
  }

  protected resetFilters(): void {
    this.formGroup.reset();
    this.formGroup.controls.pendientes.setValue(true);
  }

}
