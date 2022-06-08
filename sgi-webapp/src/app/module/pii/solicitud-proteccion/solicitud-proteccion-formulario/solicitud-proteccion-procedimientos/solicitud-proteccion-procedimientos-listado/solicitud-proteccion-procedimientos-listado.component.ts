import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProcedimiento } from '@core/models/pii/procedimiento';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest, Subscription } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { ISolicitudProteccionProcedimientoModalData, SolicitudProteccionProcedimientoModalComponent } from '../../../modals/solicitud-proteccion-procedimiento-modal/solicitud-proteccion-procedimiento-modal.component';
import { SolicitudProteccionActionService } from '../../../solicitud-proteccion.action.service';
import { SolicitudProteccionProcedimientosFragment } from '../solicitud-proteccion-procedimientos.fragment';

const SOLICITUD_PROTECCION_PROCEDIMIENTO_KEY = marker('pii.solicitud-proteccion.procedimiento');
const MSG_PROCEDIMIENTO_PERDER_CAMBIOS_KEY = marker('pii.solicitud-proteccion.procedimiento.lost-changes');
const MSG_DELETE = marker('msg.delete.entity');

@Component({
  selector: 'sgi-solicitud-proteccion-procedimientos-listado',
  templateUrl: './solicitud-proteccion-procedimientos-listado.component.html',
  styleUrls: ['./solicitud-proteccion-procedimientos-listado.component.scss']
})
export class SolicitudProteccionProcedimientosListadoComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  formPart: SolicitudProteccionProcedimientosFragment;

  elementosPagina = [5, 10, 25, 100];
  columnas = ['fecha', 'tipo-procedimiento', 'accion', 'acciones'];
  msgParamEntity = {};
  msgParamEntityPlural = {};
  msgDelete: string;
  msgLostProcedimientoDocumentoChanged: string;

  dataSource = new MatTableDataSource<StatusWrapper<IProcedimiento>>();
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    protected actionService: SolicitudProteccionActionService,
    private readonly matDialog: MatDialog,
    private readonly dialogService: DialogService,
    private readonly translate: TranslateService,

  ) {
    super(actionService.FRAGMENT.PROCEDIMIENTOS, actionService);
    this.formPart = this.fragment as SolicitudProteccionProcedimientosFragment;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();


    this.dataSource.paginator = this.paginator;
    this.dataSource.sortingDataAccessor = (item: StatusWrapper<IProcedimiento>, property: string) => {
      switch (property) {
        default: return item.value[property];
      }
    };
    this.dataSource.sort = this.sort;
    this.subscriptions.push(this.formPart.procedimientos$.subscribe(elements => {
      this.dataSource.data = elements;
    }));
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROTECCION_PROCEDIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROTECCION_PROCEDIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_DELETE,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })).subscribe((value) => this.msgDelete = value);

    this.translate.get(
      SOLICITUD_PROTECCION_PROCEDIMIENTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_PROCEDIMIENTO_PERDER_CAMBIOS_KEY,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })).subscribe((value) => this.msgLostProcedimientoDocumentoChanged = value);

  }

  /**
   * Apertura del modal de {@link IProcedimiento}
   */
  openModal(procedimiento: StatusWrapper<IProcedimiento>): void {
    if (procedimiento) {
      procedimiento.setEdited();
    }
    const procedimientoModalData: ISolicitudProteccionProcedimientoModalData = {
      procedimiento: procedimiento ?? this.formPart.createEmptyProcedimiento(),
    };

    const config: MatDialogConfig = {
      data: procedimientoModalData,

    };
    const dialogRef = this.matDialog.open(SolicitudProteccionProcedimientoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (result: ISolicitudProteccionProcedimientoModalData) => {
        if (!result) {
          return;
        }
        if (result.procedimiento.created) {
          this.formPart.addProcedimiento(result.procedimiento);
        } else {
          this.formPart.editProcedimiento(result.procedimiento);
        }
      });
  }

  deleteProcedimiento(wrapper: StatusWrapper<IProcedimiento>) {
    this.showConfirmationMessage(this.msgDelete, () => {
      this.formPart.deleteProcedimiento(wrapper);
    });
  }

  setSelectedProcedimiento(wrapper: StatusWrapper<IProcedimiento>) {
    combineLatest([this.formPart.procedimientoSelected$, this.formPart.procedimientoDocumentos$])
      .pipe(
        take(1),
        filter(([procedimientoSelected, documentos]) => procedimientoSelected?.value?.id !== wrapper.value?.id),
      )
      .subscribe(([procedimientoSelected, documentos]) => {
        if (!documentos.some(documento => documento.created)) {
          this.formPart.setSelectedProcedimiento(wrapper);
        } else {
          this.showConfirmationMessage(this.msgLostProcedimientoDocumentoChanged, () => {
            this.formPart.setSelectedProcedimiento(wrapper);
          });
        }
      });
  }

  private showConfirmationMessage(message: string, callback) {
    this.subscriptions.push(
      this.dialogService.showConfirmation(message)
        .pipe(
          filter(response => !!response)
        )
        .subscribe(
          () => callback()
        )
    );
  }

}
