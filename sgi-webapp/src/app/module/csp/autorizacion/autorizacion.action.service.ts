import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IAutorizacion } from '@core/models/csp/autorizacion';
import { Estado } from '@core/models/csp/estado-autorizacion';
import { ActionService } from '@core/services/action-service';
import { AutorizacionService } from '@core/services/csp/autorizacion/autorizacion.service';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';
import { EstadoAutorizacionService } from '@core/services/csp/estado-autorizacion/estado-autorizacion.service';
import { DialogService } from '@core/services/dialog.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { AUTORIZACION_DATA_KEY } from './autorizacion-data.resolver';
import { AutorizacionDatosGeneralesFragment } from './autorizacion-formulario/autorizacion-datos-generales/autorizacion-datos-generales.fragment';
import { AutorizacionHistoricoEstadosFragment } from './autorizacion-formulario/autorizacion-historico-estados/autorizacion-historico-estados.fragment';
import { AUTORIZACION_ROUTE_PARAMS } from './autorizacion-route-params';

const MSG_REGISTRAR = marker('msg.csp.autorizacion.presentar');

export interface IAutorizacionData {
  presentable: boolean,
  isInvestigador: boolean
}

@Injectable()
export class AutorizacionActionService extends
  ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    HISTORICO_ESTADOS: 'historico-estados'
  };

  private datosGenerales: AutorizacionDatosGeneralesFragment;
  private historicoEstados: AutorizacionHistoricoEstadosFragment;

  private readonly data: IAutorizacionData;
  public readonly id: number;

  get estado(): Estado {
    return this.datosGenerales.getValue().estado?.estado;
  }

  get autorizacion(): IAutorizacion {
    return this.datosGenerales.getValue();
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
    personaService: PersonaService,
    empresaService: EmpresaService,
    estadoAutorizacionService: EstadoAutorizacionService,
    convocatoriaService: ConvocatoriaService,
    public authService: SgiAuthService,
    public dialogService: DialogService,
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

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    this.addFragment(this.FRAGMENT.HISTORICO_ESTADOS, this.historicoEstados);

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
}
