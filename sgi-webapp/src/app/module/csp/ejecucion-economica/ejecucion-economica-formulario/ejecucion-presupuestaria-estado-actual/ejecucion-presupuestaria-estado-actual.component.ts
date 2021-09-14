import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDatoEconomico } from '@core/models/sge/dato-economico';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { Subscription } from 'rxjs';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { IProyectoRelacion, RowTreeDesglose } from '../desglose-economico.fragment';
import { EjecucionPresupuestariaEstadoActualFragment } from './ejecucion-presupuestaria-estado-actual.fragment';

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

  msgParamEntity = {};
  textoDelete: string;

  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesglose<IDatoEconomico>>();

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    actionService: EjecucionEconomicaActionService
  ) {
    super(actionService.FRAGMENT.EJECUCION_PRESUPUESTARIA_ESTADO_ACTUAL, actionService);

    this.formPart = this.fragment as EjecucionPresupuestariaEstadoActualFragment;
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

}
