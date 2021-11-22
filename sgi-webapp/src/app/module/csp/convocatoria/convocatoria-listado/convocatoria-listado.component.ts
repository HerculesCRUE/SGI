import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, ESTADO_MAP, IConvocatoria } from '@core/models/csp/convocatoria';
import { IConvocatoriaEntidadConvocante } from '@core/models/csp/convocatoria-entidad-convocante';
import { IConvocatoriaEntidadFinanciadora } from '@core/models/csp/convocatoria-entidad-financiadora';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http/';
import { NGXLogger } from 'ngx-logger';
import { from, Observable, of } from 'rxjs';
import { map, mergeAll, mergeMap, switchMap } from 'rxjs/operators';
import { ConvocatoriaListadoModalComponent, IConvocatoriaListadoModalData } from '../modals/convocatoria-listado-modal/convocatoria-listado-modal.component';

const MSG_BUTTON_ADD = marker('btn.add.entity');
const MSG_ERROR_LOAD = marker('error.load');
const MSG_REACTIVE = marker('msg.csp.reactivate');
const MSG_SUCCESS_REACTIVE = marker('msg.reactivate.entity.success');
const MSG_SUCCESS_CLONED = marker('msg.cloned.entity.success');
const MSG_ERROR_REACTIVE = marker('error.reactivate.entity');
const MSG_ERROR_CLONING = marker('error.cloning.entity');
const MSG_DEACTIVATE = marker('msg.deactivate.entity');
const MSG_ERROR_DEACTIVATE = marker('error.csp.deactivate.entity');
const MSG_SUCCESS_DEACTIVATE = marker('msg.csp.deactivate.success');
const AREA_TENATICA_KEY = marker('csp.area-tematica');
const CONVOCATORIA_KEY = marker('csp.convocatoria');

export interface IConvocatoriaListado {
  convocatoria: IConvocatoria;
  fase: IConvocatoriaFase;
  entidadConvocante: IConvocatoriaEntidadConvocante;
  entidadConvocanteEmpresa: IEmpresa;
  entidadFinanciadora: IConvocatoriaEntidadFinanciadora;
  entidadFinanciadoraEmpresa: IEmpresa;
}

