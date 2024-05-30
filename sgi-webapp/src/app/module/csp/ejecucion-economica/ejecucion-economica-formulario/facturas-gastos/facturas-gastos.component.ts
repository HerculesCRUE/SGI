import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ValidacionClasificacionGastos } from '@core/models/csp/configuracion';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/cnf/config.service';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { DialogService } from '@core/services/dialog.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { Subscription, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { FacturasGastosModalComponent } from '../../modals/facturas-gastos-modal/facturas-gastos-modal.component';
import { DatoEconomicoDetalleClasificacionModalData, FacturasJustificantesClasificacionModal } from '../../modals/facturas-justificantes-clasificacion-modal/facturas-justificantes-clasificacion-modal.component';
import { IDesgloseEconomicoExportData, RowTreeDesglose } from '../desglose-economico.fragment';
import { GastosClasficadosSgiEnum, IDesglose } from '../facturas-justificantes.fragment';
import { FacturasGastosExportModalComponent } from './export/facturas-gastos-export-modal.component';
import { FacturasGastosFragment } from './facturas-gastos.fragment';

const MODAL_CLASIFICACION_TITLE_KEY = marker('title.csp.ejecucion-economica.facturas-gastos');
const MSG_ACCEPT_CLASIFICACION = marker('csp.ejecucion-economica.clasificacion-gastos.aceptar');

@Component({
  selector: 'sgi-facturas-gastos',
  templateUrl: './facturas-gastos.component.html',
  styleUrls: ['./facturas-gastos.component.scss']
})
export class FacturasGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: FacturasGastosFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamEntity = {};

  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesglose<IDesglose>>();
  @ViewChild('anualSel') selectAnualidades: MatSelect;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get GastosClasficadosSgiEnum() {
    return GastosClasficadosSgiEnum;
  }

  get isClasificacionGastosEnabled(): boolean {
    return this.formPart.configuracionValidacionClasificacionGastos === ValidacionClasificacionGastos.CLASIFICACION;
  }

  private totalElementos = 0;
  private limiteRegistrosExportacionExcel: string;

  constructor(
    actionService: EjecucionEconomicaActionService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    private gastoProyectoService: GastoProyectoService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly cnfService: ConfigService
  ) {
    super(actionService.FRAGMENT.FACTURAS_GASTOS, actionService);

    this.formPart = this.fragment as FacturasGastosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.subscriptions.push(this.formPart.desglose$.subscribe(elements => {
      this.dataSourceDesglose.data = elements;
      this.totalElementos = elements.length;
    }));

    this.subscriptions.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-facturas-gastos').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showDetail(element: IDesglose): void {
    if (this.isClasificacionGastosEnabled) {
      this.openModalClasificacion(element);
    } else {
      this.openModalView(element);
    }
  }

  acceptClasificacion(element: IDesglose): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_ACCEPT_CLASIFICACION).pipe(
        filter(aceptado => !!aceptado)
      ).subscribe(() => {
        this.formPart.acceptClasificacionGastosProyectos(
          this.dataSourceDesglose.data.find(desgloseRow => desgloseRow.level === 3 && desgloseRow.item.id === element.id)
        );
      })
    );

  }

  isAcceptClasificacionAllowed(element: IDesglose): boolean {
    return element.clasificadoAutomaticamente && !this.formPart.isGastoProyectoUpdated(element.id);
  }

  openModalClasificacion(element: IDesglose): void {
    this.subscriptions.push(
      this.ejecucionEconomicaService.getFacturaGasto(element.id).pipe(
        map(detalle => {
          const datoEconomicoDetalle = detalle as DatoEconomicoDetalleClasificacionModalData;
          datoEconomicoDetalle.proyectosSgiIds = this.formPart.relaciones$.value.map(relacion => relacion.id);
          datoEconomicoDetalle.proyecto = element.proyecto;
          return datoEconomicoDetalle;
        }),
        switchMap((detalle) => {
          detalle.gastoProyecto = this.formPart.getGastoProyectoUpdated(element.id);
          if (!detalle.gastoProyecto) {
            return this.gastoProyectoService.findByGastoRef(element.id).pipe(
              map(gastoProyecto => {
                detalle.gastoProyecto = gastoProyecto;
                return detalle;
              })
            );
          }
          return of(detalle);
        }),
        switchMap(modalData => {
          modalData.tituloModal = MODAL_CLASIFICACION_TITLE_KEY;
          modalData.showDatosCongreso = false;
          modalData.disableProyectoSgi = this.formPart.disableProyectoSgi;
          const config: MatDialogConfig<DatoEconomicoDetalleClasificacionModalData> = {
            data: modalData
          };

          return this.matDialog.open(FacturasJustificantesClasificacionModal, config).afterClosed();
        }),
        filter(modalData => !!modalData)
      ).subscribe(
        modalData => {
          this.formPart.updateGastoProyecto(modalData.gastoProyecto);
        },
        this.formPart.processError
      )
    );
  }

  openModalView(element: IDesglose): void {
    this.subscriptions.push(this.ejecucionEconomicaService.getFacturaGasto(element.id).subscribe(
      (detalle) => {
        const config: MatDialogConfig<IDatoEconomicoDetalle> = {
          data: detalle
        };
        this.matDialog.open(FacturasGastosModalComponent, config);
      }
    ));
  }

  openExportModal(): void {

    this.subscriptions.push(this.formPart.loadDataExport().subscribe(
      (exportData) => {
        const data: IDesgloseEconomicoExportData = {
          columns: exportData?.columns,
          data: exportData?.data,
          totalRegistrosExportacionExcel: this.totalElementos,
          limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel),
          showColumClasificadoAutomaticamente: this.formPart.isClasificacionGastosEnabled,
          showColumnProyectoSgi: !this.formPart.disableProyectoSgi
        };

        const config = {
          data
        };

        this.matDialog.open(FacturasGastosExportModalComponent, config);
      },
      this.formPart.processError
    ));
  }

  public clearDesglose(): void {
    this.formPart.clearRangos();
    this.selectAnualidades.options.forEach((item: MatOption) => item.deselect());
    this.formPart.clearDesglose();
  }

}
