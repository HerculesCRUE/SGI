import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { PersonalContratadoModalComponent } from '../../modals/personal-contratado-modal/personal-contratado-modal.component';
import { RowTreeDesglose } from '../desglose-economico.fragment';
import { IDesglose } from '../facturas-justificantes.fragment';
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

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    private matDialog: MatDialog
  ) {
    super(actionService.FRAGMENT.PERSONAL_CONTRATADO, actionService);

    this.formPart = this.fragment as PersonalContratadoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.subscriptions.push(this.formPart.desglose$.subscribe(elements => {
      this.dataSourceDesglose.data = elements;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showDetail(element: IDesglose): void {
    this.subscriptions.push(this.ejecucionEconomicaService.getPersonaContratada(element.id).subscribe(
      (detalle) => {
        const config: MatDialogConfig<IDatoEconomicoDetalle> = {
          panelClass: 'sgi-dialog-container',
          data: detalle
        };
        this.matDialog.open(PersonalContratadoModalComponent, config);
      }
    ));
  }

}
