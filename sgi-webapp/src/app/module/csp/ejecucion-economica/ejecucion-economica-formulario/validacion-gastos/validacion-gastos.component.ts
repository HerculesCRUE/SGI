import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { FragmentComponent } from '@core/component/fragment.component';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { GastoService } from '@core/services/sge/gasto/gasto.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { GastoDetalleModalData as GastoDetalleEditarModalData, ValidacionGastosEditarModalComponent } from '../../modals/validacion-gastos-editar-modal/validacion-gastos-editar-modal.component';
import { ValidacionGastosHistoricoModalComponent } from '../../modals/validacion-gastos-historico-modal/validacion-gastos-historico-modal.component';
import { GastoDetalleModalData, ValidacionGastosModalComponent } from '../../modals/validacion-gastos-modal/validacion-gastos-modal.component';
import { EstadoTipo, ESTADO_TIPO_MAP, ValidacionGasto, ValidacionGastosFragment } from './validacion-gastos.fragment';

@Component({
  selector: 'sgi-validacion-gastos',
  templateUrl: './validacion-gastos.component.html',
  styleUrls: ['./validacion-gastos.component.scss']
})
export class ValidacionGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ValidacionGastosFragment;
  private subscriptions: Subscription[] = [];

  formGroup: FormGroup;

  elementsPage = [5, 10, 25, 100];

  readonly dataSource = new MatTableDataSource<ValidacionGasto>();

  @ViewChild(MatPaginator, { static: true }) private paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) private sort: MatSort;

  constructor(
    public actionService: EjecucionEconomicaActionService,
    private matDialog: MatDialog,
    private gastoService: GastoService,
    private gastoProyectoService: GastoProyectoService,
  ) {
    super(actionService.FRAGMENT.VALIDACION_GASTOS, actionService);
    this.formPart = this.fragment as ValidacionGastosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.loadForm();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (gasto: ValidacionGasto, property: string) => {
      switch (property) {
        case 'anualidad':
          return gasto.anualidad;
        case 'proyecto':
          return gasto.proyecto?.titulo;
        case 'clasificacionSGE':
          return gasto.clasificacionSGE?.nombre;
        case 'conceptoGasto':
          return gasto.conceptoGasto?.nombre;
        case 'aplicacionPresupuestaria':
          return gasto.partidaPresupuestaria;
        case 'codigoEconomico':
          return `${gasto.codigoEconomico?.id} ${gasto.codigoEconomico?.nombre ? '-' : ''} ${gasto.codigoEconomico?.nombre}`;
        default:
          const gastoColumn = this.formPart.columns.find(column => column.id === property);
          return gastoColumn ? gasto.columnas[gastoColumn.id] : gasto[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.gastos$.subscribe(
      (data) => {
        data.sort((a, b) => {
          return b.anualidad.localeCompare(a.anualidad)
            || a.proyecto?.titulo.localeCompare(b.proyecto?.titulo)
            || a.conceptoGasto?.nombre.localeCompare(b.conceptoGasto?.nombre)
            || a.clasificacionSGE?.nombre.localeCompare(b.clasificacionSGE?.nombre)
            || a.partidaPresupuestaria.localeCompare(b.partidaPresupuestaria)
            || `${a.codigoEconomico?.id} ${a.codigoEconomico?.nombre ? '-' : ''} ${a.codigoEconomico?.nombre}`
              .localeCompare(`${b.codigoEconomico?.id} ${b.codigoEconomico?.nombre ? '-' : ''} ${b.codigoEconomico?.nombre}`);
        });

        this.dataSource.data = data;
      })
    );
  }

  onSearch(): void {
    const estado = this.formGroup.controls.estado.value;
    const fechaDesde = LuxonUtils.toBackend(this.formGroup.controls.fechaDesde.value);
    const fechaHasta = LuxonUtils.toBackend(this.formGroup.controls.fechaHasta.value);
    this.formPart.searchGastos(estado, fechaDesde, fechaHasta);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  showDetail(element: ValidacionGasto): void {
    this.subscriptions.push(this.gastoService.findById(element.id).subscribe(
      (detalle) => {
        const gastoDetalle = detalle as GastoDetalleModalData;
        gastoDetalle.estado = ESTADO_TIPO_MAP.get(this.formGroup.controls.estado.value);
        const config: MatDialogConfig<GastoDetalleModalData> = {
          data: gastoDetalle
        };
        this.matDialog.open(ValidacionGastosModalComponent, config);
      }
    ));
  }

  showEditModal(element: ValidacionGasto): void {
    this.subscriptions.push(
      this.gastoService.findById(element.id)
        .pipe(
          map((gasto) => gasto as GastoDetalleEditarModalData),
          switchMap((gastoDetalle) =>
            this.gastoProyectoService.findByGastoRef(element.id)
              .pipe(
                map((gastoProyecto) => {
                  gastoDetalle.gastoProyecto = gastoProyecto;
                  return gastoDetalle;
                })
              )
          )
        ).subscribe(
          (detalle) => {
            detalle.estado = ESTADO_TIPO_MAP.get(this.formGroup.controls.estado.value);
            detalle.disableProyectoSgi = this.formPart.disableProyectoSgi;
            const config: MatDialogConfig<GastoDetalleEditarModalData> = {
              data: detalle
            };
            const dialogRef = this.matDialog.open(ValidacionGastosEditarModalComponent, config);
            dialogRef.afterClosed().subscribe((modalData: ValidacionGastosEditarModalComponent) => {
              if (modalData) {
                this.onSearch();
              }
            });
          }
        )
    );
  }

  showHistorical(element: ValidacionGasto): void {
    this.gastoProyectoService.findByGastoRef(element.id).subscribe(
      (detalle) => {
        const gastoProyecto = detalle as IGastoProyecto;
        const config: MatDialogConfig<number> = {
          data: gastoProyecto?.id
        };
        this.matDialog.open(ValidacionGastosHistoricoModalComponent, config);
      }
    );
  }

  private loadForm() {
    this.formGroup = new FormGroup({
      estado: new FormControl(EstadoTipo.BLOQUEADOS, Validators.required),
      fechaDesde: new FormControl(),
      fechaHasta: new FormControl()
    });
  }

}
