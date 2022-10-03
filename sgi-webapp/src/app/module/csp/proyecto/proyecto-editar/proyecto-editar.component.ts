import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { ActionComponent } from '@core/component/action.component';
import { SgiError } from '@core/errors/sgi-error';
import { MSG_PARAMS } from '@core/i18n';
import { Estado, IEstadoProyecto } from '@core/models/csp/estado-proyecto';
import { Module } from '@core/module';
import { DialogService } from '@core/services/dialog.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { switchMap } from 'rxjs/operators';
import { CambioEstadoModalComponent, ProyectoCambioEstadoModalComponentData } from '../modals/proyecto-cambio-estado-modal/cambio-estado-modal.component';
import { PROYECTO_ROUTE_NAMES } from '../proyecto-route-names';
import { ProyectoActionService } from '../proyecto.action.service';

const MSG_BUTTON_EDIT = marker('btn.save.entity');
const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const PROYECTO_KEY = marker('csp.proyecto');
const MSG_BUTTON_CAMBIO_ESTADO = marker('csp.proyecto.cambio-estado');
const MSG_CAMBIO_ESTADO_SUCCESS = marker('msg.csp.cambio-estado.success');

@Component({
  selector: 'sgi-proyecto-editar',
  templateUrl: './proyecto-editar.component.html',
  styleUrls: ['./proyecto-editar.component.scss'],
  viewProviders: [
    ProyectoActionService
  ]
})
export class ProyectoEditarComponent extends ActionComponent implements OnInit {
  PROYECTO_ROUTE_NAMES = PROYECTO_ROUTE_NAMES;

  textoCrear: string;
  textoEditarSuccess: string;
  textoEditarError: string;
  textoCambioEstado = MSG_BUTTON_CAMBIO_ESTADO;

  disableCambioEstado = false;
  isInvestigador = false;

  get isModuleINV(): boolean {
    return this.route.snapshot.data.module === Module.INV;
  }

  get isModuleCSP(): boolean {
    return this.route.snapshot.data.module === Module.CSP;
  }


  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    router: Router,
    private route: ActivatedRoute,
    public actionService: ProyectoActionService,
    private matDialog: MatDialog,
    private confirmDialogService: DialogService,
    private readonly translate: TranslateService) {
    super(router, route, actionService, confirmDialogService);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.isInvestigador = this.actionService.isInvestigador;

    this.subscriptions.push(this.actionService.status$.subscribe(
      status => {
        this.disableCambioEstado = status.changes || status.errors || this.isInvestigador;
      }
    ));
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_BUTTON_EDIT,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoCrear = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoEditarSuccess = value);

    this.translate.get(
      PROYECTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoEditarError = value);
  }

  saveOrUpdate(action: 'save' | 'cambiar-estado'): void {
    if (action === 'cambiar-estado') {
      this.openCambioEstado();
    }
    else {
      this.actionService.saveOrUpdate().subscribe(
        () => { },
        (error) => {
          this.logger.error(error);
          if (error instanceof SgiError) {
            if (!!!error.managed) {
              this.snackBarService.showError(error);
            }
          }
          else {
            this.snackBarService.showError(this.textoEditarError);
          }
        },
        () => {
          this.snackBarService.showSuccess(this.textoEditarSuccess);
        }
      );
    }
  }

  hasEstadoCambio(...estadosProyecto: Estado[]): boolean {
    let estadoCorrecto = false;
    estadosProyecto.forEach((estadoProyecto) => {
      if (this.actionService.estado === estadoProyecto) {
        estadoCorrecto = true;
        return;
      }
    });

    return estadoCorrecto;

  }

  /**
   * Apertura de modal cambio de estado para insertar comentario
   */
  private openCambioEstado(): void {
    const data: ProyectoCambioEstadoModalComponentData = {
      estadoActual: this.actionService.estado,
      estadoNuevo: null,
      comentario: null,
      proyecto: this.actionService.proyecto,
      miembrosEquipoPersonaRefs: this.actionService.miembrosEquipoPersonaRefs,
      solicitanteRefSolicitud: this.actionService.solicitanteRefSolicitud,
      solicitudFormularioSolicitud: this.actionService.solicitudFormularioSolicitud
    };
    const config = {
      data
    };
    const dialogRef = this.matDialog.open(CambioEstadoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: IEstadoProyecto) => {
        if (modalData) {
          this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
          this.router.navigate(['../'], { relativeTo: this.activatedRoute });
        }
      }

    );
  }

}