@Component({
  selector: 'sgi-convocatoria-listado',
  templateUrl: './convocatoria-listado.component.html',
  styleUrls: ['./convocatoria-listado.component.scss']
})
export class ConvocatoriaListadoComponent extends AbstractTablePaginationComponent<IConvocatoriaListado> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  convocatorias$: Observable<IConvocatoriaListado[]>;
  textoCrear: string;
  textoDesactivar: string;
  textoReactivar: string;
  textoErrorDesactivar: string;
  textoSuccessDesactivar: string;
  textoSuccessReactivar: string;
  textoErrorReactivar: string;
  private textErrorCloning: string;
  private textSuccessClonation: string;

  busquedaAvanzada = false;

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get Estado() {
    return Estado;
  }

  mapModificable: Map<number, boolean> = new Map();

  msgParamEntity = {};
  msgParamAreaTematicaEntity = {};

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    private convocatoriaService: ConvocatoriaService,
    private empresaService: EmpresaService,
    private dialogService: DialogService,
    public authService: SgiAuthService,
    private readonly translate: TranslateService,
    private router: Router,
    private matDialog: MatDialog,
    private activatedRoute: ActivatedRoute
  ) {
    super(snackBarService, MSG_ERROR_LOAD);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(18%-10px)';
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
      codigo: new FormControl(''),
      titulo: new FormControl(''),
      fechaPublicacionDesde: new FormControl(null),
      fechaPublicacionHasta: new FormControl(null),
      activo: new FormControl(true),
      unidadGestion: new FormControl(null),
      modeloEjecucion: new FormControl(null),
      abiertoPlazoPresentacionSolicitud: new FormControl(false),
      finalidad: new FormControl(null),
      ambitoGeografico: new FormControl(null),
      estado: new FormControl(null),
      entidadConvocante: new FormControl(null),
      entidadFinanciadora: new FormControl(null),
      fuenteFinanciacion: new FormControl(null),
      areaTematica: new FormControl(null),
    });



    this.filter = this.createFilter();
  }

  private setupI18N(): void {

    this.translate.get(
      AREA_TENATICA_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAreaTematicaEntity = { entity: value });

    this.translate.get(
      CONVOCATORIA_KEY,
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
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoDesactivar = value);

    this.translate.get(
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorDesactivar = value);

    this.translate.get(
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_DEACTIVATE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoSuccessDesactivar = value);


    this.translate.get(
      CONVOCATORIA_KEY,
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
      CONVOCATORIA_KEY,
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
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_REACTIVE,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoErrorReactivar = value);

    this.translate.get(
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS_CLONED,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textSuccessClonation = value);

    this.translate.get(
      CONVOCATORIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR_CLONING,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textErrorCloning = value);
  }

  protected createObservable(): Observable<SgiRestListResult<IConvocatoriaListado>> {
    const observable$ = this.convocatoriaService.findAllTodosRestringidos(this.getFindOptions()).pipe(
      map(result => {
        const convocatorias = result.items.map((convocatoria) => {
          return {
            convocatoria,
            entidadConvocante: {} as IConvocatoriaEntidadConvocante,
            entidadConvocanteEmpresa: {} as IEmpresa,
            entidadFinanciadora: {} as IConvocatoriaEntidadFinanciadora,
            entidadFinanciadoraEmpresa: {} as IEmpresa,
            fase: {} as IConvocatoriaFase
          } as IConvocatoriaListado;
        });
        return {
          page: result.page,
          total: result.total,
          items: convocatorias
        } as SgiRestListResult<IConvocatoriaListado>;
      }),
      switchMap((result) => {
        return from(result.items).pipe(
          mergeMap(element => {
            if (this.authService.hasAnyAuthorityForAnyUO(['CSP-CON-E', 'CSP-CON-V'])) {
              return this.convocatoriaService.modificable(element.convocatoria.id).pipe(
                map((value) => {
                  this.mapModificable.set(element.convocatoria.id, value);
                  return element;
                })
              );
            }
            return of(element);
          }),
          map((convocatoriaListado) => {
            return this.convocatoriaService.findEntidadesFinanciadoras(convocatoriaListado.convocatoria.id).pipe(
              map(entidadFinanciadora => {
                if (entidadFinanciadora.items.length > 0) {
                  const fuenteFinanciacionFilter = this.formGroup.get('fuenteFinanciacion').value;
                  const entidadFinanciadoraFilter = this.formGroup.get('entidadFinanciadora').value;
                  if (entidadFinanciadoraFilter) {
                    entidadFinanciadora.items = entidadFinanciadora.items
                      .filter(entidadF => entidadF.empresa.id === entidadFinanciadoraFilter.id);
                  } else if (fuenteFinanciacionFilter) {
                    entidadFinanciadora.items = entidadFinanciadora.items
                      .filter(entidadF => entidadF.fuenteFinanciacion.id === fuenteFinanciacionFilter.id);
                  }
                  convocatoriaListado.entidadFinanciadora = entidadFinanciadora.items[0];
                }
                return convocatoriaListado;
              }),
              switchMap(() => {
                if (convocatoriaListado.entidadFinanciadora.id) {
                  return this.empresaService.findById(convocatoriaListado.entidadFinanciadora.empresa.id).pipe(
                    map(empresa => {
                      convocatoriaListado.entidadFinanciadoraEmpresa = empresa;
                      return convocatoriaListado;
                    }),
                  );
                }
                return of(convocatoriaListado);
              }),
              switchMap(() => {
                return this.convocatoriaService.findAllConvocatoriaFases(convocatoriaListado.convocatoria.id).pipe(
                  map(convocatoriaFase => {
                    if (convocatoriaFase.items.length > 0) {
                      convocatoriaListado.fase = convocatoriaFase.items[0];
                    }
                    return convocatoriaListado;
                  })
                );
              }),
              switchMap(() => {
                return this.convocatoriaService.findAllConvocatoriaEntidadConvocantes(convocatoriaListado.convocatoria.id).pipe(
                  map(convocatoriaEntidadConvocante => {
                    const entidadConvocanteFilter = this.formGroup.get('entidadConvocante').value;

                    if (convocatoriaEntidadConvocante.items.length > 0) {
                      if (entidadConvocanteFilter) {
                        convocatoriaEntidadConvocante.items = convocatoriaEntidadConvocante.items
                          .filter(entidadC => entidadC.entidad.id === entidadConvocanteFilter.id);
                      }
                      convocatoriaListado.entidadConvocante = convocatoriaEntidadConvocante.items[0];
                    }
                    return convocatoriaListado;
                  }),
                  switchMap(() => {
                    if (convocatoriaListado.entidadConvocante.id) {
                      return this.empresaService.findById(convocatoriaListado.entidadConvocante.entidad.id).pipe(
                        map(empresa => {
                          convocatoriaListado.entidadConvocanteEmpresa = empresa;
                          return convocatoriaListado;
                        }),
                      );
                    }
                    return of(convocatoriaListado);
                  }),
                );
              })
            );
          }),
          mergeAll(),
          map(() => result)
        );
      }),
    );

    return observable$;
  }

  protected initColumns(): void {
    if (this.authService.hasAuthorityForAnyUO('CSP-CON-R')) {
      this.columnas = [
        'titulo', 'codigo', 'fechaInicioSolicitud', 'fechaFinSolicitud',
        'entidadConvocante', 'planInvestigacion', 'entidadFinanciadora',
        'fuenteFinanciacion', 'estado', 'activo', 'acciones'
      ];
    } else {
      this.columnas = [
        'titulo', 'codigo', 'fechaInicioSolicitud', 'fechaFinSolicitud',
        'entidadConvocante', 'planInvestigacion', 'entidadFinanciadora',
        'fuenteFinanciacion', 'estado', 'acciones'
      ];
    }
  }

  protected loadTable(reset?: boolean): void {
    this.convocatorias$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('codigo', SgiRestFilterOperator.LIKE_ICASE, controls.codigo.value);

    filter
      .and('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.titulo.value)
      .and('estado', SgiRestFilterOperator.EQUALS, controls.estado.value)
      .and('activo', SgiRestFilterOperator.EQUALS, controls.activo.value?.toString())
      .and('fechaPublicacion', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionDesde.value))
      .and('fechaPublicacion', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionHasta.value))
      .and('unidadGestionRef', SgiRestFilterOperator.EQUALS, controls.unidadGestion.value?.id?.toString())
      .and('modeloEjecucion.id', SgiRestFilterOperator.EQUALS, controls.modeloEjecucion.value?.id?.toString())
      .and('abiertoPlazoPresentacionSolicitud', SgiRestFilterOperator.EQUALS, controls.abiertoPlazoPresentacionSolicitud.value?.toString())
      .and('finalidad.id', SgiRestFilterOperator.EQUALS, controls.finalidad.value?.id?.toString())
      .and('ambitoGeografico.id', SgiRestFilterOperator.EQUALS, controls.ambitoGeografico.value?.id?.toString())
      .and('entidadesConvocantes.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadConvocante.value?.id)
      .and('entidadesFinanciadoras.entidadRef', SgiRestFilterOperator.EQUALS, controls.entidadFinanciadora.value?.id)
      .and('entidadesFinanciadoras.fuenteFinanciacion.id', SgiRestFilterOperator.EQUALS, controls.fuenteFinanciacion.value?.id?.toString())
      .and('areasTematicas.areaTematica.id', SgiRestFilterOperator.EQUALS, controls.areaTematica.value?.id?.toString());

    return filter;
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.activo.setValue(true);
    this.formGroup.controls.abiertoPlazoPresentacionSolicitud.setValue(false);
    this.formGroup.controls.fechaPublicacionDesde.setValue(null);
    this.formGroup.controls.fechaPublicacionHasta.setValue(null);
    this.onSearch();
  }

  /**
   * Mostrar busqueda avanzada
   */
  toggleBusquedaAvanzada() {
    this.busquedaAvanzada = !this.busquedaAvanzada;
  }

  /**
   * Desactivar convocatoria
   * @param convocatoria convocatoria
   */
  deactivateConvocatoria(convocatoria: IConvocatoriaListado): void {
    const subcription = this.dialogService.showConfirmation(this.textoDesactivar).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.convocatoriaService.desactivar(convocatoria.convocatoria.id);
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
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorDesactivar);
          }
        }
      );
    this.suscripciones.push(subcription);
  }

  /**
   * Activamos una convocatoria
   * @param convocatoria convocatoria
   */
  activeConvocatoria(convocatoria: IConvocatoriaListado): void {
    const suscription = this.dialogService.showConfirmation(this.textoReactivar).pipe(
      switchMap((accept) => {
        if (accept) {
          convocatoria.convocatoria.activo = true;
          return this.convocatoriaService.reactivar(convocatoria.convocatoria.id);
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
          convocatoria.convocatoria.activo = false;
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textoErrorReactivar);
          }
        }
      );
    this.suscripciones.push(suscription);
  }

  hasAuthorityDelete(unidadGestionId: number): boolean {
    return this.authService.hasAuthorityForAnyUO('CSP-CON-B') ||
      this.authService.hasAuthorityForAnyUO('CSP-CON-B_' + unidadGestionId);
  }

  clone(convocatoriaToCloneId: number): void {
    this.suscripciones.push(
      this.convocatoriaService.clone(convocatoriaToCloneId)
        .subscribe((id: number) => {
          this.snackBarService.showSuccess(this.textSuccessClonation);
          this.router.navigate([`../${id}`], { relativeTo: this.activatedRoute });
        }, (error) => {
          this.logger.error(error);
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(this.textErrorCloning);
          }
        }));
  }

  openExportModal(): void {
    const data: IConvocatoriaListadoModalData = {
      findOptions: this.findOptions
    };

    const config = {
      data
    };
    this.matDialog.open(ConvocatoriaListadoModalComponent, config);
  }

}
