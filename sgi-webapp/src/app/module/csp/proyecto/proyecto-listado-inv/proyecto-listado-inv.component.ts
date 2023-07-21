import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { AbstractTablePaginationComponent } from '@core/component/abstract-table-pagination.component';
import { Estado, ESTADO_MAP } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { RSQLSgiRestFilter, SgiRestFilter, SgiRestFilterOperator, SgiRestListResult } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { merge, Observable, of } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

export interface IProyectoListadoData extends IProyecto {
  prorrogado: boolean;
  proyectosSGE: string;
}

@Component({
  selector: 'sgi-proyecto-listado-inv',
  templateUrl: './proyecto-listado-inv.component.html',
  styleUrls: ['./proyecto-listado-inv.component.scss']
})
export class ProyectoListadoInvComponent extends AbstractTablePaginationComponent<IProyectoListadoData> implements OnInit {
  ROUTE_NAMES = ROUTE_NAMES;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  proyecto$: Observable<IProyectoListadoData[]>;

  get Estado() {
    return Estado;
  }

  mapModificable: Map<number, boolean> = new Map();

  get ESTADO_MAP() {
    return ESTADO_MAP;
  }

  get fechaActual() {
    return DateTime.now();
  }

  constructor(
    private readonly proyectoService: ProyectoService
  ) {
    super();
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
    this.formGroup = new FormGroup({
      aplicarFiltro: new FormControl(true),
    });
    this.filter = this.createFilter();
  }

  protected resetFilters(): void {
    super.resetFilters();
    this.formGroup.controls.aplicarFiltro.setValue('false');
  }

  protected createObservable(reset?: boolean): Observable<SgiRestListResult<IProyectoListadoData>> {
    return this.proyectoService.findAllInvestigador(this.getFindOptions(reset)).pipe(
      map((response) => {
        return response as SgiRestListResult<IProyectoListadoData>;
      }),
      switchMap((response) => {
        const requestsProyecto: Observable<IProyectoListadoData>[] = [];
        response.items.forEach(proyecto => {
          if (proyecto.id) {
            requestsProyecto.push(this.proyectoService.hasProyectoProrrogas(proyecto.id).pipe(
              map(value => {
                proyecto.prorrogado = value;
                return proyecto;
              }),
              switchMap(() =>
                this.proyectoService.findAllProyectosSgeProyecto(proyecto.id).pipe(
                  map(value => {
                    proyecto.proyectosSGE = value.items.map(element => element.proyectoSge.id).join(', ');
                    return proyecto;
                  }))
              )
            ));
          } else {
            requestsProyecto.push(of(proyecto));
          }

        });
        return of(response).pipe(
          tap(() => merge(...requestsProyecto).subscribe())
        );
      })
    );

  }

  protected initColumns(): void {
    this.columnas = [
      'id',
      'codigoSGE',
      'titulo',
      'codigoExterno',
      'codigoInterno',
      'fechaInicio',
      'fechaFin',
      'fechaFinDefinitiva',
      'finalizado',
      'prorrogado',
      'estado',
      'acciones'
    ];
  }

  protected loadTable(reset?: boolean): void {
    this.proyecto$ = this.getObservableLoadTable(reset);
  }

  protected createFilter(): SgiRestFilter {
    if (this.formGroup.controls.aplicarFiltro.value) {
      return new RSQLSgiRestFilter('participacionActual', SgiRestFilterOperator.EQUALS, 'true');
    }
    return undefined;
  }

}
