import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudProyectoSocioPeriodoJustificacion } from '@core/models/csp/solicitud-proyecto-socio-periodo-justificacion';
import { DialogService } from '@core/services/dialog.service';
import { StatusWrapper } from '@core/utils/status-wrapper';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SolicitudProyectoSocioPeriodoJustificacionModalComponent, SolicitudProyectoSocioPeriodoJustificacionModalData } from '../../modals/solicitud-proyecto-socio-periodo-justificacion-modal/solicitud-proyecto-socio-periodo-justificacion-modal.component';
import { SolicitudProyectoSocioActionService } from '../../solicitud-proyecto-socio.action.service';
import { SolicitudProyectoSocioPeriodoJustificacionFragment } from './solicitud-proyecto-socio-periodo-justificacion.fragment';

const MSG_DELETE = marker('msg.delete.entity');
const SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION_KEY = marker('csp.solicitud-proyecto-periodo-justificacion');

@Component({
  selector: 'sgi-solicitud-proyecto-socio-periodo-justificacion',
  templateUrl: './solicitud-proyecto-socio-periodo-justificacion.component.html',
  styleUrls: ['./solicitud-proyecto-socio-periodo-justificacion.component.scss']
})
export class SolicitudProyectoSocioPeriodoJustificacionComponent extends FragmentComponent implements OnInit, OnDestroy {
  formPart: SolicitudProyectoSocioPeriodoJustificacionFragment;
  private subscriptions: Subscription[] = [];

  elementosPagina = [5, 10, 25, 100];
  displayedColumns = ['numPeriodo', 'mesInicial', 'mesFinal', 'fechaInicio', 'fechaFin', 'acciones'];

  msgParamEntity = {};
  textoDelete: string;

  dataSource = new MatTableDataSource<StatusWrapper<ISolicitudProyectoSocioPeriodoJustificacion>>();
  @ViewChild(MatSort, { static: true }) sort: MatSort;

  constructor(
    private actionService: SolicitudProyectoSocioActionService,
    private matDialog: MatDialog,
    private dialogService: DialogService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.PERIODOS_JUSTIFICACION, actionService);
    this.formPart = this.fragment as SolicitudProyectoSocioPeriodoJustificacionFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    const subcription = this.formPart.periodoJustificaciones$.subscribe(
      (proyectoEquipos) => {
        this.dataSource.data = proyectoEquipos;
      }
    );
    this.subscriptions.push(subcription);
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (wrapper, property) => {
      switch (property) {
        case 'fechaInicio':
          return wrapper.value.fechaInicio;
        case 'fechaFin':
          return wrapper.value.fechaFin;
        default:
          return wrapper.value[property];
      }
    };
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value });

    this.translate.get(
      SOLICITUD_PROYECTO_PERIODO_JUSTIFICACION_KEY,
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

  openModal(wrapper?: StatusWrapper<ISolicitudProyectoSocioPeriodoJustificacion>): void {
    const periodoJustificacion: ISolicitudProyectoSocioPeriodoJustificacion = {
      fechaFin: undefined,
      fechaInicio: undefined,
      id: undefined,
      mesFinal: undefined,
      mesInicial: undefined,
      numPeriodo: this.dataSource.data.length + 1,
      observaciones: undefined,
      solicitudProyectoSocioId: this.fragment.getKey() as number
    };
    const data: SolicitudProyectoSocioPeriodoJustificacionModalData = {
      periodoJustificacion: wrapper?.value ?? periodoJustificacion,
      duracion: this.actionService.solicitudProyectoDuracion,
      empresa: this.actionService.empresa,
      selectedPeriodoJustificaciones: this.dataSource.data.map(element => element.value),
      mesInicioSolicitudProyectoSocio: this.actionService.mesInicio,
      mesFinSolicitudProyectoSocio: this.actionService.mesFin,
      isEdit: Boolean(wrapper),
      readonly: this.formPart.readonly
    };

    if (wrapper) {
      const index = data.selectedPeriodoJustificaciones.findIndex((element) => element === wrapper.value);
      if (index >= 0) {
        data.selectedPeriodoJustificaciones.splice(index, 1);
      }
    }

    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(SolicitudProyectoSocioPeriodoJustificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: SolicitudProyectoSocioPeriodoJustificacionModalData) => {
        if (modalData) {
          if (wrapper) {
            this.formPart.updatePeriodoJustificacion(wrapper);
          } else {
            this.formPart.addPeriodoJustificacion(modalData.periodoJustificacion);
          }
        }
      }
    );
  }

  deletePeriodoJustificacion(wrapper: StatusWrapper<ISolicitudProyectoSocioPeriodoJustificacion>): void {
    this.subscriptions.push(
      this.dialogService.showConfirmation(this.textoDelete).subscribe(
        (aceptado) => {
          if (aceptado) {
            this.formPart.deletePeriodoJustificacion(wrapper);
          }
        }
      )
    );
  }
}
