import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { IDireccionTesis, TIPO_PROYECTO_MAP } from '@core/models/prc/direccion-tesis';
import { TipoEstadoProduccion, TIPO_ESTADO_PRODUCCION_MAP } from '@core/models/prc/estado-produccion-cientifica';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { DireccionTesisService } from '@core/services/prc/direccion-tesis/direccion-tesis.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ERROR = marker('error.load');

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

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly direccionTesisService: DireccionTesisService
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
      tipoProyecto: new FormControl(null),
      fechaDefensaDesde: new FormControl(null),
      fechaDefensaHasta: new FormControl(null),
      estado: new FormControl(this.FILTER_ESTADO_INITIAL_VALUE)
    });
  }


  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IDireccionTesis>> {
    return this.direccionTesisService.findAll(this.getFindOptions(reset));
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

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaDefensaDesde.setValue(null);
    this.formGroup.controls.fechaDefensaHasta.setValue(null);
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
