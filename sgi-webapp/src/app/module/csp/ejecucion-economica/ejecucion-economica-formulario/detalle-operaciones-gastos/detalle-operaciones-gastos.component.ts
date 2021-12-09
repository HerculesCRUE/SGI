import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { RowTreeDesglose } from '../desglose-economico.fragment';
import { DetalleOperacionesGastosFragment } from './detalle-operaciones-gastos.fragment';
import { DetalleOperacionesGastosExportModalComponent } from './export/detalle-operaciones-gastos-export-modal.component';

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
    super(actionService.FRAGMENT.DETALLE_OPERACIONES_GASTOS, actionService);

    this.formPart = this.fragment as DetalleOperacionesGastosFragment;
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
        this.matDialog.open(DetalleOperacionesGastosExportModalComponent, config);
      },
      (error) => {
        if (error instanceof HttpProblem) {
          this.formPart.pushProblems(error);
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
