import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { FormularioSolicitud } from '@core/enums/formulario-solicitud';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { CambioEstadoModalComponent, SolicitudCambioEstadoModalComponentData } from '../modals/cambio-estado-modal/cambio-estado-modal.component';
import { SOLICITUD_ROUTE_NAMES } from '../solicitud-route-names';
import { SolicitudActionService } from '../solicitud.action.service';

const MSG_BUTTON_EDIT = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const SOLICITUD_KEY = marker('csp.solicitud');
const MSG_BUTTON_CAMBIO_ESTADO = marker('csp.solicitud.cambio-estado');
const MSG_CAMBIO_ESTADO_SUCCESS = marker('msg.csp.cambio-estado.success');
const MSG_CAMBIO_ESTADO_ERROR = marker('error.csp.solicitud.cambio-estado');

@Component({
  selector: 'sgi-solicitud-editar',
  templateUrl: './solicitud-editar.component.html',
  styleUrls: ['./solicitud-editar.component.scss'],
  viewProviders: [
    SolicitudActionService
  ]
})
export class SolicitudEditarComponent extends ActionComponent implements OnInit {
  SOLICITUD_ROUTE_NAMES = SOLICITUD_ROUTE_NAMES;

  textoCrear: string;
  textoEditarSuccess: string;
  textoEditarError: string;
  textoCambioEstado = MSG_BUTTON_CAMBIO_ESTADO;

  disableCambioEstado = false;

  get Estado() {
    return Estado;
  }

  get FormularioSolicitud() {
    return FormularioSolicitud;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    route: ActivatedRoute,
    public actionService: SolicitudActionService,
    dialogService: DialogService,
    private matDialog: MatDialog,
    private readonly translate: TranslateService
  ) {
    super(router, route, actionService, dialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.subscriptions.push(this.actionService.status$.subscribe(
      status => {
        this.disableCambioEstado = status.changes || status.errors || this.actionService.readonly;
      }
    ));
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_EDIT,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEditarSuccess = value);

    this.translate.get(
      SOLICITUD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
        );
      })
    ).subscribe((value) => this.textoEditarError = value);
  }

  saveOrUpdate(): void {
    this.actionService.saveOrUpdate().subscribe(
      () => { },
      (error) => {
        this.logger.error(error);
        this.snackBarService.showError(this.textoEditarError);
      },
      () => {
        this.snackBarService.showSuccess(this.textoEditarSuccess);
        this.router.navigate(['../'], { relativeTo: this.activatedRoute });
      }
    );
  }

  hasEstadoCambio(...estadosSolicitud: Estado[]): boolean {
    let estadoCorrecto = false;
    estadosSolicitud.forEach((estadoSolicitud) => {
      if (this.actionService.estado === estadoSolicitud) {
        estadoCorrecto = true;
        return;
      }
    });

    return estadoCorrecto;
  }

  /**
   * Apertura de modal cambio de estado para insertar comentario
   */
  openCambioEstado(): void {
    const data: SolicitudCambioEstadoModalComponentData = {
      estadoActual: this.actionService.estado,
      estadoNuevo: null,
      comentario: null,
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(CambioEstadoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: SolicitudCambioEstadoModalComponentData) => {
        if (modalData) {
          const estadoSolicitud = {
            estado: modalData.estadoNuevo,
            comentario: modalData.comentario
          } as IEstadoSolicitud;
          this.actionService.cambiarEstado(estadoSolicitud).subscribe(
            () => { },
            (error) => {
              this.logger.error(error);
              this.snackBarService.showError(MSG_CAMBIO_ESTADO_ERROR);
            },
            () => {
              this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
              this.router.navigate(['../'], { relativeTo: this.activatedRoute });
            }
          );;
        }

      }
    );
  }
}
