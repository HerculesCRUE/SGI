import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { IFuenteFinanciacion } from '@core/models/csp/fuente-financiacion';
import { IPrograma } from '@core/models/csp/programa';
import { IProyecto } from '@core/models/csp/proyecto';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { ITipoAmbitoGeografico } from '@core/models/csp/tipo-ambito-geografico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { IUnidadGestion } from '@core/models/usr/unidad-gestion';
import { ROUTE_NAMES } from '@core/route.names';
import { FuenteFinanciacionService } from '@core/services/csp/fuente-financiacion/fuente-financiacion.service';
import { ProgramaService } from '@core/services/csp/programa.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { TipoAmbitoGeograficoService } from '@core/services/csp/tipo-ambito-geografico.service';
import { UnidadGestionService } from '@core/services/csp/unidad-gestion.service';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, Subscription } from 'rxjs';
import { map, startWith, switchMap, tap } from 'rxjs/operators';

const MSG_ERROR = marker('error.load');
const MSG_BUTTON_NEW = marker('btn.add.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const PROYECTO_KEY = marker('csp.proyecto');

interface IProyectoData extends IProyecto {
  prorrogado: boolean;
}

@Component({
  selector: 'sgi-proyecto-listado',
  templateUrl: './proyecto-listado.component.html',
  styleUrls: ['./proyecto-listado.component.scss']
})
export class ProyectoListadoComponent extends AbstractTablePaginationComponent<IProyectoData> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  textoCrear = MSG_BUTTON_NEW;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  proyecto$: Observable<IProyectoData[]>;

  colectivosResponsableProyecto: string[];
  colectivosMiembroEquipo: string[];

  get Estado() {
    return Estado;
  }

  busquedaAvanzada = false;

  private subscriptions: Subscription[] = [];

  private unidadGestionFiltered: IUnidadGestion[] = [];
  unidadesGestion$: Observable<IUnidadGestion[]>;

  private ambitoGeograficoFiltered: ITipoAmbitoGeografico[] = [];
  ambitoGeografico$: Observable<ITipoAmbitoGeografico[]>;

  private planInvestigacionFiltered: IPrograma[] = [];
  planInvestigacion$: Observable<IPrograma[]>;

  private fuenteFinanciacionFiltered: IFuenteFinanciacion[] = [];
  fuenteFinanciacion$: Observable<IFuenteFinanciacion[]>;

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
    private unidadGestionService: UnidadGestionService,
    private tipoAmbitoGeograficoService: TipoAmbitoGeograficoService,
    private programaService: ProgramaService,
    private fuenteFinanciacionService: FuenteFinanciacionService,
    private rolProyectoService: RolProyectoService,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, MSG_ERROR);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.formGroup = new FormGroup({
      titulo: new FormControl(''),
      acronimo: new FormControl(''),
      estado: new FormControl(''),
      activo: new FormControl('true'),
      unidadGestion: new FormControl('', [IsEntityValidator.isValid()]),
      fechaInicioDesde: new FormControl(),
      fechaInicioHasta: new FormControl(),
      fechaFinDesde: new FormControl(),
      fechaFinHasta: new FormControl(),
      ambitoGeografico: new FormControl(''),
      responsableProyecto: new FormControl({ value: '', disabled: true }),
      miembroEquipo: new FormControl({ value: '', disabled: true }),
      socioColaborador: new FormControl(''),
      convocatoria: new FormControl(''),
      entidadConvocante: new FormControl(''),
      planInvestigacion: new FormControl(''),
      entidadFinanciadora: new FormControl(''),
      fuenteFinanciacion: new FormControl(''),
      codigoExterno: new FormControl(''),
      finalizado: new FormControl(''),
      prorrogado: new FormControl('')
    });
    this.loadUnidadesGestion();
    this.loadAmbitoGeografico();
    this.loadPlanInvestigacion();
    this.loadFuenteFinanciacion();
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
          { entity: value }
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
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
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
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.activo.setValue('true');
    this.formGroup.controls.fechaInicioDesde.setValue(null);
    this.formGroup.controls.fechaInicioHasta.setValue(null);
    this.formGroup.controls.fechaFinDesde.setValue(null);
    this.formGroup.controls.fechaFinHasta.setValue(null);
    this.onSearch();
  }

  protected createObservable(): Observable<SgiRestListResult<IProyectoData>> {
    let observable$ = null;
    if (this.authService.hasAuthorityForAnyUO('CSP-PRO-R')) {
      observable$ = this.proyectoService.findTodos(this.getFindOptions()).pipe(
        map((response) => {
          return response as SgiRestListResult<IProyecto>;
        }),
        switchMap((response) => {
          const requestsProyecto: Observable<IProyectoData>[] = [];
          response.items.forEach(proyecto => {
            const proyectoData = proyecto as IProyectoData;
            if (proyecto.id) {
              requestsProyecto.push(this.proyectoService.hasProyectoProrrogas(proyecto.id).pipe(
                map(value => {
                  proyectoData.prorrogado = value;
                  return proyectoData;
                })
              ));
            } else {
              requestsProyecto.push(of(proyectoData));
            }
          });
          return of(response).pipe(
            tap(() => merge(...requestsProyecto).subscribe())
          );
        })
      );
    } else {
      observable$ = this.proyectoService.findAll(this.getFindOptions()).pipe(
        map((response) => {
          return response as SgiRestListResult<IProyecto>;
        }),
        switchMap((response) => {
          const requestsProyecto: Observable<IProyectoData>[] = [];
          response.items.forEach(proyecto => {
            const proyectoData = proyecto as IProyectoData;
            if (proyecto.id) {
              requestsProyecto.push(this.proyectoService.hasProyectoProrrogas(proyecto.id).pipe(
                map(value => {
                  proyectoData.prorrogado = value;
                  return proyectoData;
                })
              ));
            } else {
              requestsProyecto.push(of(proyectoData));
            }
          });
          return of(response).pipe(
            tap(() => merge(...requestsProyecto).subscribe())
          );
        })
      );
    }
    return observable$;
  }

  protected initColumns(): void {
    if (this.authService.hasAuthorityForAnyUO('CSP-PRO-R')) {
      this.columnas = ['titulo', 'acronimo', 'codigoExterno', 'fechaInicio', 'fechaFin', 'fechaFinDefinitiva', 'finalizado', 'prorrogado', 'estado', 'activo', 'acciones'];
    } else {
      this.columnas = ['titulo', 'acronimo', 'codigoExterno', 'fechaInicio', 'fechaFin', 'fechaFinDefinitiva', 'finalizado', 'prorrogado', 'estado', 'acciones'];
    }
  }

  protected loadTable(reset?: boolean): void {
    this.proyecto$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('acronimo', SgiRestFilterOperator.LIKE_ICASE, controls.acronimo.value)
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('estado.estado', SgiRestFilterOperator.EQUALS, controls.estado.value)
      .and('codigoExterno', SgiRestFilterOperator.LIKE_ICASE, controls.codigoExterno.value);
    if (controls.activo.value !== 'todos') {
      filter.and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value);
    }
    filter
      .and('unidadGestionRef', SgiRestFilterOperator.EQUALS, controls.unidadGestion.value?.acronimo)
      .and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
      .and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value))
      .and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
      .or('fechaFinDefinitiva', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
      .and('fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value))
      .or('fechaFinDefinitiva', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value))
      .and('ambitoGeografico.id', SgiRestFilterOperator.EQUALS, controls.ambitoGeografico.value?.id?.toString())
      .and('responsableProyecto', SgiRestFilterOperator.EQUALS, controls.responsableProyecto.value?.id)
      .and('equipos.personaRef', SgiRestFilterOperator.EQUALS, controls.miembroEquipo.value?.id)
      .and('socios.empresaRef', SgiRestFilterOperator.EQUALS, controls.socioColaborador.value?.id)
      .and('convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
      .and('entidadesConvocantes.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadConvocante.value?.id)
      .and('planInvestigacion', SgiRestFilterOperator.EQUALS, controls.planInvestigacion.value?.id?.toString())
      .and('entidadesFinanciadoras.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadFinanciadora.value?.id)
      .and('entidadesFinanciadoras.fuenteFinanciacion.id', SgiRestFilterOperator.EQUALS, controls.fuenteFinanciacion.value?.id?.toString())
      .and('finalizado', SgiRestFilterOperator.EQUALS, controls.finalizado.value?.toString())
      .and('prorrogado', SgiRestFilterOperator.EQUALS, controls.prorrogado.value?.toString());

    return filter;
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
          this.snackBarService.showError(this.textoErrorDesactivar);
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
          this.snackBarService.showSuccess(this.textoSuccessDesactivar);
          this.loadTable();
        },
        (error) => {
          this.logger.error(error);
          proyecto.activo = false;
          this.snackBarService.showError(this.textoErrorDesactivar);
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
   * Devuelve el nombre de una unidad de gestión.
   * @param unidadGestion unidad de gestión
   * @returns nombre de una unidad de gestión
   */
  getUnidadGestion(unidadGestion?: IUnidadGestion): string | undefined {
    return typeof unidadGestion === 'string' ? unidadGestion : unidadGestion?.nombre;
  }

  /**
   * Devuelve el nombre de una fuente de financiacion.
   * @param fuente de financiacion fuente de financiacion.
   * @returns nombre de una fuente de financiacion
   */
  getFuenteFinanciacion(fuente?: IFuenteFinanciacion): string | undefined {
    return typeof fuente === 'string' ? fuente : fuente?.nombre;
  }

  /**
   * Devuelve el nombre de un tipo de ámbito geográfico.
   * @param ambito de un ámbito geográfico
   * @returns nombre de un ámbito geográfico
   */
  getAmbitoGeografico(ambito?: ITipoAmbitoGeografico): string | undefined {
    return typeof ambito === 'string' ? ambito : ambito?.nombre;
  }

  /**
   * Devuelve el nombre de un plan de investigación.
   * @param plan de un plan de investigación.
   * @returns nombre de de un plan de investigación
   */
  getPlanInvestigacion(plan?: IFuenteFinanciacion): string | undefined {
    return typeof plan === 'string' ? plan : plan?.nombre;
  }

  /**
   * Filtra la lista devuelta por el servicio de Unidades de Gestión
   *
   * @param value del input para autocompletar
   */
  private filtroUnidadGestion(value: string): IUnidadGestion[] {
    const filterValue = value.toString().toLowerCase();
    return this.unidadGestionFiltered.filter(unidadGestion => unidadGestion.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Filtra la lista devuelta por el servicio de Tipos de ámbitos geográficos
   *
   * @param value del input para autocompletar
   */
  private filtroAmbitoGeografico(value: string): ITipoAmbitoGeografico[] {
    const filterValue = value.toString().toLowerCase();
    return this.ambitoGeograficoFiltered.filter(ambitoGeografico => ambitoGeografico.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Filtra la lista devuelta por el servicio de Planes e Investigación
   *
   * @param value del input para autocompletar
   */
  private filtroPlanInvestigacion(value: string): IPrograma[] {
    const filterValue = value.toString().toLowerCase();
    return this.planInvestigacionFiltered.filter(fuente => fuente.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Filtra la lista devuelta por el servicio de Fuentes de Financiación
   *
   * @param value del input para autocompletar
   */
  private filtroFuenteFinanciacion(value: string): IFuenteFinanciacion[] {
    const filterValue = value.toString().toLowerCase();
    return this.fuenteFinanciacionFiltered.filter(fuente => fuente.nombre.toLowerCase().includes(filterValue));
  }

  /**
   * Cargar unidad gestion
   */
  private loadUnidadesGestion() {
    this.subscriptions.push(
      // TODO Debería filtrar por el rol
      this.unidadGestionService.findAllRestringidos().subscribe(
        res => {
          this.unidadGestionFiltered = res.items;
          this.unidadesGestion$ = this.formGroup.controls.unidadGestion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroUnidadGestion(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );
  }

  /**
   * Cargar fuente financiacion
   */
  private loadFuenteFinanciacion() {
    this.suscripciones.push(
      this.fuenteFinanciacionService.findAll().subscribe(
        (res) => {
          this.fuenteFinanciacionFiltered = res.items;
          this.fuenteFinanciacion$ = this.formGroup.controls.fuenteFinanciacion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroFuenteFinanciacion(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );
  }

  /**
   * Cargar planes de investigación
   */
  private loadPlanInvestigacion() {
    this.suscripciones.push(
      this.programaService.findAllPlan().subscribe(
        (res) => {
          this.planInvestigacionFiltered = res.items;
          this.planInvestigacion$ = this.formGroup.controls.planInvestigacion.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroPlanInvestigacion(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );
  }

  /**
   * Cargar ámbitos geográficos
   */
  private loadAmbitoGeografico() {
    this.suscripciones.push(
      this.tipoAmbitoGeograficoService.findAll().subscribe(
        (res) => {
          this.ambitoGeograficoFiltered = res.items;
          this.ambitoGeografico$ = this.formGroup.controls.ambitoGeografico.valueChanges
            .pipe(
              startWith(''),
              map(value => this.filtroAmbitoGeografico(value))
            );
        },
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR);
        }
      )
    );
  }

  /**
   * Carga las listas de colectivos para hacer la busqueda de responsables de proyecto y miembros de equipo
   * y activa ambos campos en el buscador.
   */
  private loadColectivos() {
    const queryOptions: SgiRestFindOptions = {};
    queryOptions.filter = new RSQLSgiRestFilter('rolPrincipal', SgiRestFilterOperator.EQUALS, 'false')
    this.subscriptions.push(this.rolProyectoService.findAll(queryOptions).subscribe(
      (response) => {
        response.items.forEach((rolProyecto: IRolProyecto) => {
          this.rolProyectoService.findAllColectivos(rolProyecto.id).subscribe(
            (res) => {
              this.colectivosMiembroEquipo = res.items;
              this.formGroup.controls.miembroEquipo.enable();
            }
          );
        });
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR);
      }
    ));

    const queryOptionsResponsable: SgiRestFindOptions = {};
    queryOptionsResponsable.filter = new RSQLSgiRestFilter('rolPrincipal', SgiRestFilterOperator.EQUALS, 'true');
    this.rolProyectoService.findAll(queryOptionsResponsable).subscribe(
      (response) => {
        response.items.forEach((rolProyecto: IRolProyecto) => {
          this.rolProyectoService.findAllColectivos(rolProyecto.id).subscribe(
            (res) => {
              this.colectivosResponsableProyecto = res.items;
              this.formGroup.controls.responsableProyecto.enable();
            }
          );
        });
      },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(MSG_ERROR);
      }
    );
  }
}
