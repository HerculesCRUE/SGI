import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEmail } from '@core/models/sgp/email';
import { TranslateService } from '@ngx-translate/core';
import { SgiAuthService } from '@sgi/framework/auth';
import { Subscription } from 'rxjs';
import { ClasificacionModalComponent, TipoClasificacion } from 'src/app/esb/sgo/shared/clasificacion-modal/clasificacion-modal.component';
import { TipoColectivo } from 'src/app/esb/sgp/shared/select-persona/select-persona.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { ISolicitudSolicitanteRrhh, SolicitudRrhhAreaAnepListado, SolicitudRrhhSolitanteFragment } from './solicitud-rrhh-solicitante.fragment';

const SOLICITUD_RRHH_SOLICITANTE_KEY = marker('csp.solicitud.solicitud-rrhh.solicitante');
const SOLICITUD_RRHH_NOMBRE_KEY = marker('csp.solicitud.solicitud-rrhh.nombre');
const SOLICITUD_RRHH_APELLIDOS_KEY = marker('csp.solicitud.solicitud-rrhh.apellidos');
const SOLICITUD_RRHH_NUMERO_DOCUMENTO_KEY = marker('csp.solicitud.solicitud-rrhh.numero-documento');
const SOLICITUD_RRHH_TIPO_DOCUMENTO_KEY = marker('csp.solicitud.solicitud-rrhh.tipo-documento');
const SOLICITUD_RRHH_TELEFONO_KEY = marker('csp.solicitud.solicitud-rrhh.telefono');
const SOLICITUD_RRHH_EMAIL_KEY = marker('csp.solicitud.solicitud-rrhh.email');
const SOLICITUD_RRHH_AREA_ANEP_KEY = marker('csp.solicitud.solicitud-rrhh.area-anep');

@Component({
  selector: 'sgi-solicitud-rrhh-solicitante',
  templateUrl: './solicitud-rrhh-solicitante.component.html',
  styleUrls: ['./solicitud-rrhh-solicitante.component.scss']
})
export class SolicitudRrhhSolitanteComponent extends FormFragmentComponent<ISolicitudSolicitanteRrhh> implements OnInit {
  formPart: SolicitudRrhhSolitanteFragment;

  private subscriptions = [] as Subscription[];

  msgParamSolicitanteEntity = {};
  msgParamNombreEntity = {};
  msgParamApellidosEntity = {};
  msgParamNumeroDocumentoEntity = {};
  msgParamTipoDocumentoEntity = {};
  msgParamTelefonoEntity = {};
  msgParamEmailEntity = {};
  msgParamAreaAnepEntity = {};

  areasAnep = new MatTableDataSource<SolicitudRrhhAreaAnepListado>();
  columnsAreasAnep = ['niveles', 'areaAnep', 'acciones'];

  emails = new MatTableDataSource<IEmail>();
  columnsEmails = ['email'];

  telefonos = new MatTableDataSource<string>();
  columnsTelefonos = ['telefono'];

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get tipoColectivoSolicitante() {
    return TipoColectivo.SOLICITANTE_CSP;
  }

  constructor(
    protected actionService: SolicitudActionService,
    private matDialog: MatDialog,
    public authService: SgiAuthService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.SOLICITANTE, actionService);
    this.formPart = this.fragment as SolicitudRrhhSolitanteFragment;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.loadAreasAnep();
    this.loadEmails();
    this.loadTelefonos();
  }

  deleteAreaAnep() {
    this.formPart.deleteAreaAnep();
  }

  openModal(): void {
    const config = {
      data: {
        selectedClasificaciones: [],
        tipoClasificacion: TipoClasificacion.AREAS_ANEP,
        multiSelect: false
      }
    };
    const dialogRef = this.matDialog.open(ClasificacionModalComponent, config);
    dialogRef.afterClosed().subscribe(
      (clasificaciones) => {
        if (!!clasificaciones && clasificaciones.length === 1) {
          this.formPart.updateAreaAnep(clasificaciones[0]);
        }
      }
    );
  }

  private setupI18N(): void {

    this.translate.get(
      SOLICITUD_RRHH_SOLICITANTE_KEY
    ).subscribe((value) =>
      this.msgParamSolicitanteEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
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
      SOLICITUD_RRHH_NUMERO_DOCUMENTO_KEY
    ).subscribe((value) =>
      this.msgParamNumeroDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

    this.translate.get(
      SOLICITUD_RRHH_TIPO_DOCUMENTO_KEY
    ).subscribe((value) =>
      this.msgParamTipoDocumentoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
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

    this.translate.get(
      SOLICITUD_RRHH_AREA_ANEP_KEY
    ).subscribe((value) =>
      this.msgParamAreaAnepEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR }
    );

  }


  private loadAreasAnep() {
    this.subscriptions.push(this.formPart.areasAnep$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.areasAnep.data = [];
      } else {
        this.areasAnep.data = data;
      }
    }
    ));
  }

  private loadEmails() {
    this.subscriptions.push(this.formPart.solicitanteEmails$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.emails.data = [];
      } else {
        this.emails.data = data;
      }
    }
    ));
  }

  private loadTelefonos() {
    this.subscriptions.push(this.formPart.solicitanteTelefonos$.subscribe((data) => {
      if (!data || data.length === 0) {
        this.telefonos.data = [];
      } else {
        this.telefonos.data = data;
      }
    }
    ));
  }

}
