import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaConceptoGasto } from '@core/models/csp/convocatoria-concepto-gasto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ConvocatoriaConceptoGastoService } from '@core/services/csp/convocatoria-concepto-gasto.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subject, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_ROUTE_NAMES } from '../../convocatoria-route-names';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';
import { ConvocatoriaConceptoGastoFragment } from './convocatoria-concepto-gasto.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_DELETE_CODIGO_ECONOMICO = marker('msg.csp.convocatoria-concepto-gasto.listado.codigo-economico.delete');
const CONVOCATORIA_CONCEPTO_GASTO_PERMITIDO_KEY = marker('csp.convocatoria-concepto-gasto-permitido');
const CONVOCATORIA_CONCEPTO_GASTO_NO_PERMITIDO_KEY = marker('csp.convocatoria-concepto-gasto-no-permitido');

@Component({
  selector: 'sgi-convocatoria-concepto-gasto',
  templateUrl: './convocatoria-concepto-gasto.component.html',
  styleUrls: ['./convocatoria-concepto-gasto.component.scss']
})

export class ConvocatoriaConceptoGastoComponent extends FragmentComponent implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  get CONVOCATORIA_ROUTE_NAMES() {
    return CONVOCATORIA_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formPart: ConvocatoriaConceptoGastoFragment;
  private subscriptions: Subscription[] = [];
  costesIndirectos$ = new Subject<IConvocatoriaConceptoGasto[]>();

  elementosPagina = [5, 10, 25, 100];
  displayedColumnsPermitidos =
    ['conceptoGasto.nombre', 'conceptoGasto.descripcion', 'conceptoGasto.costesIndirectos', 'importeMaximo', 'porcentajeMaximo', 'mesInicial', 'mesFinal', 'observaciones', 'acciones'];
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
    protected actionService: ConvocatoriaActionService,
    private dialogService: DialogService,
    private convocatoriaConceptoGastoService: ConvocatoriaConceptoGastoService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.ELEGIBILIDAD, actionService);
    this.formPart = this.fragment as ConvocatoriaConceptoGastoFragment;

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
    this.setupI18N();
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

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_PERMITIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntityPermitido = { entity: value });

    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_NO_PERMITIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntityNoPermitido = { entity: value });


    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_PERMITIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeletePermitido = value);

    this.translate.get(
      CONVOCATORIA_CONCEPTO_GASTO_NO_PERMITIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDeleteNoPermitido = value);
  }

  deleteConvocatoriaConceptoGasto(wrapper: StatusWrapper<IConvocatoriaConceptoGasto>, isPermitido: boolean) {
    this.convocatoriaConceptoGastoService.existsCodigosEconomicos(wrapper.value.id).subscribe(res => {
      if (res) {
        this.subscriptions.push(
          this.dialogService.showConfirmation(MSG_DELETE_CODIGO_ECONOMICO).subscribe(
            (aceptado: boolean) => {
              if (aceptado) {
                this.formPart.deleteConvocatoriaConceptoGasto(wrapper);
              }
            }
          )
        );
      } else {
        const messageConfirmation = isPermitido ? this.textoDeletePermitido : this.textoDeleteNoPermitido;
        this.subscriptions.push(
          this.dialogService.showConfirmation(messageConfirmation).subscribe(
            (aceptado: boolean) => {
              if (aceptado) {
                this.formPart.deleteConvocatoriaConceptoGasto(wrapper);
              }
            }
          )
        );
      }
    });
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
