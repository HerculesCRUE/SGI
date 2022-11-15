import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { ISolicitudRrhhTutor } from '@core/models/csp/solicitud-rrhh-tutor';
import { IEmail } from '@core/models/sgp/email';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { SolicitudPublicActionService } from '../../solicitud-public.action.service';
import { SolicitudRrhhTutorPublicFragment } from './solicitud-rrhh-tutor-public.fragment';

const SOLICITUD_RRHH_TUTOR_KEY = marker('csp.solicitud.solicitud-rrhh.tutor');
const SOLICITUD_RRHH_NOMBRE_KEY = marker('csp.solicitud.solicitud-rrhh.nombre');
const SOLICITUD_RRHH_APELLIDOS_KEY = marker('csp.solicitud.solicitud-rrhh.apellidos');
const SOLICITUD_RRHH_CATEGORIA_KEY = marker('csp.solicitud.solicitud-rrhh.categoria');
const SOLICITUD_RRHH_DEPARTAMENTO_KEY = marker('csp.solicitud.solicitud-rrhh.departamento');
const SOLICITUD_RRHH_CENTRO_KEY = marker('csp.solicitud.solicitud-rrhh.centro');
const SOLICITUD_RRHH_TELEFONO_KEY = marker('csp.solicitud.solicitud-rrhh.telefono');
const SOLICITUD_RRHH_EMAIL_KEY = marker('csp.solicitud.solicitud-rrhh.email');

@Component({
  selector: 'sgi-solicitud-rrhh-tutor-public',
  templateUrl: './solicitud-rrhh-tutor-public.component.html',
  styleUrls: ['./solicitud-rrhh-tutor-public.component.scss']
})
export class SolicitudRrhhTutorPublicComponent extends FormFragmentComponent<ISolicitudRrhhTutor> implements OnInit {
  formPart: SolicitudRrhhTutorPublicFragment;

  private subscriptions = [] as Subscription[];

  msgParamTutorEntity = {};
  msgParamNombreEntity = {};
  msgParamApellidosEntity = {};
  msgParamCategoriaEntity = {};
  msgParamDepartamentoEntity = {};
  msgParamTelefonoEntity = {};
  msgParamEmailEntity = {};
  msgParamCentroEntity = {};

  emails = new MatTableDataSource<IEmail>();
  columnsEmails = ['email'];

  telefonos = new MatTableDataSource<string>();
  columnsTelefonos = ['telefono'];

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get tipoColectivoTutor() {
    return TipoColectivo.TUTOR_CSP;
  }

  constructor(
    protected actionService: SolicitudPublicActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.TUTOR, actionService);
    this.formPart = this.fragment as SolicitudRrhhTutorPublicFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadEmails();
    this.loadTelefonos();
  }

  private loadEmails() {
    this.subscriptions.push(this.formPart.tutorEmails$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.emails.data = [];
      } else {
        this.emails.data = data;
      }
    }
    ));
  }

  private loadTelefonos() {
    this.subscriptions.push(this.formPart.tutorTelefonos$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.telefonos.data = [];
      } else {
        this.telefonos.data = data;
      }
    }
    ));
  }

  private setupI18N(): void {

    this.translate.get(
      SOLICITUD_RRHH_TUTOR_KEY
    ).subscribe((value) =>
      this.msgParamTutorEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_NOMBRE_KEY
    ).subscribe((value) =>
      this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_APELLIDOS_KEY
    ).subscribe((value) =>
      this.msgParamApellidosEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL }
    );

    this.translate.get(
      SOLICITUD_RRHH_CATEGORIA_KEY
    ).subscribe((value) =>
      this.msgParamCategoriaEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_DEPARTAMENTO_KEY
    ).subscribe((value) =>
      this.msgParamDepartamentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_CENTRO_KEY
    ).subscribe((value) =>
      this.msgParamCentroEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_TELEFONO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamTelefonoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_EMAIL_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) =>
      this.msgParamEmailEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

  }

}
