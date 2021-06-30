import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { ProyectoSgeService } from '@core/services/sge/proyecto-sge.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

const MSG_LISTADO_ERROR = marker('error.load');

export interface SearchProyectoEconomicoModalData {
  selectedProyectos: IProyectoSge[];
}

interface ProyectoListado {
  proyecto: IProyectoSge;
  selected: boolean;
}

@Component({
  templateUrl: './search-proyectos-economicos-modal.component.html',
  styleUrls: ['./search-proyectos-economicos-modal.component.scss']
})
export class SearchProyectosEconomicosModalComponent implements OnInit, AfterViewInit {
  formGroup: FormGroup;

  proyectos$: Observable<ProyectoListado[]>;

  displayedColumns = ['id', 'titulo', 'fechaInicio', 'fechaFin', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  constructor(
    private readonly logger: NGXLogger,
    public dialogRef: MatDialogRef<SearchProyectosEconomicosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SearchProyectoEconomicoModalData,
    private proyectoService: ProyectoSgeService,
    protected snackBarService: SnackBarService
  ) {
  }

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      datosProyecto: new FormControl(''),
      fechaInicioDesde: new FormControl(null),
      fechaInicioHasta: new FormControl(null),
      fechaFinDesde: new FormControl(null),
      fechaFinHasta: new FormControl(null),

    });
  }

  ngAfterViewInit(): void {
    this.search(true);

    merge(
      this.paginator.page,
      this.sort.sortChange
    ).pipe(
      tap(() => this.search())
    ).subscribe();
  }

  closeModal(proyecto?: IProyectoSge): void {
    this.dialogRef.close(proyecto);
  }

  search(reset?: boolean) {
    this.proyectos$ = this.proyectoService
      .findAll(
        {
          page: {
            index: reset ? 0 : this.paginator.pageIndex,
            size: this.paginator.pageSize
          },
          sort: new RSQLSgiRestSort(this.sort?.active, SgiRestSortDirection.fromSortDirection(this.sort?.direction)),
          filter: this.buildFilter()
        }
      )
      .pipe(
        map((response) => {
          // Map response total
          this.totalElementos = response.total;
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }
          // Return the values
          return response.items.map(proyecto => {
            const proyectoListado: ProyectoListado = {
              proyecto,
              selected: this.data.selectedProyectos.some(selectedProyecto => selectedProyecto.id === proyecto.id)
            };
            return proyectoListado;
          });
        }),
        catchError((error) => {
          this.logger.error(error);
          // On error reset pagination values
          this.paginator.firstPage();
          this.totalElementos = 0;
          this.snackBarService.showError(MSG_LISTADO_ERROR);
          return of([]);
        })
      );
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    this.formGroup.reset();
    this.search(true);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  /**
   * Recupera el filtro resultante de componer los filtros del formulario
   *
   * @returns el filtro
   */
  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;
    const rsqlFilter =
      new RSQLSgiRestFilter('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
        .and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value))
        .and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
        .and('fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value))
        .and('id', SgiRestFilterOperator.LIKE_ICASE, controls.datosProyecto.value)
        .or('titulo', SgiRestFilterOperator.LIKE_ICASE, controls.datosProyecto.value)
        .and('fechaInicio', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioDesde.value))
        .and('fechaInicio', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaInicioHasta.value))
        .and('fechaFin', SgiRestFilterOperator.GREATHER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinDesde.value))
        .and('fechaFin', SgiRestFilterOperator.LOWER_OR_EQUAL, LuxonUtils.toBackend(controls.fechaFinHasta.value));

    return rsqlFilter;
  }

}
