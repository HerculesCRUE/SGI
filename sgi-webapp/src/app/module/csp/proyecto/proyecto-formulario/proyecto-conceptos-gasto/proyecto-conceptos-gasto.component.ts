import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoConceptoGasto } from '@core/models/csp/proyecto-concepto-gasto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { ProyectoConceptoGastoService } from '@core/services/csp/proyecto-concepto-gasto.service';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subject, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { CONVOCATORIA_CONCEPTO_GASTO_ID_KEY } from '../../../proyecto-concepto-gasto/proyecto-concepto-gasto.action.service';
import { PROYECTO_ROUTE_NAMES } from '../../proyecto-route-names';
import { ProyectoActionService } from '../../proyecto.action.service';
import { ConceptoGastoListado, ProyectoConceptosGastoFragment } from './proyecto-conceptos-gasto.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const MSG_DELETE_CODIGO_ECONOMICO = marker('msg.csp.proyecto-concepto-gasto.listado.codigo-economico.delete');
const PROYECTO_CONCEPTO_GASTO_PERMITIDO_KEY = marker('csp.proyecto-concepto-gasto-permitido');
const PROYECTO_CONCEPTO_GASTO_NO_PERMITIDO_KEY = marker('csp.proyecto-concepto-gasto-no-permitido');

@Component({
  selector: 'sgi-proyecto-concepto-gasto',
  templateUrl: './proyecto-conceptos-gasto.component.html',
  styleUrls: ['./proyecto-conceptos-gasto.component.scss']
})
export class ProyectoConceptosGastoComponent extends FormFragmentComponent<ConceptoGastoListado[]> implements OnInit, OnDestroy {
  ROUTE_NAMES = ROUTE_NAMES;

  get PROYECTO_ROUTE_NAMES() {
    return PROYECTO_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formPart: ProyectoConceptosGastoFragment;
  private subscriptions: Subscription[] = [];
  costesIndirectos$ = new Subject<IProyectoConceptoGasto[]>();

  elementosPagina = [5, 10, 25, 100];
  displayedColumnsPermitidos =
    ['helpIcon', 'conceptoGasto.nombre', 'conceptoGasto.descripcion', 'importeMaximo', 'fechaInicio', 'fechaFin', 'observaciones', 'acciones'];
  displayedColumnsNoPermitidos =
    ['helpIcon', 'conceptoGasto.nombre', 'conceptoGasto.descripcion', 'fechaInicio', 'fechaFin', 'observaciones', 'acciones'];

  msgParamEntityPermitido = {};
  msgParamEntityNoPermitido = {};
  textoDelete: string;

  dataSourcePermitidos = new MatTableDataSource<ConceptoGastoListado>();
  dataSourceNoPermitidos = new MatTableDataSource<ConceptoGastoListado>();
  @ViewChild('paginatorPermitidos', { static: true }) paginatorPermitidos: MatPaginator;
  @ViewChild('paginatorNoPermitidos', { static: true }) paginatorNoPermitidos: MatPaginator;
  @ViewChild('sortPermitidos', { static: true }) sortPermitidos: MatSort;
  @ViewChild('sortNoPermitidos', { static: true }) sortNoPermitidos: MatSort;

  constructor(
    protected actionService: ProyectoActionService,
    private dialogService: DialogService,
    private proyectoConceptoGastoService: ProyectoConceptoGastoService,
    private readonly translate: TranslateService,
  ) {
    super(actionService.FRAGMENT.ELEGIBILIDAD, actionService);
    this.formPart = this.fragment as ProyectoConceptosGastoFragment;

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
    this.dataSourcePermitidos = new MatTableDataSource<ConceptoGastoListado>();
    this.dataSourcePermitidos.paginator = this.paginatorPermitidos;
    this.dataSourcePermitidos.sort = this.sortPermitidos;
    this.dataSourceNoPermitidos = new MatTableDataSource<ConceptoGastoListado>();
    this.dataSourceNoPermitidos.paginator = this.paginatorNoPermitidos;
    this.dataSourceNoPermitidos.sort = this.sortNoPermitidos;
    this.subscriptions.push(this.formPart?.proyectoConceptosGastoPermitidos$.subscribe(elements => {
      this.costesIndirectos$.next(elements.filter(element => element.proyectoConceptoGasto)
        .map(element => element.proyectoConceptoGasto.value));
      this.dataSourcePermitidos.data = elements;
    }));
    this.subscriptions.push(this.formPart?.proyectoConceptosGastoNoPermitidos$.subscribe(elements => {
      this.dataSourceNoPermitidos.data = elements;
    }));

    this.dataSourcePermitidos.sortingDataAccessor =
      (wrapper: ConceptoGastoListado, property: string) => {
        switch (property) {
          default:
            return wrapper[property];
        }
      };

    this.dataSourceNoPermitidos.sortingDataAccessor =
      (wrapper: ConceptoGastoListado, property: string) => {
        switch (property) {
          default:
            return wrapper[property];
        }
      };
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_PERMITIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntityPermitido = { entity: value });

    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_NO_PERMITIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntityNoPermitido = { entity: value });


    this.translate.get(
      PROYECTO_CONCEPTO_GASTO_NO_PERMITIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoDelete = value);
  }

  deleteConceptoGasto(wrapper: StatusWrapper<IProyectoConceptoGasto>) {
    this.proyectoConceptoGastoService.hasCodigosEconomicos(wrapper.value.id).subscribe(res => {
      if (res) {
        this.subscriptions.push(
          this.dialogService.showConfirmation(MSG_DELETE_CODIGO_ECONOMICO).subscribe(
            (aceptado: boolean) => {
              if (aceptado) {
                this.formPart.deleteProyectoConceptoGasto(wrapper);
              }
            }
          )
        );
      } else {
        this.subscriptions.push(
          this.dialogService.showConfirmation(this.textoDelete).subscribe(
            (aceptado: boolean) => {
              if (aceptado) {
                this.formPart.deleteProyectoConceptoGasto(wrapper);
              }
            }
          )
        );
      }
    });
  }

  displayerCosteIndirecto(costeIndirecto: IProyectoConceptoGasto): string {
    const nombreCosteIndirecto = costeIndirecto?.conceptoGasto?.nombre;
    let mesesCosteIndirecto = '';
    if (costeIndirecto?.fechaInicio) {
      mesesCosteIndirecto = ' (' + costeIndirecto.fechaInicio + (costeIndirecto.fechaFin ? (' - ' + costeIndirecto.fechaFin) : '') + ')';
    }

    return nombreCosteIndirecto ? (nombreCosteIndirecto + mesesCosteIndirecto) : '';
  }

  getConvocatoriaConceptoGastoState(convocatoriaConceptoGastoId: number) {
    return { [CONVOCATORIA_CONCEPTO_GASTO_ID_KEY]: convocatoriaConceptoGastoId };
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

}
