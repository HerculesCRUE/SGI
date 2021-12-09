import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of, Subscription } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { EJECUCION_ECONOMICA_ROUTE_NAMES } from '../ejecucion-economica-route-names';

const MSG_ERROR = marker('error.load');

@Component({
  selector: 'sgi-ejecucion-economica-listado',
  templateUrl: './ejecucion-economica-listado.component.html',
  styleUrls: ['./ejecucion-economica-listado.component.scss']
})
export class EjecucionEconomicaListadoComponent extends AbstractTablePaginationComponent<IProyectoProyectoSge>
  implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  dataSource$: Observable<IProyectoProyectoSge[]>;

  private subscriptions: Subscription[] = [];

  busquedaAvanzada = false;

  colectivosResponsableProyecto: string[];

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get Estado() {
    return Estado;
  }

  get EJECUCION_ECONOMICA_ROUTE_NAMES() {
    return EJECUCION_ECONOMICA_ROUTE_NAMES;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    private proyectoProyectoSgeService: ProyectoProyectoSgeService,
    private rolProyectoService: RolProyectoService,
    private proyectoService: ProyectoService,
  ) {
    super(snackBarService, MSG_ERROR);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(17%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.formGroup = new FormGroup({
      acronimo: new FormControl(undefined),
      tituloProyecto: new FormControl(undefined),
      identificadorSge: new FormControl(undefined),
      fechaInicioDesde: new FormControl(null),
      fechaInicioHasta: new FormControl(null),
      fechaFinDesde: new FormControl(null),
      fechaFinHasta: new FormControl(null),
      convocatoria: new FormControl(undefined),
      responsableProyecto: new FormControl({ value: '', disabled: true }),
      estadoProyecto: new FormControl(null)
    });

    this.loadColectivos();
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IProyectoProyectoSge>> {
    const observable$ = this.proyectoProyectoSgeService.findAll(this.getFindOptions(reset)).pipe(
      switchMap(response => {
        const requestsProyecto: Observable<IProyectoProyectoSge>[] = [];
        response.items.forEach(ejecucionEconomicaListado => {
          requestsProyecto.push(this.proyectoService.findById(ejecucionEconomicaListado.proyecto.id).pipe(
            map(proyecto => {
              ejecucionEconomicaListado.proyecto = proyecto;
              return ejecucionEconomicaListado;
            })
          ));
        });
        return of(response).pipe(
          tap(() => merge(...requestsProyecto).subscribe())
        );
      }),
    );

    return observable$;
  }

  protected initColumns(): void {
    this.columnas = [
      'proyecto.titulo',
      'proyecto.acronimo',
      'proyecto.fechaInicio',
      'proyecto.fechaFin',
      'proyecto.estado.estado',
      'proyectoSgeRef',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.dataSource$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const filter = new RSQLSgiRestFilter('proyecto.titulo', SgiRestFilterOperator.LIKE_ICASE, controls.tituloProyecto.value)
      .and('proyectoSgeRef', SgiRestFilterOperator.EQUALS, controls.identificadorSge.value)
      .and('proyecto.fechaInicio',
        SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
      .and('proyecto.fechaInicio',
        SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value))
      .and('proyecto.fechaFin',
        SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
      .and('proyecto.fechaFin',
        SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value));

    if (this.busquedaAvanzada) {
      filter
        .and('proyecto.convocatoria.id', SgiRestFilterOperator.EQUALS, controls.convocatoria.value?.id?.toString())
        .and('responsableProyecto', SgiRestFilterOperator.EQUALS, controls.responsableProyecto.value?.id)
        .and('proyecto.estado.estado', SgiRestFilterOperator.EQUALS, controls.estadoProyecto.value)
        .and('proyecto.acronimo', SgiRestFilterOperator.LIKE_ICASE, controls.acronimo.value);
    }

    return filter;
  }

  toggleBusquedaAvanzada(): void {
    this.busquedaAvanzada = !this.busquedaAvanzada;
  }

  onClearFilters(): void {
    super.onClearFilters();
    this.formGroup.controls.fechaInicioDesde.setValue(null);
    this.formGroup.controls.fechaInicioHasta.setValue(null);
    this.formGroup.controls.fechaFinDesde.setValue(null);
    this.formGroup.controls.fechaFinHasta.setValue(null);
  }

  ngOnDestroy(): void {
    this.subscriptions?.forEach(x => x.unsubscribe());
  }

  /**
   * Carga las listas de colectivos para hacer la busqueda de responsables de proyecto y activa el campo en el buscador.
   */
  private loadColectivos() {
    const queryOptionsResponsable: SgiRestFindOptions = {};
    queryOptionsResponsable.filter = new RSQLSgiRestFilter('rolPrincipal', SgiRestFilterOperator.EQUALS, 'true');
    this.subscriptions.push(
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
      )
    );
  }

}
