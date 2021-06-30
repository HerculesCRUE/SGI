import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

const MSG_LISTADO_ERROR = marker('error.load');

export interface SearchEmpresaModalData {
  selectedEmpresas: IEmpresa[];
}

interface EmpresaListado {
  empresa: IEmpresa;
  selected: boolean;
}

@Component({
  templateUrl: './search-empresa.component.html',
  styleUrls: ['./search-empresa.component.scss']
})
export class SearchEmpresaModalComponent implements OnInit, AfterViewInit {

  formGroup: FormGroup;

  displayedColumns = ['numeroIdentificacion', 'nombre', 'razonSocial', 'acciones'];
  elementosPagina = [5, 10, 25, 100];
  totalElementos = 0;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  empresas$: Observable<EmpresaListado[]> = of();

  constructor(
    private readonly logger: NGXLogger,
    public dialogRef: MatDialogRef<SearchEmpresaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SearchEmpresaModalData,
    private empresaService: EmpresaService,
    private snackBarService: SnackBarService
  ) {
  }

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      datosEmpresa: new FormControl()
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

  closeModal(empresa?: IEmpresa): void {
    this.dialogRef.close(empresa);
  }

  search(reset?: boolean): void {
    const options: SgiRestFindOptions = {
      page: {
        index: reset ? 0 : this.paginator.pageIndex,
        size: this.paginator.pageSize
      },
      sort: new RSQLSgiRestSort(this.sort?.active, SgiRestSortDirection.fromSortDirection(this.sort?.direction)),
      filter: this.buildFilter()
    };
    this.empresas$ = this.empresaService.findAll(options)
      .pipe(
        map((response) => {
          // Map response total
          this.totalElementos = response.total;
          // Reset pagination to first page
          if (reset) {
            this.paginator.pageIndex = 0;
          }
          // Return the values
          return response.items.map(empresa => {
            const empresaListado: EmpresaListado = {
              empresa,
              selected: this.data.selectedEmpresas.some(selectedEmpresa => selectedEmpresa.id === empresa.id)
            };
            return empresaListado;
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
    FormGroupUtil.clean(this.formGroup);
    this.search(true);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    const rsqlFilter = new RSQLSgiRestFilter('numeroIdentificacion', SgiRestFilterOperator.LIKE_ICASE, controls.datosEmpresa.value)
      .or('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.datosEmpresa.value)
      .or('razonSocial', SgiRestFilterOperator.LIKE_ICASE, controls.datosEmpresa.value);

    return rsqlFilter;
  }

}
