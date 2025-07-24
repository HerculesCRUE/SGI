import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { IEstadoValidacionIP, TIPO_ESTADO_VALIDACION_MAP } from '@core/models/csp/estado-validacion-ip';
import { EstadoValidacionIPService } from '@core/services/csp/estado-validacion-ip/estado-validacion-ip.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { RSQLSgiRestFilter, SgiRestFilterOperator } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { catchError, map } from 'rxjs/operators';
import { LanguageService } from '@core/services/language.service';


export interface IHistoricoIpModalData {
  proyectoFacturacionId: number;
}

@Component({
  selector: 'sgi-historico-ip-modal',
  templateUrl: './historico-ip-modal.component.html',
  styleUrls: ['./historico-ip-modal.component.scss']
})
export class HistoricoIpModalComponent extends DialogCommonComponent implements OnInit, OnDestroy {

  public dataSource: MatTableDataSource<IEstadoValidacionIP> = new MatTableDataSource<IEstadoValidacionIP>();
  public displayedColumns = ['estado', 'fecha', 'motivo'];

  @ViewChild('sort', { static: true }) sort: MatSort;

  get TIPO_ESTADO_VALIDACION_MAP() {
    return TIPO_ESTADO_VALIDACION_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    @Inject(MAT_DIALOG_DATA) public data: IHistoricoIpModalData,
    matDialogRef: MatDialogRef<HistoricoIpModalComponent>,
    private readonly estadoValidacionIPService: EstadoValidacionIPService,
    private readonly languageService: LanguageService
  ) {
    super(matDialogRef);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.dataSource.sortingDataAccessor =
      (wrapper: IEstadoValidacionIP, property: string) => {
        switch (property) {
          case 'motivo':
            return this.languageService.getFieldValue(wrapper.comentario);

          default:
            return wrapper[property];
        }
      };
    this.dataSource.sort = this.sort;

    this.initializeDataTable();
  }

  private initializeDataTable() {
    const filter = new RSQLSgiRestFilter('proyectoFacturacionId', SgiRestFilterOperator.EQUALS, this.data?.proyectoFacturacionId?.toString());

    this.subscriptions.push(
      this.estadoValidacionIPService.findAll({ filter })
        .pipe(
          map(response => this.dataSource.data = response.items),
          catchError(error => {
            this.logger.error(error);
            this.processError(error);
            return error;
          })
        ).subscribe());

    this.dataSource.sort = this.sort;
  }

}
