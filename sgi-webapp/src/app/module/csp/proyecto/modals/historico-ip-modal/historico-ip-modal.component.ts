import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { IEstadoValidacionIP, TIPO_ESTADO_VALIDACION_MAP } from '@core/models/csp/estado-validacion-ip';
import { EstadoValidacionIPService } from '@core/services/csp/estado-validacion-ip/estado-validacion-ip.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { RSQLSgiRestFilter, SgiRestFilterOperator, SgiRestFindOptions } from '@sgi/framework/http';
import { NGXLogger } from 'ngx-logger';
import { catchError, map } from 'rxjs/operators';

export interface IHistoricoIpModalData{
  proyectoFacturacionId: number;
}

@Component({
  selector: 'sgi-historico-ip-modal',
  templateUrl: './historico-ip-modal.component.html',
  styleUrls: ['./historico-ip-modal.component.scss']
})
export class HistoricoIpModalComponent extends BaseModalComponent<IHistoricoIpModalData, HistoricoIpModalComponent> implements OnInit, OnDestroy {

  public dataSource: MatTableDataSource<IEstadoValidacionIP> = new MatTableDataSource<IEstadoValidacionIP>();
  public displayedColumns = ['estado', 'fecha', 'motivo'];

  @ViewChild('sort', { static: true }) sort: MatSort;

  get TIPO_ESTADO_VALIDACION_MAP() {
    return TIPO_ESTADO_VALIDACION_MAP;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: IHistoricoIpModalData,
    public matDialogRef: MatDialogRef<HistoricoIpModalComponent>,
    private readonly translate: TranslateService,
    private readonly estadoValidacionIPService: EstadoValidacionIPService) {
      super(snackBarService, matDialogRef, data);
  }

  protected getDatosForm(): IHistoricoIpModalData {
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    return null;
  }


  ngOnInit(): void {

    this.initializeDataTable();
  }

  private initializeDataTable() {

    const filter = new RSQLSgiRestFilter('proyectoFacturacionId',  SgiRestFilterOperator.EQUALS, this.data?.proyectoFacturacionId?.toString());

    this.subscriptions.push(
      this.estadoValidacionIPService.findAll( { filter } )
        .pipe(
          map(response => this.dataSource.data = response.items),
          catchError(error => {
            this.logger.error(error);
            this.snackBarService.showError(error);
            return error;
          })
        ).subscribe());

    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subs => subs.unsubscribe());
  }

}
