import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IDireccionTesis, TIPO_PROYECTO_MAP } from '@core/models/prc/direccion-tesis';
import { TipoEstadoProduccion, TIPO_ESTADO_PRODUCCION_MAP } from '@core/models/prc/estado-produccion-cientifica';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Module } from '@core/module';
import { LayoutService } from '@core/services/layout.service';
import { DireccionTesisService } from '@core/services/prc/direccion-tesis/direccion-tesis.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { IAuthStatus, SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

@Component({
  selector: 'sgi-tesis-tfm-tfg-listado',
  templateUrl: './tesis-tfm-tfg-listado.component.html',
  styleUrls: ['./tesis-tfm-tfg-listado.component.scss']
})
export class TesisTfmTfgListadoComponent extends AbstractTablePaginationComponent<IDireccionTesis> implements OnInit {

  TIPO_COLECTIVO = TipoColectivo;
  fxLayoutProperties: FxLayoutProperties;

  direccionesTesis$: Observable<IDireccionTesis[]>;

  readonly TIPO_ESTADO_PRODUCCION_LIST;
  private readonly FILTER_ESTADO_INITIAL_VALUE = [TipoEstadoProduccion.PENDIENTE, TipoEstadoProduccion.VALIDADO_PARCIALMENTE];

  get TIPO_ESTADO_PRODUCCION_MAP() {
    return TIPO_ESTADO_PRODUCCION_MAP;
  }

  get TIPO_PROYECTO_MAP() {
    return TIPO_PROYECTO_MAP;
  }

  get authStatus$(): Observable<IAuthStatus> {
    return this.authService.authStatus$.asObservable();
  }

  get isModuleINV(): boolean {
    return this.layoutService.activeModule$.value === Module.INV;
  }

  constructor(
    private readonly authService: SgiAuthService,
    private readonly direccionTesisService: DireccionTesisService,
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
      investigador: new FormControl(null),
      grupoInvestigacion: new FormControl(''),
      tituloTrabajo: new FormControl(''),
      tipoProyecto: new FormControl(null),
      fechaDefensaDesde: new FormControl(null),
      fechaDefensaHasta: new FormControl(null),
      estado: new FormControl(this.FILTER_ESTADO_INITIAL_VALUE)
    });
  }


  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IDireccionTesis>> {
    return this.isModuleINV
      ? this.direccionTesisService.findDireccionesTesisInvestigador(this.getFindOptions(reset))
      : this.direccionTesisService.findAll(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.columnas = ['fechaDefensa', 'tituloTrabajo', 'estado', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.direccionesTesis$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    const filter = new RSQLSgiRestFilter('investigador', SgiRestFilterOperator.LIKE_ICASE, controls.investigador.value?.id)
      .and('grupoInvestigacion', SgiRestFilterOperator.EQUALS, controls.grupoInvestigacion.value?.id?.toString())
      .and('tituloTrabajo', SgiRestFilterOperator.LIKE_ICASE, controls.tituloTrabajo.value)
      .and('tipoProyecto', SgiRestFilterOperator.LIKE_ICASE, controls.tipoProyecto.value)
      .and('fechaDefensaDesde', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaDefensaDesde.value))
      .and('fechaDefensaHasta', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaDefensaHasta.value));
    if (controls.estado.value.length > 0) {
      filter.and('estado.estado', SgiRestFilterOperator.IN, controls.estado.value);
    }
    return filter;
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaDefensaDesde.setValue(null);
    this.formGroup.controls.fechaDefensaHasta.setValue(null);
    this.formGroup.controls.estado.setValue(this.FILTER_ESTADO_INITIAL_VALUE);
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }
}
