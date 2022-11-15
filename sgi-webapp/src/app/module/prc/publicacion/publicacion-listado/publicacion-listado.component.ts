import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { TipoEstadoProduccion, TIPO_ESTADO_PRODUCCION_MAP } from '@core/models/prc/estado-produccion-cientifica';
import { IPublicacion, TIPO_PRODUCCION_MAP } from '@core/models/prc/publicacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Module } from '@core/module';
import { LayoutService } from '@core/services/layout.service';
import { PublicacionService } from '@core/services/prc/publicacion/publicacion.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { IAuthStatus, SgiAuthService } from '@sgi/framework/auth';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

@Component({
  selector: 'sgi-publicacion-listado',
  templateUrl: './publicacion-listado.component.html',
  styleUrls: ['./publicacion-listado.component.scss']
})
export class PublicacionListadoComponent extends AbstractTablePaginationComponent<IPublicacion> implements OnInit {
  TIPO_COLECTIVO = TipoColectivo;
  fxLayoutProperties: FxLayoutProperties;

  publicaciones$: Observable<IPublicacion[]>;

  readonly TIPO_ESTADO_PRODUCCION_LIST;
  private readonly FILTER_ESTADO_INITIAL_VALUE = [TipoEstadoProduccion.PENDIENTE, TipoEstadoProduccion.VALIDADO_PARCIALMENTE];

  get TIPO_ESTADO_PRODUCCION_MAP() {
    return TIPO_ESTADO_PRODUCCION_MAP;
  }

  get TIPO_PRODUCCION_MAP() {
    return TIPO_PRODUCCION_MAP;
  }

  get authStatus$(): Observable<IAuthStatus> {
    return this.authService.authStatus$.asObservable();
  }

  get isModuleINV(): boolean {
    return this.layoutService.activeModule$.value === Module.INV;
  }

  constructor(
    private readonly authService: SgiAuthService,
    private readonly publicacionService: PublicacionService,
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
      isbn: new FormControl(''),
      tipoProduccion: new FormControl(null),
      tituloPublicacion: new FormControl(''),
      fechaPublicacionDesde: new FormControl(null),
      fechaPublicacionHasta: new FormControl(null),
      estado: new FormControl(this.FILTER_ESTADO_INITIAL_VALUE)
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IPublicacion>> {
    return this.isModuleINV
      ? this.publicacionService.findPublicacionesInvestigador(this.getFindOptions(reset))
      : this.publicacionService.findAll(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.columnas = ['fechaPublicacion', 'tituloPublicacion', 'tipoProduccion', 'estado', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.publicaciones$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    const filter = new RSQLSgiRestFilter('investigador', SgiRestFilterOperator.LIKE_ICASE, controls.investigador.value?.id)
      .and('grupoInvestigacion', SgiRestFilterOperator.EQUALS, controls.grupoInvestigacion.value?.id?.toString())
      .and('isbn', SgiRestFilterOperator.LIKE_ICASE, controls.isbn.value)
      .and('tipoProduccion', SgiRestFilterOperator.LIKE_ICASE, controls.tipoProduccion.value)
      .and('tituloPublicacion', SgiRestFilterOperator.LIKE_ICASE, controls.tituloPublicacion.value)
      .and('fechaPublicacionDesde', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionDesde.value))
      .and('fechaPublicacionHasta', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionHasta.value));
    if (controls.estado.value.length > 0) {
      filter.and('estado.estado', SgiRestFilterOperator.IN, controls.estado.value);
    }
    return filter;
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.fechaPublicacionDesde.setValue(null);
    this.formGroup.controls.fechaPublicacionHasta.setValue(null);
    this.formGroup.controls.estado.setValue(this.FILTER_ESTADO_INITIAL_VALUE);
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }
}
