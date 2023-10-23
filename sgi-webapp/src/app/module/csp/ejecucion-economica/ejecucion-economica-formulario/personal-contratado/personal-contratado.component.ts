import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/cnf/config.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { PersonalContratadoModalComponent } from '../../modals/personal-contratado-modal/personal-contratado-modal.component';
import { IDesgloseEconomicoExportData, RowTreeDesglose } from '../desglose-economico.fragment';
import { IDesglose } from '../facturas-justificantes.fragment';
import { PersonalContratadoExportModalComponent } from './export/personal-contratado-export-modal.component';
import { PersonalContratadoFragment } from './personal-contratado.fragment';

@Component({
  selector: 'sgi-personal-contratado',
  templateUrl: './personal-contratado.component.html',
  styleUrls: ['./personal-contratado.component.scss']
})
export class PersonalContratadoComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: PersonalContratadoFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamEntity = {};

  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesglose<IDesglose>>();
  @ViewChild('anualSel') selectAnualidades: MatSelect;

  private totalElementos = 0;
  private limiteRegistrosExportacionExcel: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService
  ) {
    super(actionService.FRAGMENT.PERSONAL_CONTRATADO, actionService);

    this.formPart = this.fragment as PersonalContratadoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.subscriptions.push(this.formPart.desglose$.subscribe(elements => {
      this.dataSourceDesglose.data = elements;
      this.totalElementos = elements.length;
    }));

    this.subscriptions.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-personal-contratado').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showDetail(element: IDesglose): void {
    this.subscriptions.push(this.ejecucionEconomicaService.getPersonaContratada(element.id).subscribe(
      (detalle) => {
        const config: MatDialogConfig<IDatoEconomicoDetalle> = {
          data: detalle
        };
        this.matDialog.open(PersonalContratadoModalComponent, config);
      }
    ));
  }

  openExportModal(): void {

    this.subscriptions.push(this.formPart.loadDataExport().subscribe(
      (exportData) => {
        const data: IDesgloseEconomicoExportData = {
          columns: exportData?.columns,
          data: exportData?.data,
          totalRegistrosExportacionExcel: this.totalElementos,
          limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
        };

        const config = {
          data
        };

        this.matDialog.open(PersonalContratadoExportModalComponent, config);
      },
      this.formPart.processError
    ));
  }

  public clearDesglose(): void {
    this.formPart.clearRangos();
    this.selectAnualidades.options.forEach((item: MatOption) => item.deselect());
    this.formPart.clearDesglose();
  }

}
