import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { DEFAULT_PREFIX_RECIPIENTS_CSP_SOL_HITOS } from '@core/models/cnf/config-keys';
import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { ISolicitudHito } from '@core/models/csp/solicitud-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { ConfigService } from '@core/services/cnf/config.service';
import { EmailTplService } from '@core/services/com/email-tpl/email-tpl.service';
import { EmailService } from '@core/services/com/email/email.service';
import { SgiApiTaskService } from '@core/services/tp/sgiapitask/sgi-api-task.service';
import { TipoHitoValidator } from '@core/validators/tipo-hito-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { pairwise, startWith, switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const SOLICITUD_HITO_KEY = marker('csp.solicitud-hito');
const SOLICITUD_HITO_COMENTARIO_KEY = marker('csp.solicitud-hito.comentario');
const SOLICITUD_HITO_FECHA_INICIO_KEY = marker('csp.solicitud-hito.fecha');
const SOLICITUD_HITO_TIPO_KEY = marker('csp.solicitud-hito.tipo');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const AVISO_FECHA_ENVIO_KEY = marker('label.aviso.fecha-envio.short');
const AVISO_ASUNTO_KEY = marker('label.aviso.asunto');
const AVISO_CONTENIDO_KEY = marker('label.aviso.contenido.short');
const AVISO_DESTINATARIOS_KEY = marker('label.aviso.destinatarios');

export interface SolicitudHitosModalComponentData {
  hitos: ISolicitudHito[];
  hito: ISolicitudHito;
  idModeloEjecucion: number;
  readonly: boolean;
  unidadGestionId: number;
  tituloConvocatoria: string;
  tituloSolicitud: string;
}

@Component({
  templateUrl: './solicitud-hitos-modal.component.html',
  styleUrls: ['./solicitud-hitos-modal.component.scss']
})
export class SolicitudHitosModalComponent extends DialogFormComponent<SolicitudHitosModalComponentData> implements OnInit, OnDestroy {

  textSaveOrUpdate: string;
  title: string;

  msgParamTipoEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamComentarioEntity = {};
  msgParamFechaEnvioEntity = {};
  msgParamAsuntoEntity = {};
  msgParamContenidoEntity = {};
  msgParamDestinatariosEntity = {};

  get now(): DateTime {
    return DateTime.now().plus({ minute: 15 });
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    matDialogRef: MatDialogRef<SolicitudHitosModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: SolicitudHitosModalComponentData,
    private readonly translate: TranslateService,
    private configService: ConfigService,
    private emailTplService: EmailTplService,
    private emailSErvice: EmailService,
    private sgiApiTaskService: SgiApiTaskService
  ) {
    super(matDialogRef, !!data.hito?.id);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.createValidatorDate(this.data?.hito?.tipoHito);

    const suscription = this.formGroup.get('tipoHito').valueChanges.subscribe((value) => this.createValidatorDate(value));
    this.subscriptions.push(suscription);

    this.subscriptions.push(this.formGroup.get('fechaInicio').valueChanges.subscribe(
      (value: DateTime) => {
        this.validarFecha(value);
        this.createValidatorDate(this.formGroup.get('tipoHito').value);
      }));

    this.textSaveOrUpdate = this.data.hito?.id ? MSG_ACEPTAR : MSG_ANADIR;

    if (!!!this.data.hito?.aviso) {
      this.validarFecha(this.data.hito?.fecha);
    }

    this.formGroup.get('generaAviso').valueChanges.pipe(startWith(!!this.data.hito?.aviso), pairwise()).subscribe(
      ([oldValue, newValue]: [boolean, boolean]) => {
        if (!!oldValue && !!!newValue) {
          if (this.formGroup.get('aviso').enabled) {
            this.formGroup.get('aviso').disable();
          }
          this.clearAviso();
        }
        else if (!!!oldValue && !!newValue) {
          if (this.formGroup.get('aviso').disabled) {
            this.formGroup.get('aviso').enable();
          }
          this.fillDefaultAviso();
        }
      }
    );

    if (this.data.hito?.aviso && this.isLoadEmailRequired(this.data.hito.aviso?.email)) {
      this.emailSErvice.findGenericEmailtTextById(this.data.hito.aviso?.email?.id).subscribe(
        (comunicado) => {
          this.data.hito.aviso.email = comunicado;
          this.formGroup.get('aviso.destinatarios').setValue(comunicado.recipients);
          this.formGroup.get('aviso.asunto').setValue(comunicado.subject);
          this.formGroup.get('aviso.contenido').setValue(comunicado.content);
        }
      );
    }

    if (this.data.hito?.aviso && this.isLoadTareaProgramadaRequired(this.data.hito.aviso?.task)) {
      this.sgiApiTaskService.findSendEmailTaskById(this.data.hito.aviso?.task?.id).subscribe(
        (tareaProgramada) => {
          this.data.hito.aviso.task = tareaProgramada;
          this.formGroup.get('aviso.fechaEnvio').setValue(tareaProgramada.instant);
          this.lockAviso(tareaProgramada.instant);
        }
      );
    }
    else {
      this.lockAviso(this.data.hito?.aviso?.task?.instant);
    }
  }

