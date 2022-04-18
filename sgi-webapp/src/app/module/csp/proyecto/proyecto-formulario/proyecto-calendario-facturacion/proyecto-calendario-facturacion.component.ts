import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEstadoValidacionIP, TipoEstadoValidacion, TIPO_ESTADO_VALIDACION_MAP } from '@core/models/csp/estado-validacion-ip';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ROUTE_NAMES } from '@core/route.names';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { HistoricoIpModalComponent } from '../../modals/historico-ip-modal/historico-ip-modal.component';
import { DialogAction, IProyectoCalendarioFacturacionModalData, ProyectoCalendarioFacturacionModalComponent } from '../../modals/proyecto-calendario-facturacion-modal/proyecto-calendario-facturacion-modal.component';
import { ProyectoActionService } from '../../proyecto.action.service';
import { IProyectoFacturacionData, ProyectoCalendarioFacturacionFragment } from './proyecto-calendario-facturacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const PROYECTO_CALENDARIO_FACTURACION_ITEM_KEY = marker('csp.proyecto-calendario-facturacion.item');

@Component({
  selector: 'sgi-proyecto-calendario-facturacion',
  templateUrl: './proyecto-calendario-facturacion.component.html',
  styleUrls: ['./proyecto-calendario-facturacion.component.scss']
})
export class ProyectoCalendarioFacturacionComponent extends FragmentComponent implements OnInit, OnDestroy {

  ROUTE_NAMES = ROUTE_NAMES;

  private subscriptions: Subscription[] = [];

  formPart: ProyectoCalendarioFacturacionFragment;

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  elementosPagina: number[] = [5, 10, 25, 100];
  displayedColumns = [
    'numPrevision',
    'fechaEmision',
    'importeBase',
    'iva',
    'importeTotal',
    'comentario',
    'hitoFacturacion',
    'fechaConformidad',
    'numFacturaEmitida',
    'validacionIP',
    'acciones'
  ];

  msgParamEntity = {};
  textDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProyectoFacturacionData>>();
  @ViewChild('paginator', { static: true }) paginator: MatPaginator;
  @ViewChild('sort', { static: true }) sort: MatSort;

  get TIPO_ESTADO_VALIDACION_MAP() {
    return TIPO_ESTADO_VALIDACION_MAP;
  }

  get DIALOG_ACTION() {
    return DialogAction;
  }

  constructor(
    public actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService) {

    super(actionService.FRAGMENT.CALENDARIO_FACTURACION, actionService);
    this.formPart = this.fragment as ProyectoCalendarioFacturacionFragment;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.initDataTable();
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_ITEM_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_ITEM_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textDelete = value);
  }

  private initDataTable() {
    this.dataSource.paginator = this.paginator;

    this.subscriptions.push(this.formPart.proyectosFacturacion$.subscribe(
      (proyectosFacturacion) => this.dataSource.data = proyectosFacturacion));

    this.dataSource.sortingDataAccessor = (wrapper, property) => {
      switch (property) {
        case 'hitoFacturacion':
          return wrapper.value.tipoFacturacion.nombre;
        case 'importeTotal':
          return this.getImporteTotal(wrapper.value.importeBase, wrapper.value.porcentajeIVA);
        case 'validacionIP':
          return wrapper.value.estadoValidacionIP.estado;
        case 'numPrevision':
          return wrapper.value.numeroPrevision;
        case 'iva':
          return wrapper.value.porcentajeIVA;
        default:
          return wrapper.value[property];
      }
    }

    this.dataSource.sort = this.sort;
  }

  public getImporteTotal(importeBase: number, porcentajeIVA: number): number {
    return importeBase + (importeBase * porcentajeIVA / 100);
  }

  public canShowNotificarIP(estado: TipoEstadoValidacion): boolean {
    return estado === TipoEstadoValidacion.PENDIENTE || estado === TipoEstadoValidacion.RECHAZADA;
  }

  public canShowValidarIP(estado: TipoEstadoValidacion): boolean {
    return estado === TipoEstadoValidacion.NOTIFICADA;
  }

  public canShowDelete(estado): boolean {
    return estado !== TipoEstadoValidacion.VALIDADA;
  }

  openModal(action: DialogAction, proyectoFacturacionToUpdate?: IProyectoFacturacionData, rowIndex?: number): void {

    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    const proyectoFacturacionTable = this.dataSource.data.map(wrapper => wrapper.value);

    proyectoFacturacionTable.splice(row, 1);

    if (proyectoFacturacionToUpdate) {
      this.showProyectoFacturacionDialog(proyectoFacturacionToUpdate, row, action);
    } else {
      this.showProyectoFacturacionDialog({
        numeroPrevision: this.formPart.getNextNumeroPrevision()
      } as IProyectoFacturacionData, row, action);
    }

  }

  private showProyectoFacturacionDialog(
    proyectoFacturacion: IProyectoFacturacionData,
    row: number, action: DialogAction): void {
    const modalData: IProyectoCalendarioFacturacionModalData = {
      proyectoFacturacion,
      porcentajeIVA: proyectoFacturacion.porcentajeIVA || this.formPart.proyectoIVA,
      action
    };

    const config = {
      panelClass: 'sgi-dialog-container',
      data: modalData
    };

    const dialogRef = this.matDialog.open(ProyectoCalendarioFacturacionModalComponent, config);

    dialogRef.afterClosed().subscribe((returnedModalData: IProyectoCalendarioFacturacionModalData) => {
      if (!returnedModalData.proyectoFacturacion) {
        return;
      }
      if (returnedModalData.action === DialogAction.NEW) {
        this.formPart.addProyectoFacturacion(returnedModalData.proyectoFacturacion);
      } else {
        const itemToUpdate = new StatusWrapper<IProyectoFacturacionData>(returnedModalData.proyectoFacturacion);
        if (itemToUpdate.value.estadoValidacionIP?.estado === TipoEstadoValidacion.VALIDADA) {
          itemToUpdate.value.fechaConformidad = DateTime.now();
        }
        this.formPart.updateProyectoFacturacion(itemToUpdate, row);
      }
    });
  }

  public openHistoricoEstadoValidacionIP(proyectoFacturacionId: number): void {
    if (!proyectoFacturacionId) {
      return;
    }

    const config: MatDialogConfig = {
      panelClass: 'sgi-dialog-container',
      data: {
        proyectoFacturacionId
      },
      width: '45vw'
    };

    this.matDialog.open(HistoricoIpModalComponent, config);
  }

  public deleteItemFacturacion(toDelete: StatusWrapper<IProyectoFacturacionData>): void {
    const messageConfirmation = this.textDelete;
    this.subscriptions.push(
      this.dialogService.showConfirmation(messageConfirmation).subscribe(
        (aceptado: boolean) => {
          if (aceptado) {
            this.formPart.deleteProyectoFacturacion(toDelete);
          }
        }
      )
    );
  }

  public notificarIP(item: StatusWrapper<IProyectoFacturacionData>, rowIndex: number): void {
    // Necesario para sincronizar los cambios de orden de registros dependiendo de la ordenación y paginación
    this.dataSource.sortData(this.dataSource.filteredData, this.dataSource.sort);
    const row = (this.paginator.pageSize * this.paginator.pageIndex) + rowIndex;

    item.value.estadoValidacionIP = {
      estado: TipoEstadoValidacion.NOTIFICADA,
    } as IEstadoValidacionIP;
    this.formPart.updateProyectoFacturacion(item, row);
  }

}
