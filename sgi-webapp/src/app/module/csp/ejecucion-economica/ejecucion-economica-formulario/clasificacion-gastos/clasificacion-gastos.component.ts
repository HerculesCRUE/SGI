import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { IGastoProyecto } from '@core/models/csp/gasto-proyecto';
import { IDatoEconomicoDetalle } from '@core/models/sge/dato-economico-detalle';
import { GastoProyectoService } from '@core/services/csp/gasto-proyecto/gasto-proyecto-service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { DialogService } from '@core/services/dialog.service';
import { EjecucionEconomicaService, TipoOperacion } from '@core/services/sge/ejecucion-economica.service';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { Observable, Subscription, forkJoin, of } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { EjecucionEconomicaActionService } from '../../ejecucion-economica.action.service';
import { DatoEconomicoDetalleClasificacionModalData, FacturasJustificantesClasificacionModal } from '../../modals/facturas-justificantes-clasificacion-modal/facturas-justificantes-clasificacion-modal.component';
import { GastosClasficadosSgiEnum } from '../facturas-justificantes.fragment';
import { ClasificacionGasto, ClasificacionGastosFragment } from './clasificacion-gastos.fragment';

const MODAL_CLASIFICACION_TITLE_KEY = marker('title.csp.ejecucion-economica.clasificacion.detalle-gasto');
const MSG_ACCEPT_CLASIFICACION = marker('csp.ejecucion-economica.clasificacion-gastos.aceptar');

@Component({
  selector: 'sgi-clasificacion-gastos',
  templateUrl: './clasificacion-gastos.component.html',
  styleUrls: ['./clasificacion-gastos.component.scss']
})
export class ClasificacionGastosComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: ClasificacionGastosFragment;
  private subscriptions: Subscription[] = [];

  formGroup: FormGroup;

  elementsPage = [5, 10, 25, 100];

  readonly dataSource = new MatTableDataSource<ClasificacionGasto>();

  @ViewChild(MatPaginator, { static: true }) private paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) private sort: MatSort;

  get GastosClasficadosSgiEnum() {
    return GastosClasficadosSgiEnum;
  }

  get TipoOperacion() {
    return TipoOperacion;
  }

  constructor(
    public actionService: EjecucionEconomicaActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private ejecucionEconomicaService: EjecucionEconomicaService,
    private gastoProyectoService: GastoProyectoService,
    private proyectoProyectoSgeService: ProyectoProyectoSgeService
  ) {
    super(actionService.FRAGMENT.CLASIFICACION_GASTOS, actionService);
    this.formPart = this.fragment as ClasificacionGastosFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.loadForm();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (gasto: ClasificacionGasto, property: string) => {
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

          let columnId: string;
          switch (gasto.tipo) {
            case TipoOperacion.FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS:
              columnId = gastoColumn.idFacturasGastos;
              break;
            case TipoOperacion.FACTURAS_JUSTIFICANTES_VIAJES_DIETAS:
              columnId = gastoColumn.idViajesDietas;
              break;
            case TipoOperacion.FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO:
              columnId = gastoColumn.idPersonalContratado;
              break;

            default:
              break;
          }

          return columnId ? gasto.columnas[columnId] : gasto[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(
      this.formPart.gastos$.subscribe(
        (data) => {
          data.sort((a, b) => {
            return b.anualidad.localeCompare(a.anualidad);
          });

          this.dataSource.data = data;
        })
    );
  }

  onSearch(): void {
    const fechaDesde = LuxonUtils.toBackend(this.formGroup.controls.fechaDesde.value);
    const fechaHasta = LuxonUtils.toBackend(this.formGroup.controls.fechaHasta.value);
    const gastosClasficadosSgi = this.formGroup.controls.gastosClasficadosSgi.value;
    this.formPart.searchGastos(fechaDesde, fechaHasta, gastosClasficadosSgi);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  acceptClasificacion(clasificacionGasto: ClasificacionGasto): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(MSG_ACCEPT_CLASIFICACION).pipe(
        filter(aceptado => !!aceptado)
      ).subscribe(() => {
        this.formPart.acceptClasificacionGastosProyectos(clasificacionGasto);
      })
    );

  }

  isAcceptClasificacionAllowed(element: ClasificacionGasto): boolean {
    return element.clasificadoAutomaticamente && !this.formPart.isGastoProyectoUpdated(element.id);
  }

  showEditModal(element: ClasificacionGasto): void {
    this.subscriptions.push(
      forkJoin({
        proyectosSgiIds: this.proyectoProyectoSgeService.findByProyectoSgeId(element.proyectoId).pipe(map(response => response.items.map(e => e.proyecto.id))),
        gastoDetalle: this.getGastoDetalle(element),
        gastoProyecto: this.getGastoProyecto(element.id)
      }).pipe(
        switchMap(({ proyectosSgiIds, gastoDetalle, gastoProyecto }) => {
          const config: MatDialogConfig<DatoEconomicoDetalleClasificacionModalData> = {
            data: {
              ...gastoDetalle,
              gastoProyecto,
              proyectosSgiIds,
              tituloModal: MODAL_CLASIFICACION_TITLE_KEY,
              proyecto: null,
              vinculacion: null,
              showDatosCongreso: element.tipo === TipoOperacion.FACTURAS_JUSTIFICANTES_VIAJES_DIETAS,
              disableProyectoSgi: this.formPart.disableProyectoSgi
            }
          };

          return this.matDialog.open(FacturasJustificantesClasificacionModal, config).afterClosed();
        }),
        filter(modalData => !!modalData)
      ).subscribe(
        (modalData: DatoEconomicoDetalleClasificacionModalData) => {
          this.formPart.updateGastoProyecto(modalData.gastoProyecto);
          element.proyecto = modalData.proyecto;
          element.conceptoGasto = modalData.gastoProyecto?.conceptoGasto;
        },
        this.formPart.processError
      )
    );
  }

  /**
   * Recupera el gasto proyecto modificado en memoria si se ha modficado pero no se ha persistido el cambio y si no esta en memoria lo recupera
   * 
   * @param gastoRef Identificador del gasto
   * @returns el IGastoProyecto asociado al gastoRef
   */
  private getGastoProyecto(gastoRef: string): Observable<IGastoProyecto> {
    let gastoProyecto = this.formPart.getGastoProyectoUpdated(gastoRef);
    if (!!gastoProyecto) {
      return of(gastoProyecto);
    }

    return this.gastoProyectoService.findByGastoRef(gastoRef);
  }

  private getGastoDetalle(gasto: ClasificacionGasto): Observable<IDatoEconomicoDetalle> {
    let detalleGasto$: Observable<IDatoEconomicoDetalle>;
    switch (gasto.tipo) {
      case TipoOperacion.FACTURAS_JUSTIFICANTES_FACTURAS_GASTOS:
        detalleGasto$ = this.ejecucionEconomicaService.getFacturaGasto(gasto.id);
        break;
      case TipoOperacion.FACTURAS_JUSTIFICANTES_VIAJES_DIETAS:
        detalleGasto$ = this.ejecucionEconomicaService.getViajeDieta(gasto.id);
        break;
      case TipoOperacion.FACTURAS_JUSTIFICANTES_PERSONAL_CONTRATADO:
        detalleGasto$ = this.ejecucionEconomicaService.getPersonaContratada(gasto.id);
        break;

      default:
        detalleGasto$ = of(void 0);
    }

    return detalleGasto$;
  }

  private loadForm() {
    this.formGroup = new FormGroup({
      fechaDesde: new FormControl(),
      fechaHasta: new FormControl(),
      gastosClasficadosSgi: new FormControl()
    });
  }

}
