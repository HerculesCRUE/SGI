import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IFacturaEmitida } from '@core/models/sge/factura-emitida';
import { IFacturaEmitidaDetalle } from '@core/models/sge/factura-emitida-detalle';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/cnf/config.service';
import { CalendarioFacturacionService } from '@core/services/sge/calendario-facturacion.service';
import { DateTime } from 'luxon';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { FacturasEmitidasModalComponent } from '../../modals/facturas-emitidas-modal/facturas-emitidas-modal.component';
import { IDesgloseFacturaEmitidaExportData, RowTreeDesgloseFacturaEmitida } from '../desglose-facturas.fragment';
import { IDesglose } from '../facturas-justificantes.fragment';
import { FacturasEmitidasExportModalComponent } from './export/facturas-emitidas-export-modal.component';
import { FacturasEmitidasFragment } from './facturas-emitidas.fragment';

@Component({
  selector: 'sgi-facturas-emitidas',
  templateUrl: './facturas-emitidas.component.html',
  styleUrls: ['./facturas-emitidas.component.scss']
})
export class FacturasEmitidasComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: FacturasEmitidasFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamEntity = {};
  textoDelete: string;

  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesgloseFacturaEmitida<IFacturaEmitida>>();

  @ViewChild('anualSel') selectAnualidades: MatSelect;

  private totalElementos = 0;
  private limiteRegistrosExportacionExcel: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private calendarioFacturacionService: CalendarioFacturacionService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService
  ) {
    super(actionService.FRAGMENT.FACTURAS_EMITIDAS, actionService);

    this.formPart = this.fragment as FacturasEmitidasFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.subscriptions.push(
      this.formPart.formGroupFechas.controls.facturaDesde.valueChanges.subscribe(
        (value) => {
          if (!!value && !!!this.formPart.formGroupFechas.controls.facturaHasta.value) {
            this.formPart.formGroupFechas.controls.facturaHasta.setValue(DateTime.now());
          }
        }
      )
    );
    this.subscriptions.push(this.formPart.desglose$.subscribe(elements => {
      this.dataSourceDesglose.data = elements;
      this.totalElementos = elements.length;
    }));

    this.subscriptions.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-facturas-emitidas').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  public clearDesglose(): void {
    this.formPart.formGroupFechas.reset();
    this.formPart.clearDesglose();
  }

  showDetail(element: IDesglose): void {
    this.subscriptions.push(this.calendarioFacturacionService.getFacturaEmitidaDetalle(element.id).subscribe(
      (detalle) => {
        const config: MatDialogConfig<IFacturaEmitidaDetalle> = {
          data: detalle
        };
        this.matDialog.open(FacturasEmitidasModalComponent, config);
      }
    ));
  }

  openExportModal(): void {

    this.subscriptions.push(this.formPart.loadDataExport().subscribe(
      (exportData) => {
        const data: IDesgloseFacturaEmitidaExportData = {
          columns: exportData?.columns,
          data: exportData?.data,
          totalRegistrosExportacionExcel: this.totalElementos,
          limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
        };

        const config = {
          data
        };

        this.matDialog.open(FacturasEmitidasExportModalComponent, config);
      },
      this.formPart.processError
    ));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
