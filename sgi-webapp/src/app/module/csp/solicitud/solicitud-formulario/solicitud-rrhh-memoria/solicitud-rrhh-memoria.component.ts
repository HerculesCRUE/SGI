import { Component, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudRrhhMemoria } from '@core/models/csp/solicitud-rrhh-memoria';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudRrhhMemoriaFragment } from './solicitud-rrhh-memoria.fragment';

const SOLICITUD_RRHH_TITULO_TRABAJO_KEY = marker('csp.solicitud.solicitud-rrhh.titulo-trabajo');
const SOLICITUD_RRHH_RESUMEN_KEY = marker('csp.solicitud.solicitud-rrhh.resumen');
const SOLICITUD_RRHH_OBSERVACIONES_KEY = marker('csp.solicitud.solicitud-rrhh.observaciones');

@Component({
  selector: 'sgi-solicitud-rrhh-memoria',
  templateUrl: './solicitud-rrhh-memoria.component.html',
  styleUrls: ['./solicitud-rrhh-memoria.component.scss']
})
export class SolicitudRrhhMemoriaComponent extends FormFragmentComponent<ISolicitudRrhhMemoria> implements OnInit {
  formPart: SolicitudRrhhMemoriaFragment;

  msgParamTituloTrabajoEntity = {};
  msgParamResumenEntity = {};
  msgParamObservacionesEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get tipoColectivoTutor() {
    return TipoColectivo.TUTOR_CSP;
  }

  constructor(
    public readonly actionService: SolicitudActionService,
    public authService: SgiAuthService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.MEMORIA, actionService);
    this.formPart = this.fragment as SolicitudRrhhMemoriaFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
  }

  private setupI18N(): void {

    this.translate.get(
      SOLICITUD_RRHH_TITULO_TRABAJO_KEY
    ).subscribe((value) =>
      this.msgParamTituloTrabajoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_RESUMEN_KEY
    ).subscribe((value) =>
      this.msgParamResumenEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_OBSERVACIONES_KEY
    ).subscribe((value) =>
      this.msgParamObservacionesEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL }
    );

  }

}
