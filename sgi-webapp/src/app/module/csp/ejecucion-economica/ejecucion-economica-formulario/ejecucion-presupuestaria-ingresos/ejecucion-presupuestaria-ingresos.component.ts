import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { IDesgloseEconomicoExportData, RowTreeDesglose } from '../desglose-economico.fragment';
import { EjecucionPresupuestariaIngresosFragment } from './ejecucion-presupuestaria-ingresos.fragment';
import { EjecucionPresupuestariaIngresosExportModalComponent } from './export/ejecucion-presupuestaria-ingresos-export-modal.component';

const ANUALIDAD_KEY = marker('csp.proyecto-presupuesto.anualidad');

@Component({
  selector: 'sgi-ejecucion-presupuestaria-ingresos',
  templateUrl: './ejecucion-presupuestaria-ingresos.component.html',
  styleUrls: ['./ejecucion-presupuestaria-ingresos.component.scss']
})
export class EjecucionPresupuestariaIngresosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: EjecucionPresupuestariaIngresosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamAnualidadesEntity = {};

  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesglose<IDatoEconomico>>();
  @ViewChild('anualSel') selectAnualidades: MatSelect;

  private totalElementos = 0;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private matDialog: MatDialog,
    private translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EJECUCION_PRESUPUESTARIA_INGRESOS, actionService);

    this.formPart = this.fragment as EjecucionPresupuestariaIngresosFragment;
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

        this.matDialog.open(EjecucionPresupuestariaIngresosExportModalComponent, config);
      },
      this.formPart.processError
    ));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  protected setupI18N(): void {
    this.translate.get(
      ANUALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamAnualidadesEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });
  }

}
