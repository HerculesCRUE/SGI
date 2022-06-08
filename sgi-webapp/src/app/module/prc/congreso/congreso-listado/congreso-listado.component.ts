import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { ICongreso, TIPO_EVENTO_MAP } from '@core/models/prc/congreso';
import { TipoEstadoProduccion, TIPO_ESTADO_PRODUCCION_MAP } from '@core/models/prc/estado-produccion-cientifica';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { CongresoService } from '@core/services/prc/congreso/congreso.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { IAuthStatus, SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ERROR = marker('error.load');

@Component({
  selector: 'sgi-congreso-listado',
  templateUrl: './congreso-listado.component.html',
  styleUrls: ['./congreso-listado.component.scss']
})
export class CongresoListadoComponent extends AbstractTablePaginationComponent<ICongreso> implements OnInit {
  TIPO_COLECTIVO = TipoColectivo;
  fxLayoutProperties: FxLayoutProperties;

  congresos$: Observable<ICongreso[]>;

  readonly TIPO_ESTADO_PRODUCCION_LIST;
  private readonly FILTER_ESTADO_INITIAL_VALUE = [TipoEstadoProduccion.PENDIENTE, TipoEstadoProduccion.VALIDADO_PARCIALMENTE];

  get TIPO_ESTADO_PRODUCCION_MAP() {
    return TIPO_ESTADO_PRODUCCION_MAP;
  }

  get TIPO_EVENTO_MAP() {
    return TIPO_EVENTO_MAP;
  }

  get authStatus$(): Observable<IAuthStatus> {
    return this.authService.authStatus$.asObservable();
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly authService: SgiAuthService,
    private readonly congresoService: CongresoService
  ) {
    super(snackBarService, MSG_ERROR);
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
      tipoEvento: new FormControl(null),
      nombreCongreso: new FormControl(''),
      fechaCelebracionDesde: new FormControl(null),
      fechaCelebracionHasta: new FormControl(null),
      estado: new FormControl(this.FILTER_ESTADO_INITIAL_VALUE)
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<ICongreso>> {
    return this.congresoService.findAll(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.columnas = ['fechaCelebracion', 'tituloTrabajo', 'tipoEvento', 'estado', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.congresos$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    const filter = new RSQLSgiRestFilter('investigador', SgiRestFilterOperator.LIKE_ICASE, controls.investigador.value?.id)
      .and('grupoInvestigacion', SgiRestFilterOperator.EQUALS, controls.grupoInvestigacion.value?.id?.toString())
      .and('tituloTrabajo', SgiRestFilterOperator.LIKE_ICASE, controls.tituloTrabajo.value)
      .and('tipoEvento', SgiRestFilterOperator.LIKE_ICASE, controls.tipoEvento.value)
      .and('nombreCongreso', SgiRestFilterOperator.LIKE_ICASE, controls.nombreCongreso.value)
      .and('fechaCelebracionDesde', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaCelebracionDesde.value))
      .and('fechaCelebracionHasta', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaCelebracionHasta.value));
    if (controls.estado.value.length > 0) {
      filter.and('estado.estado', SgiRestFilterOperator.IN, controls.estado.value);
    }
    return filter;
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaCelebracionDesde.setValue(null);
    this.formGroup.controls.fechaCelebracionHasta.setValue(null);
    this.formGroup.controls.estado.setValue(this.FILTER_ESTADO_INITIAL_VALUE);

    this.onSearch();
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }
}
