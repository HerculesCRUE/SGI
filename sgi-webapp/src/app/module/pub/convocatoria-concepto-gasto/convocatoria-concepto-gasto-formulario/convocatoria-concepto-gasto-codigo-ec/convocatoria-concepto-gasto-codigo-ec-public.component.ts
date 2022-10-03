import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { ConvocatoriaConceptoGastoPublicActionService } from '../../convocatoria-concepto-gasto-public.action.service';
import { ConvocatoriaConceptoGastoCodigoEc, ConvocatoriaConceptoGastoCodigoEcPublicFragment } from './convocatoria-concepto-gasto-codigo-ec-public.fragment';

@Component({
  selector: 'sgi-convocatoria-concepto-gasto-codigo-ec-public',
  templateUrl: './convocatoria-concepto-gasto-codigo-ec-public.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-codigo-ec-public.component.scss']
})
export class ConvocatoriaConceptoGastoCodigoEcPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  formGroup: FormGroup;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formPart: ConvocatoriaConceptoGastoCodigoEcPublicFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina: number[] = [5, 10, 25, 100];
  displayedColumns: string[] = ['conceptoGasto.nombre', 'codigoEconomicoRef', 'fechaInicio', 'fechaFin', 'observaciones', 'acciones'];

  dataSource: MatTableDataSource<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>> =
    new MatTableDataSource<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>>();
  @ViewChild('paginator', { static: true }) paginator: MatPaginator;
  @ViewChild('sort', { static: true }) sort: MatSort;

  constructor(
    protected readonly logger: NGXLogger,
    public readonly actionService: ConvocatoriaConceptoGastoPublicActionService
  ) {
    super(actionService.FRAGMENT.CODIGOS_ECONOMICOS, actionService);
    this.formPart = this.fragment as ConvocatoriaConceptoGastoCodigoEcPublicFragment;

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-10px)';
    this.fxFlexProperties.md = '0 1 calc(50%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(22%-10px)';
    this.fxFlexProperties.order = '1';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.dataSource = new MatTableDataSource<StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>>();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;

    this.subscriptions.push(this.formPart?.convocatoriaConceptoGastoCodigoEcs$.subscribe(elements => {
      this.dataSource.data = elements;
    }));

    this.dataSource.sortingDataAccessor =
      (wrapper: StatusWrapper<ConvocatoriaConceptoGastoCodigoEc>, property: string) => {
        switch (property) {
          default:
            return wrapper.value[property];
        }
      };
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
