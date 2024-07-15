import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { sortGrupoEquipoByPersonaNombre, sortGrupoEquipoByRolProyectoOrden } from '@core/models/csp/grupo-equipo';
import { sortProyectoEquipoByPersonaNombre, sortProyectoEquipoByRolProyectoOrden } from '@core/models/csp/proyecto-equipo';
import { IRelacionEjecucionEconomica, TIPO_ENTIDAD_MAP, TipoEntidad } from '@core/models/csp/relacion-ejecucion-economica';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { IPersona } from '@core/models/sgp/persona';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { ConfigService as ConfigCspService } from '@core/services/csp/configuracion/config.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RelacionEjecucionEconomicaService } from '@core/services/csp/relacion-ejecucion-economica/relacion-ejecucion-economica.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { EMPTY, Observable, Subscription, from, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, toArray } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_ROUTE_NAMES } from '../ejecucion-economica-route-names';
import { IRelacionEjecucionEconomicaWithResponsables } from '../ejecucion-economica.action.service';
import { IRequerimientoJustificacionListadoModalData, RequerimientoJustificacionListadoExportModalComponent } from '../modals/requerimiento-justificacion-listado-export-modal/requerimiento-justificacion-listado-export-modal.component';

@Component({
  selector: 'sgi-ejecucion-economica-listado',
  templateUrl: './ejecucion-economica-listado.component.html',
  styleUrls: ['./ejecucion-economica-listado.component.scss']
})
export class EjecucionEconomicaListadoComponent extends AbstractTablePaginationComponent<IRelacionEjecucionEconomicaWithResponsables>
  implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  dataSource$: Observable<IRelacionEjecucionEconomicaWithResponsables[]>;

  private subscriptions: Subscription[] = [];

  busquedaAvanzada = false;

  colectivosResponsable: string[];
  tipoEntidadSelected: TipoEntidad;
  columnasTipoEntidadGrupo: string[];
  columnasTipoEntidadProyecto: string[];

  private idsProyectoSge: string[];

  private limiteRegistrosExportacionExcel: string;

  get TIPO_ENTIDAD_MAP() {
    return TIPO_ENTIDAD_MAP;
  }

  get TipoEntidad() {
    return TipoEntidad;
  }

  get EJECUCION_ECONOMICA_ROUTE_NAMES() {
    return EJECUCION_ECONOMICA_ROUTE_NAMES;
  }

  constructor(
    private readonly logger: NGXLogger,
    private rolProyectoService: RolProyectoService,
    private proyectoService: ProyectoService,
    private personaService: PersonaService,
    private relacionEjecucionEconomicaService: RelacionEjecucionEconomicaService,
    private grupoService: GrupoService,
    private readonly matDialog: MatDialog,
    private readonly cnfService: ConfigService,
    private readonly configCspService: ConfigCspService
  ) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.createFormGroup();
    this.loadColectivos();

    this.subscriptions.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-ejecucion-economica-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IRelacionEjecucionEconomicaWithResponsables>> {
    let relaciones$: Observable<SgiRestListResult<IRelacionEjecucionEconomica>>;

    switch (this.tipoEntidadSelected) {
      case TipoEntidad.GRUPO:
        relaciones$ = this.relacionEjecucionEconomicaService.findRelacionesGrupos(this.getFindOptions(reset));
        break;
      case TipoEntidad.PROYECTO:
        relaciones$ = this.relacionEjecucionEconomicaService.findRelacionesProyectos(this.getFindOptions(reset));
        break;
      default:
        throw Error(`Invalid tipoEntidad "${this.tipoEntidadSelected}"`);
    }

    this.idsProyectoSge = [];

    return relaciones$.pipe(
      map(result => result as SgiRestListResult<IRelacionEjecucionEconomicaWithResponsables>),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(relacion => {
            let responsables$: Observable<IPersona[]>;

            switch (relacion.tipoEntidad) {
              case TipoEntidad.GRUPO:
                responsables$ = this.grupoService.findInvestigadoresPrincipales(relacion.id).pipe(
                  switchMap(responsables => {
                    if (!responsables?.length) {
                      return of([]);
                    }

                    return this.personaService.findAllByIdIn(responsables.map(responsable => responsable.persona.id)).pipe(
                      map(personas => {
                        responsables.forEach(responsable => {
                          responsable.persona = personas.items.find(persona => persona.id === responsable.persona.id);
                        })

                        responsables.sort((a, b) => {
                          return sortGrupoEquipoByRolProyectoOrden(a, b)
                            || sortGrupoEquipoByPersonaNombre(a, b);
                        });

                        return responsables.map(responsable => responsable.persona)
                      }),
                      catchError((error) => {
                        this.logger.error(error);
                        return EMPTY;
                      })
                    )
                  })
                );

                break;
              case TipoEntidad.PROYECTO:
                responsables$ = this.proyectoService.findInvestigadoresPrincipales(relacion.id).pipe(
                  switchMap(responsables => {
                    if (!responsables?.length) {
                      return of([]);
                    }

                    return this.personaService.findAllByIdIn(responsables.map(responsable => responsable.persona.id)).pipe(
                      map(personas => {
                        responsables.forEach(responsable => {
                          responsable.persona = personas.items.find(persona => persona.id === responsable.persona.id);
                        })

                        responsables.sort((a, b) => {
                          return sortProyectoEquipoByRolProyectoOrden(a, b)
                            || sortProyectoEquipoByPersonaNombre(a, b);
                        });

                        return responsables.map(responsable => responsable.persona)
                      }),
                      catchError((error) => {
                        this.logger.error(error);
                        return EMPTY;
                      })
                    )
                  })
                );

                break;
              default:
                throw Error(`Invalid tipoEntidad "${relacion.tipoEntidad}"`);
            }

            return responsables$.pipe(
              map(responsables => {
                relacion.responsables = responsables;
                return relacion;
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
    this.columnasTipoEntidadGrupo = [
      'id',
      'proyectoSgeRef',
      'nombre',
      'responsable',
      'fechaInicio',
      'fechaFin',
      'acciones'
    ];

    this.columnasTipoEntidadProyecto = [
      'id',
      'proyectoSgeRef',
      'nombre',
      'codigoExterno',
      'codigoInterno',
      'responsable',
      'fechaInicio',
      'fechaFin',
      'acciones'
    ];

    this.columnas = this.columnasTipoEntidadProyecto;
  }

  protected loadTable(reset?: boolean): void {
    this.dataSource$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    this.tipoEntidadSelected = this.formGroup.controls.tipoEntidad?.value;
    this.columnas = this.tipoEntidadSelected === TipoEntidad.PROYECTO ? this.columnasTipoEntidadProyecto : this.columnasTipoEntidadGrupo;

    const restFilter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value)
      .and('proyectoSgeRef', SgiRestFilterOperator.EQUALS, controls.identificadorSge.value)
      .and('fechaInicio',
        SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
      .and('fechaInicio',
        SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value))
      .and('fechaFin',
        SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
      .and('fechaFin',
        SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value));

    if (this.tipoEntidadSelected === TipoEntidad.PROYECTO) {
      restFilter
        .and('codigoExterno', SgiRestFilterOperator.EQUALS, controls.referenciaEntidadConvocante.value);
    }

    if (this.busquedaAvanzada) {
      if (this.tipoEntidadSelected === TipoEntidad.PROYECTO) {
        restFilter
          .and('proyecto.convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
          .and('entidadConvocante', SgiRestFilterOperator.EQUALS, controls.entidadConvocante.value?.id?.toString())
          .and('entidadFinanciadora', SgiRestFilterOperator.EQUALS, controls.entidadFinanciadora.value?.id?.toString());
      }

      restFilter
        .and('responsable', SgiRestFilterOperator.EQUALS, controls.responsable.value?.id);
    }

    return restFilter;
  }

  toggleBusquedaAvanzada(): void {
    this.busquedaAvanzada = !this.busquedaAvanzada;
  }

  protected resetFilters(): void {
    this.initFormGroup(true);
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  private createFormGroup(): void {
    this.formGroup = new FormGroup({
      tipoEntidad: new FormControl(null),
      nombre: new FormControl(null),
      identificadorSge: new FormControl(null),
      referenciaEntidadConvocante: new FormControl(null),
      fechaInicioDesde: new FormControl(null),
      fechaInicioHasta: new FormControl(null),
      fechaFinDesde: new FormControl(null),
      fechaFinHasta: new FormControl(null),
      convocatoria: new FormControl(null),
      entidadConvocante: new FormControl(null),
      entidadFinanciadora: new FormControl(null),
      responsable: new FormControl({ value: null, disabled: true })
    });

    this.initFormGroup();

    this.suscripciones.push(
      this.configCspService.isEjecucionEconomicaGruposEnabled().subscribe(enabled => {
        if (enabled) {
          this.formGroup.controls.tipoEntidad.enable();
        } else {
          this.formGroup.controls.tipoEntidad.disable();
        }
      })
    );
  }

  private initFormGroup(reset = false): void {
    if (reset) {
      this.formGroup.reset();
    }

    this.formGroup.controls.tipoEntidad.setValue(TipoEntidad.PROYECTO);

    if (!this.colectivosResponsable || this.colectivosResponsable.length === 0) {
      this.formGroup.controls.responsable.disable();
    } else {
      this.formGroup.controls.responsable.enable();
    }

    this.tipoEntidadSelected = this.formGroup.controls.tipoEntidad?.value;
  }

  /**
   * Carga las listas de colectivos para hacer la busqueda de responsables de proyecto y activa el campo en el buscador.
   */
  private loadColectivos() {
    const queryOptionsResponsable: SgiRestFindOptions = {};
    queryOptionsResponsable.filter = new RSQLSgiRestFilter('rolPrincipal', SgiRestFilterOperator.EQUALS, 'true');
    this.subscriptions.push(
      this.rolProyectoService.findAll(queryOptionsResponsable).subscribe(
        (response) => {
          response.items.forEach((rolProyecto: IRolProyecto) => {
            this.rolProyectoService.findAllColectivos(rolProyecto.id).subscribe(
              (res) => {
                this.colectivosResponsable = res.items;
                this.formGroup.controls.responsable.enable();
              }
            );
          });
        },
        (error) => this.logger.error(error)
      )
    );
  }

  openExportModal(): void {
    const data: IRequerimientoJustificacionListadoModalData = {
      findOptions: this.findOptions,
      idsProyectoSge: this.idsProyectoSge,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(RequerimientoJustificacionListadoExportModalComponent, config);
  }

}
