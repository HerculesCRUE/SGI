import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { EmailTplService } from '@core/services/com/email-tpl/email-tpl.service';
import { EmailService } from '@core/services/com/email/email.service';
import { ConfigService } from '@core/services/cnf/config.service';
import { SgiApiTaskService } from '@core/services/tp/sgiapitask/sgi-api-task.service';
import { DateValidator } from '@core/validators/date-validator';
import { IRange, RangeValidator } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { pairwise, startWith, switchMap, tap } from 'rxjs/operators';
import { DEFAULT_PREFIX_RECIPIENTS_CSP_CONV_FASES } from '@core/models/cnf/config-keys';
import { IConvocatoriaFaseAviso } from '@core/services/csp/convocatoria-fase/convocatoria-fase-aviso';
import { MatTabGroup } from '@angular/material/tabs';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_FASE_KEY = marker('csp.convocatoria-fase');
const CONVOCATORIA_FASES_FECHA_FIN_KEY = marker('csp.convocatoria-fase.fecha-fin');
const CONVOCATORIA_FASES_FECHA_INICIO_KEY = marker('csp.convocatoria-fase.fecha-inicio');
const CONVOCATORIA_FASES_OBSERVACIONES_KEY = marker('csp.convocatoria-fase.observaciones');
const CONVOCATORIA_FASES_TIPO_KEY = marker('csp.convocatoria-fase.tipo');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const AVISO_FECHA_ENVIO_KEY = marker('label.aviso.fecha-envio.short');
const AVISO_ASUNTO_KEY = marker('label.aviso.asunto');
const AVISO_CONTENIDO_KEY = marker('label.aviso.contenido.short');
const AVISO_DESTINATARIOS_KEY = marker('label.aviso.destinatarios');
export interface ConvocatoriaPlazosFaseModalComponentData {
  plazos: IConvocatoriaFase[];
  plazo: IConvocatoriaFase;
  idModeloEjecucion: number;
  readonly: boolean;
  canEdit: boolean;
  unidadGestionId: number;
  tituloConvocatoria: string;
}
@Component({
  templateUrl: './convocatoria-plazos-fase-modal.component.html',
  styleUrls: ['./convocatoria-plazos-fase-modal.component.scss']
})
export class ConvocatoriaPlazosFaseModalComponent
  extends DialogFormComponent<ConvocatoriaPlazosFaseModalComponentData> implements OnInit {

  @ViewChild('avisosTabGroup', { static: false }) avisosTabGroup: MatTabGroup;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  get minFechaPrimerAviso(): DateTime {
    return DateTime.now().plus({ minute: 15 });
  }

  get minFechaSegundoAviso(): DateTime {
    return this.formGroup.get('aviso1.fechaEnvio')?.value?.plus({ second: 1 });
  }

  textSaveOrUpdate: string;
  title: string;

  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  msgParamTipoEntity = {};
  msgParamObservacionesEntity = {};
  msgParamFechaEnvioEntity = {};
  msgParamAsuntoEntity = {};
  msgParamContenidoEntity = {};
  msgParamDestinatariosEntity = {};

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaPlazosFaseModalComponentData,
    matDialogRef: MatDialogRef<ConvocatoriaPlazosFaseModalComponent>,
    private readonly translate: TranslateService,
    private configService: ConfigService,
    private emailTplService: EmailTplService,
    private emailService: EmailService,
    private sgiApiTaskService: SgiApiTaskService
  ) {
    super(matDialogRef, !data.plazo.fechaInicio);
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.createValidatorDate(this.data?.plazo?.tipoFase);
    const suscription = this.formGroup.controls.tipoFase.valueChanges.pipe(tap((value) => this.createValidatorDate(value))).subscribe();
    this.subscriptions.push(suscription);

    this.setupI18N();
    this.textSaveOrUpdate = this.data.plazo.fechaInicio ? MSG_ACEPTAR : MSG_ANADIR;
    this.initializeAvisos();
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_FASES_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_FASES_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_FASES_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_FASES_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.PLURAL
    ).subscribe((value) => this.msgParamObservacionesEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    if (this.data.plazo.tipoFase) {
      this.translate.get(
        CONVOCATORIA_FASE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        CONVOCATORIA_FASE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).pipe(
        switchMap((value) => {
          return this.translate.get(
            TITLE_NEW_ENTITY,
            { entity: value, ...MSG_PARAMS.GENDER.FEMALE }
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
   * Validacion del rango de fechas a la hora de seleccionar
   * un tipo de fase en el modal
   * @param tipoFase convocatoria tipoFase
   */
  private createValidatorDate(tipoFase: ITipoFase): void {
    let rangoFechas: IRange[] = [];

    const convocatoriasFases = this.data.plazos.filter(plazo =>
      plazo.tipoFase.id === tipoFase?.id);
    rangoFechas = convocatoriasFases.map(
      fase => {
        const rango: IRange = {
          inicio: fase.fechaInicio,
          fin: fase.fechaFin
        };
        return rango;
      }
    );

    this.formGroup.setValidators([
      DateValidator.isAfter('fechaInicio', 'fechaFin', false),
      RangeValidator.notOverlaps('fechaInicio', 'fechaFin', rangoFechas)
    ]);
  }

  protected getValue(): ConvocatoriaPlazosFaseModalComponentData {
    this.data.plazo.fechaInicio = this.formGroup.controls.fechaInicio.value;
    this.data.plazo.fechaFin = this.formGroup.controls.fechaFin.value;
    this.data.plazo.tipoFase = this.formGroup.controls.tipoFase.value;
    this.data.plazo.observaciones = this.formGroup.controls.observaciones.value;

    this.data.plazo.aviso1 = this.buildAvisoFormGroup(this.formGroup.controls.generaAviso1 as FormControl, this.data.plazo.aviso1, this.formGroup.get('aviso1') as FormGroup);
    this.data.plazo.aviso2 = this.buildAvisoFormGroup(this.formGroup.controls.generaAviso2 as FormControl, this.data.plazo.aviso2, this.formGroup.get('aviso2') as FormGroup);

    return this.data;
  }

  protected buildFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      fechaInicio: new FormControl(this.data?.plazo?.fechaInicio, Validators.required),
      fechaFin: new FormControl(this.data?.plazo?.fechaFin, Validators.required),
      tipoFase: new FormControl(this.data?.plazo?.tipoFase, Validators.required),
      observaciones: new FormControl(this.data?.plazo?.observaciones, Validators.maxLength(250)),
      generaAviso1: new FormControl(!!this.data?.plazo?.aviso1),
      generaAviso2: new FormControl(!!this.data?.plazo?.aviso2),
      aviso1: new FormGroup({
        fechaEnvio: new FormControl(this.data?.plazo?.aviso1?.task?.instant, Validators.required),
        asunto: new FormControl(this.data?.plazo?.aviso1?.email?.subject, Validators.required),
        contenido: new FormControl(this.data?.plazo?.aviso1?.email?.content, Validators.required),
        incluirIpsSolicitud: new FormControl(this.data?.plazo?.aviso1?.incluirIpsSolicitud ?? false),
        incluirIpsProyecto: new FormControl(this.data?.plazo?.aviso1?.incluirIpsProyecto ?? false),
        destinatarios: new FormControl(this.data?.plazo?.aviso1?.email?.recipients, Validators.required)
      }),
      aviso2: new FormGroup({
        fechaEnvio: new FormControl(this.data?.plazo?.aviso2?.task?.instant, Validators.required),
        asunto: new FormControl(this.data?.plazo?.aviso2?.email?.subject, Validators.required),
        contenido: new FormControl(this.data?.plazo?.aviso2?.email?.content, Validators.required),
        incluirIpsSolicitud: new FormControl(this.data?.plazo?.aviso2?.incluirIpsSolicitud ?? false),
        incluirIpsProyecto: new FormControl(this.data?.plazo?.aviso2?.incluirIpsProyecto ?? false),
        destinatarios: new FormControl(this.data?.plazo?.aviso2?.email?.recipients, Validators.required)
      })
    });

    if (!!!this.data?.plazo?.aviso1) {
      formGroup.get('aviso1').disable();
    }

    if (!!!this.data?.plazo?.aviso2) {
      formGroup.get('aviso2').disable();
    }

    if (!this.data.canEdit) {
      formGroup.disable();
    }

    return formGroup;
  }

  private buildAvisoFormGroup(mandatoryControl: FormControl, faseAviso: IConvocatoriaFaseAviso, fgAviso: FormGroup): IConvocatoriaFaseAviso {
    if (!!!faseAviso && mandatoryControl.value) {
      faseAviso = {
        email: {} as IGenericEmailText,
        task: {} as ISendEmailTask,
        incluirIpsProyecto: false,
        incluirIpsSolicitud: false,
      };
    }

    if (!!mandatoryControl.value) {
      faseAviso.email.content = fgAviso.get('contenido').value;
      faseAviso.email.subject = fgAviso.get('asunto').value;
      faseAviso.email.recipients = fgAviso.get('destinatarios').value;
      faseAviso.incluirIpsProyecto = fgAviso.get('incluirIpsProyecto').value;
      faseAviso.incluirIpsSolicitud = fgAviso.get('incluirIpsSolicitud').value;
      faseAviso.task.instant = fgAviso.get('fechaEnvio').value;
    }
    else {
      faseAviso = null;
    }

    return faseAviso;
  }

  private initializeAvisos(): void {
    if (!!!this.data.plazo?.aviso1) {
      this.validarFecha(this.data.plazo?.fechaInicio, this.formGroup.controls?.generaAviso1 as FormControl, this.data.plazo.aviso1);
      this.validarFecha(this.data.plazo?.fechaFin, this.formGroup.controls?.generaAviso1 as FormControl, this.data.plazo.aviso1);
    }

    if (!!!this.data.plazo?.aviso2) {
      this.validarFecha(this.data.plazo?.fechaInicio, this.formGroup.controls?.generaAviso2 as FormControl, this.data.plazo.aviso2);
      this.validarFecha(this.data.plazo?.fechaFin, this.formGroup.controls?.generaAviso2 as FormControl, this.data.plazo.aviso2);
    }

    this.checkAndFillDefaultAviso(this.formGroup.controls?.generaAviso1 as FormControl, this.formGroup.get("aviso1") as FormGroup, this.data.plazo?.aviso1);
    this.checkAndFillDefaultAviso(this.formGroup.controls?.generaAviso2 as FormControl, this.formGroup.get("aviso2") as FormGroup, this.data.plazo?.aviso2);

    this.fillEmailData(this.formGroup.get('aviso1') as FormGroup, this.data.plazo?.aviso1);
    this.fillEmailData(this.formGroup.get('aviso2') as FormGroup, this.data.plazo?.aviso2);

    this.checkAndSetFechaEnvio(this.data.plazo?.aviso1, this.formGroup.get('aviso1') as FormGroup, this.formGroup.controls?.generaAviso1 as FormControl);
    this.checkAndSetFechaEnvio(this.data.plazo?.aviso2, this.formGroup.get('aviso2') as FormGroup, this.formGroup.controls?.generaAviso2 as FormControl);
  }

  private lockAviso(dateRef: DateTime, fgAviso: FormGroup, generaAvisoCtrl: FormControl): void {
    // Si no hay fecha de referencia no hacemos nada
    if (!!!dateRef) {
      return;
    }
    if (!!this.data.plazo.id && DateTime.now() >= dateRef) {
      if (generaAvisoCtrl.enabled) {
        generaAvisoCtrl.disable();
      }
      if (fgAviso.enabled) {
        fgAviso.disable();
      }
    }
  }

  private fillDefaultAviso(fgAviso: FormGroup): void {
    fgAviso.get('fechaEnvio').setValue(this.formGroup.get('fechaInicio').value);
    this.configService.getEmailRecipients(DEFAULT_PREFIX_RECIPIENTS_CSP_CONV_FASES + this.data.unidadGestionId).subscribe(
      (destinatarios) => {
        fgAviso.get('destinatarios').setValue(destinatarios);
      }
    );
    this.emailTplService.processConvocatoriaFaseTemplate(
      this.data.tituloConvocatoria,
      this.formGroup.get('fechaInicio').value ?? DateTime.now(),
      this.formGroup.get('fechaFin').value ?? DateTime.now(),
      this.formGroup.get('tipoFase').value?.nombre ?? '',
      this.formGroup.get('observaciones').value ?? ''
    ).subscribe(
      (template) => {
        fgAviso.get('asunto').setValue(template.subject);
        fgAviso.get('contenido').setValue(template.contentText);
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

  private clearAviso(fgAviso: FormGroup): void {
    fgAviso.get('fechaEnvio').setValue(null);
    fgAviso.get('destinatarios').setValue(null);
    fgAviso.get('asunto').setValue(null);
    fgAviso.get('contenido').setValue(null);
    fgAviso.get('incluirIpsSolicitud').setValue(false);
    fgAviso.get('incluirIpsProyecto').setValue(false);
  }

  /**
   * Si la fecha actual es inferior - Checkbox disabled
   * Si la fecha actual es superior - Checkbox enable
   */
  private validarFecha(date: DateTime, generaAvisoControl: FormControl, aviso: any) {

    if (!!date && date <= DateTime.now()) {
      if (generaAvisoControl.enabled) {
        generaAvisoControl.setValue(false);
        generaAvisoControl.disable();
      }
    } else {
      if (generaAvisoControl.disabled && this.data.canEdit &&
        (!!!aviso?.task?.instant ||
          (!!aviso.task?.instant && DateTime.now() <= aviso.task.instant))
      ) {
        generaAvisoControl.enable();
      }
    }
  }

  private checkAndFillDefaultAviso(generaAvisoCtrl: FormControl, fgAviso: FormGroup, dataAviso: IConvocatoriaFaseAviso): void {
    generaAvisoCtrl.valueChanges.pipe(startWith(!!dataAviso), pairwise()).subscribe(
      ([oldValue, newValue]: [boolean, boolean]) => {
        if (!!oldValue && !!!newValue) {
          if (fgAviso.enabled) {
            fgAviso.disable();
            if (this.formGroup.get('aviso2').enabled && fgAviso === this.formGroup.get('aviso1')) {
              this.formGroup.controls.generaAviso2.setValue(false);
            }
          }
          this.clearAviso(fgAviso);
        }
        else if (!!!oldValue && !!newValue) {
          if (fgAviso.disabled) {
            fgAviso.enable();
          }
          this.fillDefaultAviso(fgAviso);
          if (generaAvisoCtrl === this.formGroup.controls?.generaAviso2) {
            this.avisosTabGroup.selectedIndex = 1;
          }
          fgAviso.controls?.fechaEnvio.markAsTouched();
        }
      }
    );
  }

  private fillEmailData(fgAviso: FormGroup, dataAviso: IConvocatoriaFaseAviso) {
    if (dataAviso && this.isLoadEmailRequired(dataAviso?.email)) {
      this.emailService.findGenericEmailtTextById(dataAviso?.email?.id).subscribe(
        (comunicado) => {
          dataAviso.email = comunicado;
          fgAviso.get('destinatarios').setValue(comunicado.recipients);
          fgAviso.get('asunto').setValue(comunicado.subject);
          fgAviso.get('contenido').setValue(comunicado.content);
        }
      );
    }
  }

  private checkAndSetFechaEnvio(dataAviso: IConvocatoriaFaseAviso, fgAviso: FormGroup, generaAvisoCtrl: FormControl): void {
    if (dataAviso && this.isLoadTareaProgramadaRequired(dataAviso.task)) {
      this.sgiApiTaskService.findSendEmailTaskById(dataAviso.task?.id).subscribe(
        (tareaProgramada) => {
          dataAviso.task = tareaProgramada;
          fgAviso.get('fechaEnvio').setValue(tareaProgramada.instant);
          this.lockAviso(tareaProgramada.instant, fgAviso, generaAvisoCtrl);
        }
      );
    }
    else {
      this.lockAviso(dataAviso?.task?.instant, fgAviso, generaAvisoCtrl);
    }
  }

}