  private lockAviso(dateRef: DateTime): void {
    // Si no hay fecha de referencia no hacemos nada
    if (!!!dateRef) {
      return;
    }
    if (!!this.data.hito?.id && DateTime.now() >= dateRef) {
      if (this.formGroup.get('generaAviso').enabled) {
        this.formGroup.get('generaAviso').disable();
      }
      if (this.formGroup.get('aviso').enabled) {
        this.formGroup.get('aviso').disable();
      }
    }
  }

  private fillDefaultAviso(): void {
    this.formGroup.get('aviso.fechaEnvio').setValue(this.formGroup.get('fechaInicio').value);
    this.configService.getEmailRecipients(DEFAULT_PREFIX_RECIPIENTS_CSP_SOL_HITOS + this.data.unidadGestionId).subscribe(
      (destinatarios) => {
        this.formGroup.get('aviso.destinatarios').setValue(destinatarios);
      }
    );
    this.emailTplService.processSolicitudHitoTemplate(
      this.data.tituloSolicitud,
      this.data.tituloConvocatoria,
      this.formGroup.get('fechaInicio').value ?? DateTime.now(),
      this.formGroup.get('tipoHito').value?.nombre ?? '',
      this.formGroup.get('comentario').value ?? ''
    ).subscribe(
      (template) => {
        this.formGroup.get('aviso.asunto').setValue(template.subject);
        this.formGroup.get('aviso.contenido').setValue(template.contentText);
      }
    );
  }

  private isLoadEmailRequired(comunicado: IGenericEmailText): boolean {
    if (!!!comunicado?.id) {
      return false;
    }
    if (!!!comunicado.content && !!!comunicado.subject && !!!comunicado.recipients?.length) {
      return true;
    }
  }

  private isLoadTareaProgramadaRequired(tareaProgramada: ISendEmailTask): boolean {
    if (!!!tareaProgramada?.id) {
      return false;
    }
    if (!!!tareaProgramada.instant) {
      return true;
    }
  }

  private clearAviso(): void {
    this.formGroup.get('aviso.fechaEnvio').setValue(null);
    this.formGroup.get('aviso.destinatarios').setValue(null);
    this.formGroup.get('aviso.asunto').setValue(null);
    this.formGroup.get('aviso.contenido').setValue(null);
    this.formGroup.get('aviso.incluirIpsSolicitud').setValue(false);
  }

