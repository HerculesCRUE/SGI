import { Injectable, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { Estado, IEstadoAutorizacion } from '@core/models/csp/estado-autorizacion';
import { ActionService } from '@core/services/action-service';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { CertificadoAutorizacionService } from '@core/services/csp/certificado-autorizacion/certificado-autorizacion.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { EstadoAutorizacionService } from '@core/services/csp/estado-autorizacion/estado-autorizacion.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, Subject, throwError } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { AUTORIZACION_DATA_KEY } from './autorizacion-data.resolver';
import { AutorizacionCertificadosFragment } from './autorizacion-formulario/autorizacion-certificados/autorizacion-certificados.fragment';
import { AutorizacionDatosGeneralesFragment, IAutorizacionDatosGeneralesData } from './autorizacion-formulario/autorizacion-datos-generales/autorizacion-datos-generales.fragment'; import { AutorizacionHistoricoEstadosFragment } from './autorizacion-formulario/autorizacion-historico-estados/autorizacion-historico-estados.fragment';
import { AUTORIZACION_ROUTE_PARAMS } from './autorizacion-route-params';
import { AutorizacionCambioEstadoModalComponentData, CambioEstadoModalComponent } from './cambio-estado-modal/cambio-estado-modal.component';

const MSG_REGISTRAR = marker('msg.csp.autorizacion.presentar');
const MSG_CAMBIO_ESTADO_SUCCESS = marker('msg.csp.cambio-estado.success');

export interface IAutorizacionData {
  presentable: boolean;
  isInvestigador: boolean;
  autorizacion: IAutorizacion;
}

@Injectable()
export class AutorizacionActionService extends
  ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    HISTORICO_ESTADOS: 'historico-estados',
    CERTIFICADOS: 'certificados'
  };

  private datosGenerales: AutorizacionDatosGeneralesFragment;
  private historicoEstados: AutorizacionHistoricoEstadosFragment;
  private certificados: AutorizacionCertificadosFragment;


  private readonly data: IAutorizacionData;
  public readonly id: number;

  get estado(): Estado {
    return this.datosGenerales.getValue().estado?.estado;
  }

  get autorizacionData(): IAutorizacionDatosGeneralesData {
    return this.datosGenerales.getValue();
  }

  get disableCambioEstado$() {
    return this.datosGenerales.disableCambioEstado$;
  }

  get isInvestigador(): boolean {
    return this.data.isInvestigador;
  }

  get presentable(): boolean {
    return this.data.presentable;
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    private autorizacionService: AutorizacionService,
    certificadoAutorizacionService: CertificadoAutorizacionService,
    personaService: PersonaService,
    empresaService: EmpresaService,
    estadoAutorizacionService: EstadoAutorizacionService,
    convocatoriaService: ConvocatoriaService,
    documentoService: DocumentoService,
    public authService: SgiAuthService,
    public dialogService: DialogService,
    private matDialog: MatDialog,

    protected readonly snackBarService: SnackBarService,
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(AUTORIZACION_ROUTE_PARAMS.ID));
    if (this.id) {
      this.enableEdit();
      this.data = route.snapshot.data[AUTORIZACION_DATA_KEY];
    }

    this.dialogService = dialogService;
    this.datosGenerales = new AutorizacionDatosGeneralesFragment(
      logger, this.id, autorizacionService, personaService, empresaService, estadoAutorizacionService, convocatoriaService, authService);
    this.historicoEstados = new AutorizacionHistoricoEstadosFragment(this.id, autorizacionService, false);
    this.certificados = new AutorizacionCertificadosFragment(this.id, certificadoAutorizacionService, autorizacionService, estadoAutorizacionService, documentoService)

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.HISTORICO_ESTADOS, this.historicoEstados);
    this.addFragment(this.FRAGMENT.CERTIFICADOS, this.certificados);

    this.datosGenerales.initialize();
  }

  saveOrUpdate(): Observable<void> {
    this.performChecks(true);
    if (this.hasErrors()) {
      return throwError('Errores');
    }
    if (this.isEdit()) {
      let cascade = of(void 0);
      if (this.datosGenerales.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.datosGenerales.saveOrUpdate().pipe(tap(() => this.datosGenerales.refreshInitialState(true))))
        );
      }
      return cascade.pipe(
        switchMap(() => super.saveOrUpdate())
      );
    } else {
      let cascade = of(void 0);
      if (this.datosGenerales.hasChanges()) {
        cascade = cascade.pipe(
          switchMap(() => this.datosGenerales.saveOrUpdate().pipe(
            tap((key) => {
              this.datosGenerales.refreshInitialState(true);
              if (typeof key === 'string' || typeof key === 'number') {
                this.onKeyChange(key);
              }
            })
          ))
        );
      }
      return cascade.pipe(
        switchMap(() => super.saveOrUpdate())
      );
    }
  }

  /**
   * Acción de presentacion de una autorizacion
   */
  presentar(): Observable<void> {
    return this.dialogService.showConfirmation(MSG_REGISTRAR).pipe(
      switchMap((accept) => {
        if (accept) {
          return this.autorizacionService.presentar(this.id);
        }
      })
    );
  }

  openCambioEstado(): void {
    const data: AutorizacionCambioEstadoModalComponentData = {
      estadoActual: this.estado,
      autorizacion: this.autorizacionData,
    };
    const config = {
      panelClass: 'sgi-dialog-container',
      data
    };
    const dialogRef = this.matDialog.open(CambioEstadoModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (modalData: IEstadoAutorizacion) => {
        if (modalData) {
          this.snackBarService.showSuccess(MSG_CAMBIO_ESTADO_SUCCESS);
          this.datosGenerales.reload();
          this.historicoEstados.reload();
        }
      }
    );
  }
}
