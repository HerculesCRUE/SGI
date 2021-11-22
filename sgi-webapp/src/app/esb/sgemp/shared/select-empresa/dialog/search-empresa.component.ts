import { AfterViewInit, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { IEmpresa } from '@core/models/sgemp/empresa';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { FormGroupUtil } from '@core/utils/form-group-util';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, RSQLSgiRestSort, SgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions, SgiRestSortDirection } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { merge, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { ACTION_MODAL_MODE } from 'src/app/esb/shared/formly-forms/core/base-formly-modal.component';
import { EmpresaFormlyModalComponent, IEmpresaFormlyData } from '../../../formly-forms/empresa-formly-modal/empresa-formly-modal.component';

const MSG_LISTADO_ERROR = marker('error.load');
const TIPO_EMPRESA_KEY = marker('sgemp.empresa');

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

  msgParamEntity: {};

  constructor(
    private readonly logger: NGXLogger,
    public dialogRef: MatDialogRef<SearchEmpresaModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SearchEmpresaModalData,
    private empresaService: EmpresaService,
    private snackBarService: SnackBarService,
    private readonly translate: TranslateService,
    private empresaCreateMatDialog: MatDialog,
  ) {
  }

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      datosEmpresa: new FormControl()
    });
    this.setupI18N();
  }

  private setupI18N(): void {
    this.translate.get(
      TIPO_EMPRESA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

  }

  ngAfterViewInit(): void {
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
          if (error instanceof HttpProblem) {
            this.snackBarService.showError(error);
          }
          else {
            this.snackBarService.showError(MSG_LISTADO_ERROR);
          }
          return of([]);
        })
      );
  }

  /**
   * Clean filters an reload the table
   */
  onClearFilters(): void {
    FormGroupUtil.clean(this.formGroup);
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  private buildFilter(): SgiRestFilter {
    const controls = this.formGroup.controls;

    return new RSQLSgiRestFilter('numeroIdentificacion', SgiRestFilterOperator.LIKE_ICASE, controls.datosEmpresa.value)
      .or('nombre', SgiRestFilterOperator.LIKE_ICASE, controls.datosEmpresa.value)
      .or('razonSocial', SgiRestFilterOperator.LIKE_ICASE, controls.datosEmpresa.value);
  }

  openEmpresaCreateModal(): void {
    this.openEmpresaFormlyModal(ACTION_MODAL_MODE.NEW);
  }

  openEmpresaEditModal(empresa: IEmpresa): void {
    this.openEmpresaFormlyModal(ACTION_MODAL_MODE.EDIT, empresa);
  }

  openEmpresaViewModal(empresa: IEmpresa): void {
    this.openEmpresaFormlyModal(ACTION_MODAL_MODE.VIEW, empresa);
  }

  private openEmpresaFormlyModal(modalAction: ACTION_MODAL_MODE, empresaItem?: IEmpresa): void {
    const empresaData: IEmpresaFormlyData = {
      empresaId: empresaItem ? empresaItem.id : '',
      action: modalAction
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: empresaData
    };
    const dialogRef = this.empresaCreateMatDialog.open(EmpresaFormlyModalComponent, config);

    dialogRef.afterClosed().subscribe(
      (empresa) => {
        if (empresa) {
          this.closeModal(empresa);
        }
      }
    );
  }

}
