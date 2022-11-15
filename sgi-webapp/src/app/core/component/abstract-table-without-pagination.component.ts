import { AfterViewInit, Directive, EventEmitter, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { FormGroupUtil } from '@core/utils/form-group-util';
import {
  RSQLSgiRestSort,
  SgiRestFilter,
  SgiRestFindOptions,
  SgiRestListResult,
  SgiRestSortDirection
} from '@sgi/framework/http';
import { EMPTY, merge, Observable, of, Subscription } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class AbstractTableWithoutPaginationComponent<T> implements OnInit, OnDestroy, AfterViewInit {
  columnas: string[];
  elementosPagina: number[];
  totalElementos: number;
  filter: SgiRestFilter[];
  suscripciones: Subscription[];
  formGroup: FormGroup;

  @ViewChild(MatSort, { static: false }) sort: MatSort;

  @Output()
  readonly errors = new EventEmitter<Error[]>();
  // tslint:disable-next-line: variable-name
  private _errors: Error[] = [];

  public readonly processError: (error: Error) => void = (error: Error) => {
    this._errors = [...this._errors, error];
  }

  protected constructor() {
    this.elementosPagina = [5, 10, 25, 100];
  }

  ngOnInit(): void {
    this.totalElementos = 0;
    this.suscripciones = [];
    this.filter = [];
    this.initColumns();
  }

  ngOnDestroy(): void {
    this.suscripciones.forEach(x => x.unsubscribe());
  }

  ngAfterViewInit(): void {
    // Merge events that trigger load table data
    merge(
      // Link sortChange event to fire new request
      this.sort?.sortChange
    ).pipe(
      tap(() => {
        // Load table
        this.loadTable();
      }),
      catchError(err => {
        this.processError(err);
        this.emmitErrors();
        return EMPTY;
      })
    ).subscribe();
    // First load
    this.loadTable();
  }

  /**
   * Load table data
   */
  onSearch(): void {
    this.filter = this.createFilters();
    this.clearErrors();
    this.loadTable(true);
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    this.resetFilters();
    this.onSearch();
  }

  /**
   * Reset filters
   */
  protected resetFilters(): void {
    FormGroupUtil.clean(this.formGroup);
    this.filter = undefined;
  }

  /**
   * Devuelve el observable con la petición del listado paginado
   *
   * @param reset Inidica si reinicializa la paginación
   */
  protected getObservableLoadTable(reset?: boolean): Observable<T[]> {
    // Do the request with paginator/sort/filter values
    const observable$ = this.createObservable() ? of(void 0).pipe(
      tap(() => this.clearErrors()),
      switchMap(() => this.createObservable())
    ) : this.createObservable();
    return observable$?.pipe(
      map((response: SgiRestListResult<T>) => {
        // Map respose total
        this.totalElementos = response.total;
        // Reset pagination to first page
        // Return the values
        return response.items;
      }),
      catchError((error) => {
        // On error reset pagination values
        this.totalElementos = 0;
        this.processError(error);
        return of([]);
      }),
      tap(() => this.emmitErrors())
    );
  }

  /**
   * Crea las opciones para el listado que devuelve el servidor.
   * Hay que añadirlo al método del servicio que llamamos
   *
   * @param reset Indica la pagina actual es la primera o no
   */
  protected getFindOptions(reset?: boolean): SgiRestFindOptions {
    const options = {
      sort: new RSQLSgiRestSort(this.sort.active, SgiRestSortDirection.fromSortDirection(this.sort.direction)),
      filters: this.filter,
    };
    return options;
  }

  /**
   * Crea la petición al servidor para cargar los datos de la tabla
   */
  protected abstract createObservable(): Observable<SgiRestListResult<T>>;

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
  protected abstract createFilters(formGroup?: FormGroup): SgiRestFilter[];

  protected clearErrors(): void {
    this._errors = [];
    this.emmitErrors();
  }

  private emmitErrors(): void {
    this.errors.next(this._errors);
  }

}
