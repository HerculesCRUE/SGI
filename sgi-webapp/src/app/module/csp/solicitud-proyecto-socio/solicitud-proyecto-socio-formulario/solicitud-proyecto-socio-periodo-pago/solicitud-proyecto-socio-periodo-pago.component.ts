import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoSocioPeriodoPago } from '@core/models/csp/solicitud-proyecto-socio-periodo-pago';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SolicitudProyectoSocioPeriodoPagoModalComponent, SolicitudProyectoSocioPeriodoPagoModalData } from '../../modals/solicitud-proyecto-socio-periodo-pago-modal/solicitud-proyecto-socio-periodo-pago-modal.component';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { SolicitudProyectoSocioPeriodoPagoFragment } from './solicitud-proyecto-socio-periodo-pago.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_PROYECTO_SOCIO_PERIODO_PAGO_KEY = marker('csp.solicitud-proyecto-socio-periodo-pago');

@Component({
  selector: 'sgi-solicitud-proyecto-socio-periodo-pago',
  templateUrl: './solicitud-proyecto-socio-periodo-pago.component.html',
  styleUrls: ['./solicitud-proyecto-socio-periodo-pago.component.scss']
})
export class SolicitudProyectoSocioPeriodoPagoComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: SolicitudProyectoSocioPeriodoPagoFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numPeriodo', 'mes', 'importe', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoSocioPeriodoPago>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private actionService: SolicitudProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PERIODOS_PAGOS, actionService);
    this.formPart = this.fragment as SolicitudProyectoSocioPeriodoPagoFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.formPart.periodoPagos$.subscribe(
      (proyectoEquipos) => {
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => wrapper.value[property];
    this.subscriptions.push(subcription);
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_SOCIO_PERIODO_PAGO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_SOCIO_PERIODO_PAGO_KEY,
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

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoSocioPeriodoPago>): void {
    const solicitudProyectoPeriodoPago: ISolicitudProyectoSocioPeriodoPago = {
      id: undefined,
      importe: undefined,
      mes: undefined,
      numPeriodo: this.dataSource.data.length + 1,
      solicitudProyectoSocioId: undefined
    };
    const data: SolicitudProyectoSocioPeriodoPagoModalData = {
      solicitudProyectoPeriodoPago: wrapper?.value ?? solicitudProyectoPeriodoPago,
      duracion: this.actionService.solicitudProyectoDuracion,
      selectedMeses: this.dataSource.data.map(element => element.value.mes),
      mesInicioSolicitudProyectoSocio: this.actionService.mesInicio,
      mesFinSolicitudProyectoSocio: this.actionService.mesFin,
      isEdit: Boolean(wrapper),
      readonly: this.formPart.readonly
    };
    if (wrapper) {
      const index = data.selectedMeses.findIndex((element) => element === wrapper.value.mes);
      if (index >= 0) {
        data.selectedMeses.splice(index, 1);
      }
    }
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(SolicitudProyectoSocioPeriodoPagoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: SolicitudProyectoSocioPeriodoPagoModalData) => {
        if (modalData) {
          if (wrapper) {
            if (!wrapper.created) {
              wrapper.setEdited();
            }
            this.formPart.setChanges(true);
          } else {
            this.formPart.addPeriodoPago(modalData.solicitudProyectoPeriodoPago);
          }
        }
      }
    );
  }

  deleteProyectoEquipo(wrapper: StatusWrapper<ISolicitudProyectoSocioPeriodoPago>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoPago(wrapper);
          }
        }
      )
    );
  }

}
