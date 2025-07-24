import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { DetalleOperacionesGastosModalComponent, DetalleOperacionesGastosModalData } from '../../modals/detalle-operaciones-gastos-modal/detalle-operaciones-gastos-modal.component';
import { IDesgloseEconomicoExportData, RowTreeDesglose } from '../desglose-economico.fragment';
import { IDesglose } from '../facturas-justificantes.fragment';
import { DetalleOperacionesGastosFragment } from './detalle-operaciones-gastos.fragment';
import { DetalleOperacionesGastosExportModalComponent } from './export/detalle-operaciones-gastos-export-modal.component';

const ANUALIDAD_KEY = marker('csp.proyecto-presupuesto.anualidad');

@Component({
  selector: 'sgi-detalle-operaciones-gastos',
  templateUrl: './detalle-operaciones-gastos.component.html',
  styleUrls: ['./detalle-operaciones-gastos.component.scss']
})
export class DetalleOperacionesGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: DetalleOperacionesGastosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamAnualidadesEntity = {};
  textoDelete: string;

  private totalElementos = 0;

  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesglose<IDatoEconomico>>();

  @ViewChild('anualSel') selectAnualidades: MatSelect;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private readonly ejecucionEconomicaService: EjecucionEconomicaService,
    private matDialog: MatDialog,
    private translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DETALLE_OPERACIONES_GASTOS, actionService);

    this.formPart = this.fragment as DetalleOperacionesGastosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.subscriptions.push(this.formPart.desglose$.subscribe(elements => {
      this.dataSourceDesglose.data = elements;
      this.totalElementos = elements.length;
    }));
  }

  public clearDesglose(): void {
    this.selectAnualidades.options.forEach((item: MatOption) => item.deselect());
    this.formPart.clearDesglose();
  }

  openExportModal(): void {

    this.subscriptions.push(this.formPart.loadDataExport().subscribe(
      (exportData) => {
        const data: IDesgloseEconomicoExportData = {
          columns: exportData?.columns,
          data: exportData?.data,
          totalRegistrosExportacionExcel: this.totalElementos,
          limiteRegistrosExportacionExcel: this.formPart.limiteRegistrosExportacionExcel
        };

        const config = {
          data
        };

        this.matDialog.open(DetalleOperacionesGastosExportModalComponent, config);
      },
      this.formPart.processError
    ));
  }

  openModalView(element: IDesglose): void {
    this.subscriptions.push(
      this.ejecucionEconomicaService.getDetalleOperacionesGasto(element.id).subscribe(
        (detalle) => {
          const config: MatDialogConfig<DetalleOperacionesGastosModalData> = {
            data: {
              ...detalle,
              rowConfig: this.formPart.rowConfig
            }
          };
          this.matDialog.open(DetalleOperacionesGastosModalComponent, config);
        }
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  getCodigoEconomicoTitle(codigoEconomico: any): string {
    if (!!!codigoEconomico) {
      return '';
    }
    return (codigoEconomico.id ?? '') + (codigoEconomico.nombre ? ' - ' + codigoEconomico.nombre : '');
  }

  protected setupI18N(): void {
    this.translate.get(
      ANUALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAnualidadesEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });
  }
}
