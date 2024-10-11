import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ESTADO_MAP, Estado } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipos-configuracion';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { ProgramaService } from '@core/services/csp/programa.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico/tipo-ambito-geografico.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, Observable, Subscription, from, merge, of } from 'rxjs';
import { catchError, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';
import { CONVOCATORIA_ACTION_LINK_KEY } from '../../convocatoria/convocatoria.action.service';
import { SOLICITUD_ACTION_LINK_KEY } from '../../solicitud/solicitud.action.service';
import { ProyectoListadoExportModalComponent } from '../modals/proyecto-listado-export-modal/proyecto-listado-export-modal.component';

const MSG_ERROR = marker('error.load');
const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const PROYECTO_KEY = marker('csp.proyecto');

export interface IProyectoListadoData extends IProyecto {
  prorrogado: boolean;
  proyectosSGE: string;
}

@Component({
  selector: 'sgi-proyecto-listado',
  templateUrl: './proyecto-listado.component.html',
  styleUrls: ['./proyecto-listado.component.scss']
})
export class ProyectoListadoComponent extends AbstractTablePaginationComponent<IProyectoListadoData> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  textoCrear = MSG_BUTTON_NEW;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  proyecto$: Observable<IProyectoListadoData[]>;

  colectivosResponsableProyecto: string[];
  colectivosMiembroEquipo: string[];

  get Estado() {
    return Estado;
  }

  busquedaAvanzada = false;

  private subscriptions: Subscription[] = [];

  ambitoGeografico$: BehaviorSubject<ITipoAmbitoGeografico[]> = new BehaviorSubject<ITipoAmbitoGeografico[]>([]);

  private convocatoriaId: number;
  private solicitudId: number;

  mapModificable: Map<number, boolean> = new Map();

  private limiteRegistrosExportacionExcel: string;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get fechaActual() {
    return DateTime.now();
  }

  constructor(
    private readonly logger: NGXLogger,
    protected readonly snackBarService: SnackBarService,
    private readonly proyectoService: ProyectoService,
    private readonly dialogService: DialogService,
    public authService: SgiAuthService,
    private tipoAmbitoGeograficoService: TipoAmbitoGeograficoService,
    private programaService: ProgramaService,
    private rolProyectoService: RolProyectoService,
    private readonly translate: TranslateService,
    private convocatoriaService: ConvocatoriaService,
    private matDialog: MatDialog,
    route: ActivatedRoute,
    private readonly cnfService: ConfigService
  ) {
    super();
    if (route.snapshot.queryParamMap.get(CONVOCATORIA_ACTION_LINK_KEY)) {
      this.convocatoriaId = Number(route.snapshot.queryParamMap.get(CONVOCATORIA_ACTION_LINK_KEY));
    }
    if (route.snapshot.queryParamMap.get(SOLICITUD_ACTION_LINK_KEY)) {
      this.solicitudId = Number(route.snapshot.queryParamMap.get(SOLICITUD_ACTION_LINK_KEY));
    }
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.loadForm();

    if (this.convocatoriaId) {
      this.convocatoriaService.findById(this.convocatoriaId).pipe(
        map((convocatoria) => {
          this.formGroup.controls.convocatoria.patchValue(convocatoria);
          this.onSearch();
        }),
        catchError((err) => {
          this.logger.error(err);
          this.processError(err);
          return of({} as IConvocatoria);
        })
      ).subscribe();
    }
    if (this.solicitudId) {
      this.onSearch();
    }

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-proyecto-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  private loadForm() {
    this.formGroup = new FormGroup({
      titulo: new FormControl(''),
      id: new FormControl(''),
      acronimo: new FormControl(''),
      estado: new FormControl(''),
      activo: new FormControl('true'),
      unidadGestion: new FormControl('', [IsEntityValidator.isValid()]),
      fechaInicioDesde: new FormControl(),
      fechaInicioHasta: new FormControl(),
      fechaFinDesde: new FormControl(),
      fechaFinHasta: new FormControl(),
      ambitoGeografico: new FormControl(''),
      codigoSge: new FormControl(''),
      responsableProyecto: new FormControl({ value: '', disabled: true }),
      miembroEquipo: new FormControl({ value: '', disabled: true }),
      socioColaborador: new FormControl(''),
      convocatoria: new FormControl(undefined),
      entidadConvocante: new FormControl(''),
      planInvestigacion: new FormControl(''),
      entidadFinanciadora: new FormControl(''),
      fuenteFinanciacion: new FormControl(''),
      codigoExterno: new FormControl(''),
      codigoInterno: new FormControl(''),
      finalizado: new FormControl(''),
      prorrogado: new FormControl(''),
      palabrasClave: new FormControl(null),
      modeloEjecucion: new FormControl(null),
      finalidad: new FormControl(null),
      rolUniversidad: new FormControl(null)
    });
    this.loadAmbitoGeografico();
    this.loadColectivos();
    this.filter = this.createFilter();
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_NEW,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessReactivar = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.activo.setValue('true');
    this.formGroup.controls.fechaInicioDesde.setValue(null);
    this.formGroup.controls.fechaInicioHasta.setValue(null);
    this.formGroup.controls.fechaFinDesde.setValue(null);
    this.formGroup.controls.fechaFinHasta.setValue(null);
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IProyectoListadoData>> {
    let observable$: Observable<SgiRestListResult<IProyecto>> = null;
    if (this.authService.hasAuthorityForAnyUO('CSP-PRO-R')) {
      observable$ = this.proyectoService.findTodos(this.getFindOptions(reset));
    } else {
      observable$ = this.proyectoService.findAll(this.getFindOptions(reset));
    }
    return observable$.pipe(
      map((response) => {
        return response as SgiRestListResult<IProyectoListadoData>;
      }),
      switchMap((response) => {
        const requestsProyecto: Observable<IProyectoListadoData>[] = [];
        response.items.forEach(proyecto => {
          const proyectoData = proyecto as IProyectoListadoData;
          if (proyecto.id) {
            requestsProyecto.push(this.proyectoService.hasProyectoProrrogas(proyecto.id).pipe(
              map(value => {
                proyectoData.prorrogado = value;
                return proyectoData;
              }),
              switchMap(() =>
                this.proyectoService.findAllProyectosSgeProyecto(proyecto.id).pipe(
                  map(value => {
                    proyectoData.proyectosSGE = value.items.map(element => element.proyectoSge.id).join(', ');
                    return proyectoData;
                  }))
              )

            ));
          } else {
            requestsProyecto.push(of(proyectoData));
          }

          if (this.authService.hasAnyAuthorityForAnyUO(['CSP-PRO-E', 'CSP-PRO-V'])) {
            this.suscripciones.push(this.proyectoService.modificable(proyecto.id).subscribe((value) => {
              this.mapModificable.set(proyecto.id, value);
            }));
          }
        });
        return of(response).pipe(
          tap(() => merge(...requestsProyecto).subscribe())
        );
      })
    );

  }

  protected initColumns(): void {
    if (this.authService.hasAuthorityForAnyUO('CSP-PRO-R')) {
      this.columnas = ['id', 'codigoSGE', 'titulo', 'codigoExterno', 'codigoInterno', 'fechaInicio', 'fechaFin', 'fechaFinDefinitiva', 'finalizado', 'prorrogado', 'estado', 'activo', 'acciones'];
    } else {
      this.columnas = ['id', 'codigoSGE', 'titulo', 'codigoExterno', 'codigoInterno', 'fechaInicio', 'fechaFin', 'fechaFinDefinitiva', 'finalizado', 'prorrogado', 'estado', 'acciones'];
    }
  }

  protected loadTable(reset?: boolean): void {
    this.proyecto$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    const filter = new RSQLSgiRestFilter('id', SgiRestFilterOperator.EQUALS, controls.id.value)
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('estado.estado', SgiRestFilterOperator.EQUALS, controls.estado.value)
      .and('codigoExterno', SgiRestFilterOperator.LIKE_ICASE, controls.codigoExterno.value)
      .and('codigoInterno', SgiRestFilterOperator.LIKE_ICASE, controls.codigoInterno.value);
    if (controls.activo.value !== 'todos') {
      filter.and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value);
    }
    filter
      .and('acronimo', SgiRestFilterOperator.LIKE_ICASE, controls.acronimo.value)
      .and('unidadGestionRef', SgiRestFilterOperator.EQUALS, controls.unidadGestion.value?.id?.toString())
      .and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
      .and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value))
      .and(
        new RSQLSgiRestFilter('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
          .or('fechaFinDefinitiva', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
      )
      .and(
        new RSQLSgiRestFilter('fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value))
          .or('fechaFinDefinitiva', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value))
      )
      .and('ambitoGeografico.id', SgiRestFilterOperator.EQUALS, controls.ambitoGeografico.value?.id?.toString())
      .and('identificadoresSge.proyectoSgeRef', SgiRestFilterOperator.EQUALS, controls.codigoSge.value?.toString())
      .and('responsableProyecto', SgiRestFilterOperator.EQUALS, controls.responsableProyecto.value?.id)
      .and('equipo.personaRef', SgiRestFilterOperator.EQUALS, controls.miembroEquipo.value?.id)
      .and('socios.empresaRef', SgiRestFilterOperator.EQUALS, controls.socioColaborador.value?.id)
      .and('convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
      .and('entidadesConvocantes.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadConvocante.value?.id)
      .and('planInvestigacion', SgiRestFilterOperator.EQUALS, controls.planInvestigacion.value?.id?.toString())
      .and('entidadesFinanciadoras.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadFinanciadora.value?.id)
      .and('entidadesFinanciadoras.fuenteFinanciacion.id', SgiRestFilterOperator.EQUALS, controls.fuenteFinanciacion.value?.id?.toString())
      .and('finalizado', SgiRestFilterOperator.EQUALS, controls.finalizado.value?.toString())
      .and('prorrogado', SgiRestFilterOperator.EQUALS, controls.prorrogado.value?.toString())
      .and('solicitudId', SgiRestFilterOperator.EQUALS, this.solicitudId?.toString())
      .and('modeloEjecucion.id', SgiRestFilterOperator.EQUALS, controls.modeloEjecucion.value?.id?.toString())
      .and('finalidad.id', SgiRestFilterOperator.EQUALS, controls.finalidad.value?.id?.toString())
      .and('rolUniversidadId', SgiRestFilterOperator.EQUALS, controls.rolUniversidad.value?.id?.toString());

    const palabrasClave = controls.palabrasClave.value as string[];
    if (Array.isArray(palabrasClave) && palabrasClave.length > 0) {
      filter.and(this.createPalabrasClaveFilter(palabrasClave));
    }

    return filter;
  }

  private createPalabrasClaveFilter(palabrasClave: string[]): SgiRestFilter {
    let palabrasClaveFilter: SgiRestFilter;
    palabrasClave.forEach(palabraClave => {
      if (palabrasClaveFilter) {
        palabrasClaveFilter.or('palabrasClave.palabraClaveRef', SgiRestFilterOperator.LIKE_ICASE, palabraClave);
      } else {
        palabrasClaveFilter = new RSQLSgiRestFilter('palabrasClave.palabraClaveRef', SgiRestFilterOperator.LIKE_ICASE, palabraClave);
      }
    });
    return palabrasClaveFilter;
  }

  /**
   * Desactivar proyecto
   * @param proyecto proyecto
   */
  deactivateProyecto(proyecto: IProyecto): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.proyectoService.desactivar(proyecto.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessDesactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            this.processError(error);
          }
          else {
            this.processError(new SgiError(this.textoErrorDesactivar));
          }
        }
      );
    this.suscripciones.push(subcription);
  }

  /**
   * Activamos una proyecto
   * @param proyecto proyecto
   */
  activateProyecto(proyecto: IProyecto): void {
    const suscription = this.dialogService.showConfirmation(this.textoReactivar).pipe(
      switchMap((accept) => {
        if (accept) {
          proyecto.activo = true;
          return this.proyectoService.reactivar(proyecto.id);
        } else {
          return of();
        }
      })).subscribe(
        () => {
          this.snackBarService.showSuccess(this.textoSuccessReactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          proyecto.activo = false;
          if (error instanceof SgiError) {
            this.processError(error);
          }
          else {
            this.processError(new SgiError(this.textoErrorReactivar));
          }
        }
      );
    this.suscripciones.push(suscription);
  }

  /**
   * Mostrar busqueda avanzada
   */
  toggleBusquedaAvanzada(): void {
    this.busquedaAvanzada = !this.busquedaAvanzada;
  }

  /**
   * Cargar ámbitos geográficos
   */
  private loadAmbitoGeografico() {
    this.suscripciones.push(
      this.tipoAmbitoGeograficoService.findTodos().subscribe(
        (res) => this.ambitoGeografico$.next(res.items),
        (error) => this.logger.error(error)
      )
    );
  }

  /**
   * Carga las listas de colectivos para hacer la busqueda de responsables de proyecto y miembros de equipo
   * y activa ambos campos en el buscador.
   */
  private loadColectivos() {
    this.subscriptions.push(
      this.getColectivosRolesProyecto(false).subscribe(
        (colectivos) => {
          this.colectivosMiembroEquipo = colectivos;
          this.formGroup.controls.miembroEquipo.enable();
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );

    this.subscriptions.push(
      this.getColectivosRolesProyecto(true).subscribe(
        (colectivos) => {
          this.colectivosResponsableProyecto = colectivos;
          this.formGroup.controls.responsableProyecto.enable();
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );
  }

  /**
   * Observable con todso los colectivos asociados a roles de proyecto con el flag rolPrincipal igual al indicado, sin repetidos.
   */
  private getColectivosRolesProyecto(rolPrincipal: boolean): Observable<string[]> {
    const queryOptions: SgiRestFindOptions = {};
    queryOptions.filter = new RSQLSgiRestFilter('rolPrincipal', SgiRestFilterOperator.EQUALS, rolPrincipal.toString());

    return this.rolProyectoService.findAll(queryOptions).pipe(
      switchMap(response => from(response.items).pipe(
        mergeMap(rolProyecto => this.rolProyectoService.findAllColectivos(rolProyecto.id).pipe(map(response => response.items))),
      )),
      toArray(),
      map(colectivos => [...new Set<string>([].concat(...colectivos))])
    );
  }

  openExportModal(): void {
    const data: IBaseExportModalData = {
      findOptions: this.findOptions,
      totalRegistrosExportacionExcel: this.totalElementos,
      limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
    };

    const config = {
      data
    };
    this.matDialog.open(ProyectoListadoExportModalComponent, config);
  }
}
