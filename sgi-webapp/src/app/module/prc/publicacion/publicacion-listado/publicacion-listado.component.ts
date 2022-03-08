import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { TIPO_ESTADO_PRODUCCION_MAP } from '@core/models/prc/estado-produccion-cientifica';
import { IPublicacion, TIPO_PRODUCCION_MAP } from '@core/models/prc/publicacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { PublicacionService } from '@core/services/prc/publicacion/publicacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { SgiRestListResult, SgiRestFilter, RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';

const MSG_ERROR = marker('error.load');

@Component({
  selector: 'sgi-publicacion-listado',
  templateUrl: './publicacion-listado.component.html',
  styleUrls: ['./publicacion-listado.component.scss']
})
export class PublicacionListadoComponent extends AbstractTablePaginationComponent<IPublicacion> implements OnInit {
  TIPO_COLECTIVO = TipoColectivo;
  fxLayoutProperties: FxLayoutProperties;

  publicaciones$: Observable<IPublicacion[]>;

  get TIPO_ESTADO_PRODUCCION_MAP() {
    return TIPO_ESTADO_PRODUCCION_MAP;
  }

  get TIPO_PRODUCCION_MAP() {
    return TIPO_PRODUCCION_MAP;
  }

  constructor(
    protected readonly snackBarService: SnackBarService,
    private readonly publicacionService: PublicacionService
  ) {
    super(snackBarService, MSG_ERROR);
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
      estado: new FormControl(null)
    });
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IPublicacion>> {
    return this.publicacionService.findAll(this.getFindOptions(reset));
  }

  protected initColumns(): void {
    this.columnas = ['fechaPublicacion', 'tituloPublicacion', 'tipoProduccion', 'estado', 'acciones'];
  }

  protected loadTable(reset?: boolean): void {
    this.publicaciones$ = this.getObservableLoadTable(reset);
  }
  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    return new RSQLSgiRestFilter('investigador', SgiRestFilterOperator.LIKE_ICASE, controls.investigador.value?.id)
      .and('grupoInvestigacion', SgiRestFilterOperator.EQUALS, controls.grupoInvestigacion.value?.id?.toString())
      .and('isbn', SgiRestFilterOperator.LIKE_ICASE, controls.isbn.value)
      .and('tipoProduccion', SgiRestFilterOperator.LIKE_ICASE, controls.tipoProduccion.value)
      .and('tituloPublicacion', SgiRestFilterOperator.LIKE_ICASE, controls.tituloPublicacion.value)
      .and('fechaPublicacionDesde', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionDesde.value))
      .and('fechaPublicacionHasta', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaPublicacionHasta.value))
      .and('estado.estado', SgiRestFilterOperator.EQUALS, controls.estado.value);
  }

  onClearFilters() {
    super.onClearFilters();
    this.formGroup.controls.fechaPublicacionDesde.setValue(null);
    this.formGroup.controls.fechaPublicacionHasta.setValue(null);

    this.onSearch();
  }

  private initFlexProperties(): void {
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '1%';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }
}
