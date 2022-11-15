import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IComiteEditorial } from '@core/models/prc/comite-editorial';
import { TipoEstadoProduccion, TIPO_ESTADO_PRODUCCION_MAP } from '@core/models/prc/estado-produccion-cientifica';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Module } from '@core/module';
import { LayoutService } from '@core/services/layout.service';
import { ComiteEditorialService } from '@core/services/prc/comite-editorial/comite-editorial.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { IAuthStatus, SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

@Component({
  selector: 'sgi-comite-editorial-listado',
  templateUrl: './comite-editorial-listado.component.html',
  styleUrls: ['./comite-editorial-listado.component.scss']
})
export class ComiteEditorialListadoComponent extends AbstractTablePaginationComponent<IComiteEditorial> implements OnInit {
  TIPO_COLECTIVO = TipoColectivo;
  fxLayoutProperties: FxLayoutProperties;

  comitesEditoriales$: Observable<IComiteEditorial[]>;

  readonly TIPO_ESTADO_PRODUCCION_LIST;
  private readonly FILTER_ESTADO_INITIAL_VALUE = [TipoEstadoProduccion.PENDIENTE, TipoEstadoProduccion.VALIDADO_PARCIALMENTE];

  get TIPO_ESTADO_PRODUCCION_MAP() {
    return TIPO_ESTADO_PRODUCCION_MAP;
  }

  get authStatus$(): Observable<IAuthStatus> {
    return this.authService.authStatus$.asObservable();
  }

  get isModuleINV(): boolean {
    return this.layoutService.activeModule$.value === Module.INV;
  }

  constructor(
    private readonly authService: SgiAuthService,
    private readonly comiteEditorialService: ComiteEditorialService,
    private readonly layoutService: LayoutService
  ) {
    super();
    this.TIPO_ESTADO_PRODUCCION_LIST = Object.values(TipoEstadoProduccion);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initFlexProperties();
    this.initFormGroup();
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      fechaInicioDesde: new FormControl(null),
      fechaInicioHasta: new FormControl(null),
      grupoInvestigacion: new FormControl(''),
      investigador: new FormControl(null),
      issn: new FormControl(''),
      nombre: new FormControl(''),
      estado: new FormControl(this.FILTER_ESTADO_INITIAL_VALUE)
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IComiteEditorial>> {
    return this.isModuleINV
      ? this.comiteEditorialService.findComitesEditorialesInvestigador(this.getFindOptions(reset))
      : this.comiteEditorialService.findAll(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.columnas = ['fechaInicio', 'nombre', 'estado', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.comitesEditoriales$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    const filter = new RSQLSgiRestFilter(
      'fechaInicioDesde', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
      .and('fechaInicioHasta', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value))
      .and('grupoInvestigacion', SgiRestFilterOperator.EQUALS, controls.grupoInvestigacion.value?.id?.toString())
      .and('investigador', SgiRestFilterOperator.LIKE_ICASE, controls.investigador.value?.id)
      .and('issn', SgiRestFilterOperator.LIKE_ICASE, controls.issn.value)
      .and('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.nombre.value);
    if (controls.estado.value.length > 0) {
      filter.and('estado.estado', SgiRestFilterOperator.IN, controls.estado.value);
    }
    return filter;
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaInicioDesde.setValue(null);
    this.formGroup.controls.fechaInicioHasta.setValue(null);
    this.formGroup.controls.estado.setValue(this.FILTER_ESTADO_INITIAL_VALUE);
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }
}
