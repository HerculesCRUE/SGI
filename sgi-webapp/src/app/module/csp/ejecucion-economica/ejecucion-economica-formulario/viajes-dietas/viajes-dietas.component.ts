import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatDatepicker } from '@angular/material/datepicker';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatSelect } from '@angular/material/select';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfigService } from '@core/services/cnf/config.service';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { EjecucionEconomicaService } from '@core/services/sge/ejecucion-economica.service';
import { Subscription, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { DatoEconomicoDetalleModalData, ViajesDietasModalComponent } from '../../modals/viajes-dietas-modal/viajes-dietas-modal.component';
import { IDesgloseEconomicoExportData, RowTreeDesglose } from '../desglose-economico.fragment';
import { IDesglose } from '../facturas-justificantes.fragment';
import { ViajesDietasExportModalComponent } from './export/viajes-dietas-export-modal.component';
import { ViajesDietasFragment } from './viajes-dietas.fragment';

@Component({
  selector: 'sgi-viajes-dietas',
  templateUrl: './viajes-dietas.component.html',
  styleUrls: ['./viajes-dietas.component.scss']
})
export class ViajesDietasComponent extends FragmentComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  formPart: ViajesDietasFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  msgParamEntity = {};

  private totalElementos = 0;
  private limiteRegistrosExportacionExcel: string;

  readonly dataSourceDesglose = new MatTableDataSource<RowTreeDesglose<IDesglose>>();
  @ViewChild('anualSel') selectAnualidades: MatSelect;
  @ViewChild('pickerDevengoDesde') pickerDevengoDesde: MatDatepicker<any>;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    actionService: EjecucionEconomicaActionService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    private gastoProyectoService: GastoProyectoService,
    private matDialog: MatDialog,
    private readonly cnfService: ConfigService
  ) {
    super(actionService.FRAGMENT.VIAJES_DIETAS, actionService);

    this.formPart = this.fragment as ViajesDietasFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.subscriptions.push(this.formPart.desglose$.subscribe(elements => {
      this.dataSourceDesglose.data = elements;
      this.totalElementos = elements.length;
    }));

    this.subscriptions.push(
      this.cnfService.getLimiteRegistrosExportacionExcel('csp-exp-max-num-registros-excel-viajes-dietas').subscribe(value => {
        this.limiteRegistrosExportacionExcel = value;
      }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showDetail(element: IDesglose): void {
    this.subscriptions.push(this.ejecucionEconomicaService.getViajeDieta(element.id).pipe(
      map(detalle => {
        const datoEconomicoDetalle = detalle as DatoEconomicoDetalleModalData;
        datoEconomicoDetalle.proyectosSgiIds = this.formPart.relaciones$.value.map(relacion => relacion.id);
        datoEconomicoDetalle.proyecto = element.proyecto;
        return datoEconomicoDetalle;
      }),
      switchMap((detalle) => {
        detalle.gastoProyecto = this.formPart.updatedGastosProyectos.get(element.id);
        if (!detalle.gastoProyecto) {
          return this.gastoProyectoService.findByGastoRef(element.id).pipe(
            map(gastoProyecto => {
              detalle.gastoProyecto = gastoProyecto;
              return detalle;
            })
          );
        }
        return of(detalle);
      })
    ).subscribe(
      (detalle) => {
        const config: MatDialogConfig<DatoEconomicoDetalleModalData> = {
          data: detalle
        };
        const dialogRef = this.matDialog.open(ViajesDietasModalComponent, config);
        dialogRef.afterClosed().subscribe((modalData: DatoEconomicoDetalleModalData) => {
          if (modalData) {
            this.formPart.updateGastoProyecto(modalData.gastoProyecto);
          }
        });
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
          limiteRegistrosExportacionExcel: Number(this.limiteRegistrosExportacionExcel)
        };

        const config = {
          data
        };

        this.matDialog.open(ViajesDietasExportModalComponent, config);
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
