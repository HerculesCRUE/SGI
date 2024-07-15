import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IBaseExportModalData } from '@core/component/base-export/base-export-modal-data';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { IGrupo } from '@core/models/csp/grupo';
import { TIPO_MAP } from '@core/models/csp/grupo-tipo';
import { ILineaInvestigacion } from '@core/models/csp/linea-investigacion';
import { IPersona } from '@core/models/sgp/persona';
import { ROUTE_NAMES } from '@core/route.names';
import { ConfigService } from '@core/services/cnf/config.service';
import { ConfigService as ConfigCspService } from '@core/services/csp/configuracion/config.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { LineaInvestigacionService } from '@core/services/csp/linea-investigacion/linea-investigacion.service';
import { RolProyectoColectivoService } from '@core/services/csp/rol-proyecto-colectivo/rol-proyecto-colectivo.service';
import { DialogService } from '@core/services/dialog.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject, EMPTY, Observable, from } from 'rxjs';
import { catchError, filter, map, mergeMap, switchMap, tap, toArray } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../../csp-route-names';
import { GrupoListadoExportModalComponent } from '../modals/grupo-listado-export-modal/grupo-listado-export-modal.component';

const MSG_BUTTON_ADD = marker('btn.add.entity');
const MSG_ERROR_DELETE = marker('error.delete.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const GRUPO_KEY = marker('csp.grupo');

interface IGrupoListado extends IGrupo {
  investigadoresPrincipales: IPersona[];
}

@Component({
  selector: 'sgi-grupo-listado',
  templateUrl: './grupo-listado.component.html',
  styleUrls: ['./grupo-listado.component.scss']
})
export class GrupoListadoComponent extends AbstractTablePaginationComponent<IGrupoListado> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;
  CSP_ROUTE_NAMES = CSP_ROUTE_NAMES;

  grupos$: Observable<IGrupoListado[]>;
  colectivosBusqueda: string[];
  isInvestigador: boolean;
  isVisor: boolean;

  textoCrear: string;
  textoErrorDelete: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;

  busquedaAvanzada = false;
  readonly lineasInvestigacion$ = new BehaviorSubject<ILineaInvestigacion[]>([]);

  private limiteRegistrosExportacionExcel: string;
  private _isEjecucionEconomicaGruposEnabled: boolean;

  get TIPO_MAP() {
    return TIPO_MAP;
  }

  get isEjecucionEconomicaGruposEnabled(): boolean {
    return this._isEjecucionEconomicaGruposEnabled ?? false;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    private grupoService: GrupoService,
    private personaService: PersonaService,
    private dialogService: DialogService,
    public authService: SgiAuthService,
    private rolProyectoColectivoService: RolProyectoColectivoService,
    private readonly translate: TranslateService,
    private lineaInvestigacionService: LineaInvestigacionService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService,
    private readonly configCspService: ConfigCspService
  ) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.buildFormGroup();
    this.loadColectivosBusqueda();
    this.getLineasInvestigacion();
    this.filter = this.createFilter();
    this.isInvestigador = this.authService.hasAnyAuthority(['CSP-AUT-INV-VR']);
    this.isVisor = this.authService.hasAnyAuthority(['CSP-GIN-V']);

    this.suscripciones.push(
      this.configCspService.isEjecucionEconomicaGruposEnabled().subscribe(value => {
        this._isEjecucionEconomicaGruposEnabled = value;
      })
    );

    this.suscripciones.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-grupo-listado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IGrupoListado>> {
    return this.grupoService.findTodos(this.getFindOptions(reset)).pipe(
      map(result => {
        return {
          page: result.page,
          total: result.total,
          items: result.items.map((grupo) => grupo as IGrupoListado)
        } as SgiRestListResult<IGrupoListado>;
      }),
      switchMap(response =>
        from(response.items).pipe(
          mergeMap(grupo => this.fillInvestigadorPrincipal(grupo)),
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
      'nombre',
      'investigadorPrincipal',
      'codigo',
      'fechaInicio',
      'fechaFin',
      'tipo',
      'activo',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.grupos$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const rsqlFilter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value)
      .and('codigo', SgiRestFilterOperator.LIKE_ICASE, controls.codigo.value)
      .and('miembrosEquipo.personaRef', SgiRestFilterOperator.EQUALS, controls.miembroEquipo.value?.id)
      .and('proyectoSgeRef', SgiRestFilterOperator.EQUALS, controls.proyectoSgeRef.value)
      .and(
        'lineasInvestigacion.id',
        SgiRestFilterOperator.EQUALS,
        controls.lineaInvestigacion.value?.id ? controls.lineaInvestigacion.value?.id.toString() : null
      );
    if (controls.activo.value !== 'todos') {
      rsqlFilter.and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value);
    }
    rsqlFilter.and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
      .and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value));

    const palabrasClave = controls.palabrasClave.value as string[];
    if (Array.isArray(palabrasClave) && palabrasClave.length > 0) {
      rsqlFilter.and(this.createPalabrasClaveFilter(palabrasClave));
    }

    return rsqlFilter;
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

  activate(grupo: IGrupoListado): void {
    const subcription = this.dialogService.showConfirmation(this.textoReactivar).pipe(
      filter(accept => !!accept),
      switchMap(() => this.grupoService.activar(grupo.id))
    ).subscribe(
      () => {
        this.snackBarService.showSuccess(this.textoSuccessReactivar);
        this.loadTable();
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          this.processError(error);
        } else {
          this.processError(new SgiError(this.textoErrorReactivar));
        }
      }
    );
    this.suscripciones.push(subcription);
  }

  deactivate(grupo: IGrupoListado): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar).pipe(
      filter(accept => !!accept),
      switchMap(() => this.grupoService.desactivar(grupo.id))
    ).subscribe(
      () => {
        this.snackBarService.showSuccess(this.textoSuccessDesactivar);
        this.loadTable();
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof SgiError) {
          this.processError(error);
        } else {
          this.processError(new SgiError(this.textoErrorDesactivar));
        }
      }
    );
    this.suscripciones.push(subcription);
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.buildFormGroup();
  }

  private buildFormGroup() {
    this.formGroup = new FormGroup({
      nombre: new FormControl(null),
      codigo: new FormControl(null),
      miembroEquipo: new FormControl(null),
      proyectoSgeRef: new FormControl(null),
      activo: new FormControl('true'),
      fechaInicioDesde: new FormControl(),
      fechaInicioHasta: new FormControl(),
      palabrasClave: new FormControl(null),
      lineaInvestigacion: new FormControl(null)
    });
  }

  private fillInvestigadorPrincipal(grupo: IGrupoListado): Observable<IGrupoListado> {
    let idsInvestigadoresPrincipales: string[];
    return this.grupoService.findInvestigadoresPrincipales(grupo.id).pipe(
      filter(investigadoresPrincipales => !!investigadoresPrincipales),
      map(investigadoresPrincipales => investigadoresPrincipales.map(investigador => investigador.persona.id)),
      tap(investigadoresPrincipales => idsInvestigadoresPrincipales = [...investigadoresPrincipales]),
      switchMap(investigadoresPrincipales => this.personaService.findAllByIdIn(investigadoresPrincipales)),
      map(investigadoresPrincipales => {
        grupo.investigadoresPrincipales = investigadoresPrincipales.items;
        if (grupo.investigadoresPrincipales.length < idsInvestigadoresPrincipales.length) {
          grupo.investigadoresPrincipales.push(
            ...idsInvestigadoresPrincipales
              .filter(id => grupo.investigadoresPrincipales.map(i => i.id).includes(id))
              .map(id => {
                return { id } as IPersona;
              })
          )
        }

        return grupo;
      }),
      catchError((error) => {
        this.logger.error(error);
        this.processError(error);
        return EMPTY;
      })
    );
  }

  private loadColectivosBusqueda(): void {
    this.suscripciones.push(
      this.rolProyectoColectivoService.findColectivosActivos().subscribe(
        (colectivos) => {
          this.colectivosBusqueda = colectivos;
        },
        (error) => this.logger.error(error)
      )
    );
  }

  private setupI18N(): void {

    this.translate.get(
      GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_ADD,
          { entity: value }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorDelete = value);

    this.translate.get(
      GRUPO_KEY,
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
      GRUPO_KEY,
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
      GRUPO_KEY,
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
      GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoReactivar = value);

    this.translate.get(
      GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);

    this.translate.get(
      GRUPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoSuccessReactivar = value);

  }

  /**
   * Mostrar busqueda avanzada
   */
  toggleBusquedaAvanzada(): void {
    this.busquedaAvanzada = !this.busquedaAvanzada;
  }

  getLineasInvestigacion() {
    const lineasInvestigacionSubscription = this.lineaInvestigacionService.findTodos().subscribe(
      (response) => {
        this.lineasInvestigacion$.next(response.items);
      },
      (error) => this.logger.error(error)
    );
    this.suscripciones.push(lineasInvestigacionSubscription);
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
    this.matDialog.open(GrupoListadoExportModalComponent, config);
  }

}
