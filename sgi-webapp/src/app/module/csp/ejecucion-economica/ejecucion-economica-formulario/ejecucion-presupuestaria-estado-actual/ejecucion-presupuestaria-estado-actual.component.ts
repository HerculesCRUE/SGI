import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDatoEconomico, TIPO_DATO_ECONOMICO_MAP } from '@core/models/sge/dato-economico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { IDesgloseEconomicoExportData, RowTreeDesglose } from '../desglose-economico.fragment';
import { EjecucionPresupuestariaEstadoActualFragment } from './ejecucion-presupuestaria-estado-actual.fragment';
import { EjecucionPresupuestariaEstadoActualExportModalComponent } from './export/ejecucion-presupuestaria-estado-actual-export-modal.component';

const ANUALIDAD_KEY = marker('csp.proyecto-presupuesto.anualidad');

@Component({
  selector: 'sgi-ejecucion-presupuestaria-estado-actual',
  templateUrl: './ejecucion-presupuestaria-estado-actual.component.html',
  styleUrls: ['./ejecucion-presupuestaria-estado-actual.component.scss']
})
export class EjecucionPresupuestariaEstadoActualComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: EjecucionPresupuestariaEstadoActualFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamAnualidadesEntity = {};

  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesglose<IDatoEconomico>>();
  @ViewChild('anualSel') selectAnualidades: MatSelect;

  private totalElementos = 0;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get TIPO_DATO_ECONOMICO_MAP() {
    return TIPO_DATO_ECONOMICO_MAP;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private matDialog: MatDialog,
    private translate: TranslateService
  ) {
    super(actionService.FRAGMENT.EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL, actionService);

    this.formPart = this.fragment as EjecucionPresupuestariaEstadoActualFragment;
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

        this.matDialog.open(EjecucionPresupuestariaEstadoActualExportModalComponent, config);
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
