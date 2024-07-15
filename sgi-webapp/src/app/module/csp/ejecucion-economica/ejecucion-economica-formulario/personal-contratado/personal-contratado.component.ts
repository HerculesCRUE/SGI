import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ValidacionClasificacionGastos } from '@core/models/csp/configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/cnf/config.service';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { DialogService } from '@core/services/dialog.service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { Subscription, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { DatoEconomicoDetalleClasificacionModalData, FacturasJustificantesClasificacionModal } from '../../modals/facturas-justificantes-clasificacion-modal/facturas-justificantes-clasificacion-modal.component';
import { PersonalContratadoDetalleModalData, PersonalContratadoModalComponent } from '../../modals/personal-contratado-modal/personal-contratado-modal.component';
import { IDesgloseEconomicoExportData, RowTreeDesglose } from '../desglose-economico.fragment';
import { GastosClasficadosSgiEnum, IDesglose } from '../facturas-justificantes.fragment';
import { PersonalContratadoExportModalComponent } from './export/personal-contratado-export-modal.component';
import { PersonalContratadoFragment } from './personal-contratado.fragment';

const MODAL_CLASIFICACION_TITLE_KEY = marker('title.csp.ejecucion-economica.personal-contratado');
const MSG_ACCEPT_CLASIFICACION = marker('csp.ejecucion-economica.clasificacion-gastos.aceptar');

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
  @ViewChild('anualSel') selectAnualidades: MatSelect;

  private totalElementos = 0;
  private limiteRegistrosExportacionExcel: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get GastosClasficadosSgiEnum() {
    return GastosClasficadosSgiEnum;
  }

  get isClasificacionGastosEnabled(): boolean {
    return this.formPart.configuracionValidacionClasificacionGastos === ValidacionClasificacionGastos.CLASIFICACION;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    private gastoProyectoService: GastoProyectoService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly cnfService: ConfigService
  ) {
    super(actionService.FRAGMENT.PERSONAL_CONTRATADO, actionService);

    this.formPart = this.fragment as PersonalContratadoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.subscriptions.push(this.formPart.desglose$.subscribe(elements => {
      this.dataSourceDesglose.data = elements;
      this.totalElementos = elements.length;
    }));

    this.subscriptions.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-personal-contratado').subscribe(value => {
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
            data: modalData,
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
    this.subscriptions.push(this.ejecucionEconomicaService.getPersonaContratada(element.id).subscribe(
      (detalle) => {
        const config: MatDialogConfig<PersonalContratadoDetalleModalData> = {
          data: {
            ...detalle,
            rowConfig: this.formPart.rowConfig
          }
        };
        this.matDialog.open(PersonalContratadoModalComponent, config);
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
          rowConfig: this.formPart.rowConfig
        };

        const config = {
          data
        };

        this.matDialog.open(PersonalContratadoExportModalComponent, config);
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
