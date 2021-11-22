import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { CalendarioFacturacionService } from '@core/services/sge/calendario-facturacion.service';
import { DateTime } from 'luxon';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { FacturasEmitidasModalComponent } from '../../modals/facturas-emitidas-modal/facturas-emitidas-modal.component';
import { RowTreeDesglose } from '../desglose-economico.fragment';
import { IDesglose } from '../facturas-justificantes.fragment';
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

  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesglose<IDatoEconomico>>();

  @ViewChild('anualSel') selectAnualidades: MatSelect;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private calendarioFacturacionService: CalendarioFacturacionService,
    private matDialog: MatDialog
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
    }));
  }

  public clearDesglose(): void {
    this.formPart.formGroupFechas.reset();
    this.formPart.clearDesglose();
  }

  showDetail(element: IDesglose): void {
    this.subscriptions.push(this.calendarioFacturacionService.getFacturaEmitidaDetalle(element.id).subscribe(
      (detalle) => {
        const config: MatDialogConfig<IDatoEconomicoDetalle> = {
          panelClass: 'sgi-dialog-container',
          data: detalle
        };
        this.matDialog.open(FacturasEmitidasModalComponent, config);
      }
    ));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
