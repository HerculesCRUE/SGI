import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { INotificacionProyectoExternoCVN } from '@core/models/csp/notificacion-proyecto-externo-cvn';
import { ActionService } from '@core/services/action-service';
import { NotificacionProyectoExternoCvnService } from '@core/services/csp/notificacion-proyecto-externo-cvn/notificacion-proyecto-externo-cvn.service';
import { DialogService } from '@core/services/dialog.service';
import { DocumentoService } from '@core/services/sgdoc/documento.service';
import { EmpresaService } from '@core/services/sgemp/empresa.service';
import { PersonaService } from '@core/services/sgp/persona.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, of, throwError } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { CSP_ROUTE_NAMES } from '../csp-route-names';
import { NOTIFICACION_DATA_KEY } from './notificacion-cvn-data.resolver';
import { NotificacionCvnDatosGeneralesFragment } from './notificacion-cvn-formulario/notificacion-cvn-datos-generales/notificacion-cvn-datos-generales.fragment';
import { NOTIFICACION_CVN_ROUTE_PARAMS } from './notificacion-cvn-route-params';

const MSG_PROYECTOS = marker('csp.proyecto');
const MSG_AUTORIZACION = marker('csp.autorizacion');

export const NOTIFICACION_ACTION_LINK_KEY = 'notificacion';

@Injectable()
export class NotificacionCvnActionService extends
  ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
  };

  private datosGenerales: NotificacionCvnDatosGeneralesFragment;

  public readonly id: number;

  private readonly data: INotificacionProyectoExternoCVN;

  get notificacion(): INotificacionProyectoExternoCVN {
    return this.datosGenerales.getValue();
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    public authService: SgiAuthService,
    public dialogService: DialogService,
    service: NotificacionProyectoExternoCvnService,
    documentoService: DocumentoService,
    personaService: PersonaService,
    empresaService: EmpresaService,
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(NOTIFICACION_CVN_ROUTE_PARAMS.ID));
    if (this.id) {
      this.data = route.snapshot.data[NOTIFICACION_DATA_KEY];
      this.enableEdit();
      this.addAutorizacionLink();
      this.addProyectoLink();
    }

    this.dialogService = dialogService;
    this.datosGenerales = new NotificacionCvnDatosGeneralesFragment(
      this.id, logger, service, personaService, documentoService, empresaService, authService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

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

  private addProyectoLink(): void {
    if (!this.data.proyecto?.id) {
      return;
    }
    this.addActionLink({
      title: MSG_PROYECTOS,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      routerLink: ['../..', CSP_ROUTE_NAMES.PROYECTO, this.data.proyecto?.id.toString()],
    });
  }

  private addAutorizacionLink(): void {
    if (!this.data.autorizacion?.id) {
      return;
    }
    this.addActionLink({
      title: MSG_AUTORIZACION,
      titleParams: MSG_PARAMS.CARDINALIRY.SINGULAR,
      routerLink: ['../..', CSP_ROUTE_NAMES.AUTORIZACION, this.data.autorizacion?.id.toString()]
    });
  }
}