  private setupI18N(): void {
    this.translate.get(
      SOLICITUD_HITO_COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      SOLICITUD_HITO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      SOLICITUD_HITO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    if (this.data.hito?.tipoHito) {
      this.translate.get(
        SOLICITUD_HITO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        SOLICITUD_HITO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.MALE }
          );
        })
      ).subscribe((value) => this.title = value);
    }

    this.translate.get(
      AVISO_FECHA_ENVIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaEnvioEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.FEMALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
    this.translate.get(
      AVISO_ASUNTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAsuntoEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
    this.translate.get(
      AVISO_CONTENIDO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamContenidoEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
    this.translate.get(
      AVISO_DESTINATARIOS_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamDestinatariosEntity = {
      entity: value,
      ...MSG_PARAMS.GENDER.MALE,
      ...MSG_PARAMS.CARDINALIRY.PLURAL
    });
  }

  /**
   * Validacion de fechas a la hora de seleccionar
   * un tipo de hito en el modal
   * @param tipoHito convocatoria tipoHito
   */
  private createValidatorDate(tipoHito: ITipoHito): void {
    let fechas: DateTime[] = [];

    const convocatoriasHitos = this.data.hitos.filter(hito =>
      hito.tipoHito.id === tipoHito?.id);
    fechas = convocatoriasHitos.map(hito => hito.fecha);

    this.formGroup.setValidators([
      TipoHitoValidator.notInDate('fechaInicio', fechas)
    ]);
  }

  /**
   * Si la fecha actual es inferior - Checkbox disabled
   * Si la fecha actual es superior - Checkbox enable
   */
  private validarFecha(date: DateTime) {
    const control = this.formGroup.get('generaAviso');
    if (!!date && date <= DateTime.now()) {
      if (control.enabled) {
        control.setValue(false);
        control.disable();
      }
    } else {
      if (control.disabled && !this.data.readonly &&
        (!!!this.data.hito?.aviso?.task?.instant ||
          (!!this.data.hito?.aviso?.task?.instant && DateTime.now() <= this.data.hito.aviso.task.instant))
      ) {
        control.enable();
      }
    }
  }

  protected getValue(): SolicitudHitosModalComponentData {
    this.data.hito.comentario = this.formGroup.get('comentario').value;
    this.data.hito.fecha = this.formGroup.get('fechaInicio').value;
    this.data.hito.tipoHito = this.formGroup.get('tipoHito').value;

    if (!!!this.data.hito.aviso && this.formGroup.get('generaAviso').value) {
      this.data.hito.aviso = {
        email: {} as IGenericEmailText,
        task: {} as ISendEmailTask,
        incluirIpsSolicitud: false,
      };
    }

    const fgAviso = this.formGroup.get('aviso');
    if (this.formGroup.get('generaAviso').value) {
      this.data.hito.aviso.email.content = fgAviso.get('contenido').value;
      this.data.hito.aviso.email.subject = fgAviso.get('asunto').value;
      this.data.hito.aviso.email.recipients = fgAviso.get('destinatarios').value;
      this.data.hito.aviso.incluirIpsSolicitud = fgAviso.get('incluirIpsSolicitud').value;
      this.data.hito.aviso.task.instant = fgAviso.get('fechaEnvio').value;
    }
    else {
      this.data.hito.aviso = null;
    }

    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoHito: new FormControl(this.data?.hito?.tipoHito, Validators.required),
      fechaInicio: new FormControl(this.data?.hito?.fecha, Validators.required),
      comentario: new FormControl(this.data?.hito?.comentario, Validators.maxLength(250)),
      generaAviso: new FormControl(!!this.data?.hito?.aviso),
      aviso: new FormGroup({
        fechaEnvio: new FormControl(this.data?.hito?.aviso?.task?.instant, Validators.required),
        asunto: new FormControl(this.data?.hito?.aviso?.email?.subject, Validators.required),
        contenido: new FormControl(this.data?.hito?.aviso?.email?.content, Validators.required),
        incluirIpsSolicitud: new FormControl(this.data?.hito?.aviso?.incluirIpsSolicitud ?? false),
        destinatarios: new FormControl(this.data?.hito?.aviso?.email?.recipients, Validators.required)
      })
    });

    if (!!!this.data?.hito?.aviso) {
      formGroup.get('aviso').disable();
    }
    if (this.data.readonly) {
      formGroup.disable();
    }
    return formGroup;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

}
