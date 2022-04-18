import { AfterViewInit, Directive, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import {
  RSQLSgiRestSort,
  SgiRestFilter,
  SgiRestFindOptions,
  SgiRestListResult,
  SgiRestSortDirection
} from '@sgi/framework/http';
import { merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class AbstractTablePaginationComponent<T> implements OnInit, OnDestroy, AfterViewInit {
  columnas: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter;
  suscripciones: Subscription[];
  formGroup: FormGroup;
  findOptions: SgiRestFindOptions;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  protected constructor(
    protected readonly snackBarService: SnackBarService,
    protected readonly msgError: string
  ) {
    this.elementosPagina = [5, 10, 25, 100];
  }

  ngOnInit(): void {
    this.totalElementos = 0;
    this.suscripciones = [];
    this.initColumns();
  }

  ngOnDestroy(): void {
    this.suscripciones.forEach(x => x.unsubscribe());
  }

  ngAfterViewInit(): void {
    // Merge events that trigger load table data
    merge(
      // Link pageChange event to fire new request
      this.paginator?.page,
      // Link sortChange event to fire new request
      this.sort?.sortChange
    ).pipe(
      tap(() => {
        // Load table
        this.loadTable();
      }),
      catchError(err => {
        return err;
      })
    ).subscribe();
    // First load
    this.onSearch();
  }

  /**
   * Load table data
   */
  onSearch(): void {
    this.filter = this.createFilter();
    this.loadTable(true);
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    FormGroupUtil.clean(this.formGroup);
    this.filter = undefined;
    this.loadTable(true);
  }

  /**
   * Devuelve el observable con la petición del listado paginado
   *
   * @param reset Inidica si reinicializa la paginación
   */
  protected getObservableLoadTable(reset?: boolean): Observable<T[]> {
    // Do the request with paginator/sort/filter values
    const observable$ = this.createObservable(reset);
    return observable$?.pipe(
      map((response: SgiRestListResult<T>) => {
        // Map respose total
        this.totalElementos = response.total;
        // Reset pagination to first page
        if (reset && this.paginator) {
          this.paginator.pageIndex = 0;
        }
        // Return the values
        return response.items;
      }),
      catchError((error) => {
        // On error reset pagination values
        this.paginator?.firstPage();
        this.totalElementos = 0;
        this.showMensajeErrorLoadTable();
        return of([]);
      })
    );
  }

  /**
   * Crea las opciones para el listado que devuelve el servidor.
   * Hay que añadirlo al método del servicio que llamamos
   *
   * @param reset Indica la pagina actual es la primera o no
   */
  protected getFindOptions(reset?: boolean): SgiRestFindOptions {
    const options: SgiRestFindOptions = {
      page: {
        index: reset ? 0 : this.paginator?.pageIndex,
        size: this.paginator?.pageSize,
      },
      sort: new RSQLSgiRestSort(this.sort?.active, SgiRestSortDirection.fromSortDirection(this.sort?.direction)),
      filter: this.filter,
    };
    this.findOptions = options;
    return options;
  }

  /**
   * Muestra un mensaje de error si se produce un error al cargar los datos de la tabla
   */
  protected showMensajeErrorLoadTable(): void {
    this.snackBarService.showError(this.msgError);
  }

  /**
   * Crea la petición al servidor para cargar los datos de la tabla
   */
  protected abstract createObservable(reset?: boolean): Observable<SgiRestListResult<T>>;

  /**
   * Crea e indica el orden las columnas de la tabla
   */
  protected abstract initColumns(): void;

  /**
   * Carga los datos de la tabla
   *
   * @param reset Indica si reinicializa la paginación
   */
  protected abstract loadTable(reset?: boolean): void;

  /**
   * Crea los filtros para el listado
   */
  protected abstract createFilter(): SgiRestFilter;
}
