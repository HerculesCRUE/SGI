import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { RowTreeDesglose } from '../desglose-economico.fragment';
import { EjecucionPresupuestariaGastosFragment } from './ejecucion-presupuestaria-gastos.fragment';
import { EjecucionPresupuestariaGastosExportModalComponent } from './export/ejecucion-presupuestaria-gastos-export-modal.component';

@Component({
  selector: 'sgi-ejecucion-presupuestaria-gastos',
  templateUrl: './ejecucion-presupuestaria-gastos.component.html',
  styleUrls: ['./ejecucion-presupuestaria-gastos.component.scss']
})
export class EjecucionPresupuestariaGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: EjecucionPresupuestariaGastosFragment;

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
    private matDialog: MatDialog
  ) {
    super(actionService.FRAGMENT.EJECUCION_PRESUPUESTARIA_GASTOS, actionService);

    this.formPart = this.fragment as EjecucionPresupuestariaGastosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.subscriptions.push(this.formPart.desglose$.subscribe(elements => {
      this.dataSourceDesglose.data = elements;
    }));
  }

  public clearDesglose(): void {
    this.selectAnualidades.options.forEach((item: MatOption) => item.deselect());
    this.formPart.clearDesglose();
  }

  openExportModal(): void {
    this.subscriptions.push(this.formPart.loadDataExport().subscribe(
      (exportData) => {
        const config = {
          data: exportData
        };
        this.matDialog.open(EjecucionPresupuestariaGastosExportModalComponent, config);
      },
      this.formPart.processError
    ));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
