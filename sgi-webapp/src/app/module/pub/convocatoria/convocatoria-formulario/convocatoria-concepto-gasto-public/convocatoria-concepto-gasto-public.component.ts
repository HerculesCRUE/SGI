import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { Subject, Subscription } from 'rxjs';
import { CONVOCATORIA_PUBLIC_ROUTE_NAMES } from '../../convocatoria-public-route-names';
import { ConvocatoriaPublicActionService } from '../../convocatoria-public.action.service';
import { ConvocatoriaConceptoGastoPublicFragment } from './convocatoria-concepto-public-gasto.fragment';

@Component({
  selector: 'sgi-convocatoria-concepto-gasto-public',
  templateUrl: './convocatoria-concepto-gasto-public.component.html',
  styleUrls: ['./convocatoria-concepto-gasto-public.component.scss']
})

export class ConvocatoriaConceptoGastoPublicComponent extends FragmentComponent implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  get CONVOCATORIA_ROUTE_NAMES() {
    return CONVOCATORIA_PUBLIC_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formPart: ConvocatoriaConceptoGastoPublicFragment;
  private subscriptions: Subscription[] = [];
  costesIndirectos$ = new Subject<IConvocatoriaConceptoGasto[]>();

  elementosPagina = [5, 10, 25, 100];
  displayedColumnsPermitidos =
    ['conceptoGasto.nombre', 'conceptoGasto.descripcion', 'conceptoGasto.costesIndirectos', 'importeMaximo', 'mesInicial', 'mesFinal', 'observaciones', 'acciones'];
  displayedColumnsNoPermitidos =
    ['conceptoGasto.nombre', 'conceptoGasto.descripcion', 'conceptoGasto.costesIndirectos', 'mesInicial', 'mesFinal', 'observaciones', 'acciones'];

  msgParamEntityPermitido = {};
  msgParamEntityNoPermitido = {};
  textoDeletePermitido: string;
  textoDeleteNoPermitido: string;

  dataSourcePermitidos = new MatTableDataSource<StatusWrapper<IConvocatoriaConceptoGasto>>();
  dataSourceNoPermitidos = new MatTableDataSource<StatusWrapper<IConvocatoriaConceptoGasto>>();
  @ViewChild('paginatorPermitidos', { static: true }) paginatorPermitidos: MatPaginator;
  @ViewChild('paginatorNoPermitidos', { static: true }) paginatorNoPermitidos: MatPaginator;
  @ViewChild('sortPermitidos', { static: true }) sortPermitidos: MatSort;
  @ViewChild('sortNoPermitidos', { static: true }) sortNoPermitidos: MatSort;

  constructor(
    protected actionService: ConvocatoriaPublicActionService
  ) {
    super(actionService.FRAGMENT.ELEGIBILIDAD, actionService);
    this.formPart = this.fragment as ConvocatoriaConceptoGastoPublicFragment;

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
    this.dataSourcePermitidos = new MatTableDataSource<StatusWrapper<IConvocatoriaConceptoGasto>>();
    this.dataSourcePermitidos.paginator = this.paginatorPermitidos;
    this.dataSourcePermitidos.sort = this.sortPermitidos;
    this.dataSourceNoPermitidos = new MatTableDataSource<StatusWrapper<IConvocatoriaConceptoGasto>>();
    this.dataSourceNoPermitidos.paginator = this.paginatorNoPermitidos;
    this.dataSourceNoPermitidos.sort = this.sortNoPermitidos;
    this.subscriptions.push(this.formPart?.convocatoriaConceptoGastoPermitido$.subscribe(elements => {
      this.costesIndirectos$.next(elements.map(element => element.value));
      this.dataSourcePermitidos.data = elements;
    }));
    this.subscriptions.push(this.formPart?.convocatoriaConceptoGastoNoPermitido$.subscribe(elements => {
      this.dataSourceNoPermitidos.data = elements;
    }));

    this.dataSourcePermitidos.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaConceptoGasto>, property: string) => {
        switch (property) {
          default:
            return wrapper.value[property];
        }
      };

    this.dataSourceNoPermitidos.sortingDataAccessor =
      (wrapper: StatusWrapper<IConvocatoriaConceptoGasto>, property: string) => {
        switch (property) {
          default:
            return wrapper.value[property];
        }
      };
  }

  displayerCosteIndirecto(costeIndirecto: IConvocatoriaConceptoGasto): string {
    const nombreCosteIndirecto = costeIndirecto?.conceptoGasto?.nombre;
    let mesesCosteIndirecto = '';
    if (costeIndirecto?.mesInicial) {
      mesesCosteIndirecto = ' (' + costeIndirecto.mesInicial + (costeIndirecto.mesFinal ? (' - ' + costeIndirecto.mesFinal) : '') + ')';
    }

    return nombreCosteIndirecto ? (nombreCosteIndirecto + mesesCosteIndirecto) : '';
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
