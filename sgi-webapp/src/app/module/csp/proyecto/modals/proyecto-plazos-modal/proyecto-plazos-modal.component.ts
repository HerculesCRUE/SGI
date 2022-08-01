import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTabGroup } from '@angular/material/tabs';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { MSG_PARAMS } from '@core/i18n';
import { IGenericEmailText } from '@core/models/com/generic-email-text';
import { IProyectoFase } from '@core/models/csp/proyecto-fase';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { ISendEmailTask } from '@core/models/tp/send-email-task';
import { ConfigService } from '@core/services/cnf/config.service';
import { EmailTplService } from '@core/services/com/email-tpl/email-tpl.service';
import { EmailService } from '@core/services/com/email/email.service';
import { IProyectoFaseAviso } from '@core/services/csp/proyecto-fase/proyecto-fase-aviso';
import { SgiApiTaskService } from '@core/services/tp/sgiapitask/sgi-api-task.service';
import { DateValidator } from '@core/validators/date-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { IRange, RangeValidator } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { map, pairwise, startWith, switchMap } from 'rxjs/operators';
import { DEFAULT_PREFIX_RECIPIENTS_CSP_PRO_FASES } from '@core/models/cnf/config-keys';
import { Observable, of } from 'rxjs';
import { IConvocatoria } from '@core/models/csp/convocatoria';
import { ConvocatoriaService } from '@core/services/csp/convocatoria.service';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_PLAZO_FECHA_INICIO_KEY = marker('csp.proyecto.plazo.fecha-inicio');
const PROYECTO_PLAZO_FECHA_FIN_KEY = marker('csp.proyecto.plazo.fecha-fin');
const PROYECTO_PLAZO_TIPO_FASE_KEY = marker('csp.proyecto.plazo.tipo-fase');
const PROYECTO_PLAZO_OBSERVACIONES_KEY = marker('csp.proyecto.plazo.observaciones');
const PROYECTO_FASE_KEY = marker('csp.proyecto-fase');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const AVISO_FECHA_ENVIO_KEY = marker('label.aviso.fecha-envio.short');
const AVISO_ASUNTO_KEY = marker('label.aviso.asunto');
const AVISO_CONTENIDO_KEY = marker('label.aviso.contenido.short');
const AVISO_DESTINATARIOS_KEY = marker('label.aviso.destinatarios');

export interface ProyectoPlazosModalComponentData {
  plazos: IProyectoFase[];
  plazo: IProyectoFase;
  idModeloEjecucion: number;
  readonly: boolean;
  unidadGestionId: number;
  tituloProyecto: string;
  convocatoriaId: number;
}

@Component({
  selector: 'sgi-proyecto-plazos-modal',
  templateUrl: './proyecto-plazos-modal.component.html',
  styleUrls: ['./proyecto-plazos-modal.component.scss']
})
export class ProyectoPlazosModalComponent extends DialogFormComponent<ProyectoPlazosModalComponentData> implements OnInit, OnDestroy {

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

  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  msgParamTipoFaseEntity = {};
  msgParamObservacionesEntity = {};
  title: string;
  msgParamFechaEnvioEntity = {};
  msgParamAsuntoEntity = {};
  msgParamContenidoEntity = {};
  msgParamDestinatariosEntity = {};

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: ProyectoPlazosModalComponentData,
    matDialogRef: MatDialogRef<ProyectoPlazosModalComponent>,
    private readonly translate: TranslateService,
    private configService: ConfigService,
    private emailTplService: EmailTplService,
    private emailService: EmailService,
    private sgiApiTaskService: SgiApiTaskService,
    private convocatoriaService: ConvocatoriaService
  ) {
    super(matDialogRef, !!data.plazo?.tipoFase);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    this.subscriptions.push(this.formGroup.controls.tipoFase.valueChanges.subscribe((value) => this.createValidatorDate(value)));
    this.subscriptions.push(this.formGroup.controls.fechaFin.valueChanges.subscribe((value) => this.validatorGeneraAviso(value)));

    this.validatorGeneraAviso(this.formGroup.controls.fechaFin.value);
    this.textSaveOrUpdate = this.data.plazo?.tipoFase ? MSG_ACEPTAR : MSG_ANADIR;
    this.initializeAvisos();
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_PLAZO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_PLAZO_FECHA_FIN_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaFinEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_PLAZO_TIPO_FASE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoFaseEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_PLAZO_OBSERVACIONES_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamObservacionesEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    if (this.data.plazo?.tipoFase) {
      this.translate.get(
        PROYECTO_FASE_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_FASE_KEY,
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
   * Validamos fecha para activar o inactivar el checkbox generaAviso
   */
  private validatorGeneraAviso(fechaFinInput: DateTime) {
    const fechaActual = DateTime.now();
    const fechaFin = fechaFinInput;
    if (fechaFin <= fechaActual) {
      this.formGroup.get('generaAviso1').disable();
      this.formGroup.get('generaAviso1').setValue(false);
    } else {
      this.formGroup.get('generaAviso1').enable();
    }
  }

  /**
   * Validacion del rango de fechas a la hora de seleccionar
   * un tipo de fase en el modal
   * @param tipoFase proyecto tipoFase
   */
  private createValidatorDate(tipoFase: ITipoFase): void {
    let rangoFechas: IRange[] = [];

    const proyectoFases = this.data.plazos.filter(plazo =>
      plazo.tipoFase.id === tipoFase?.id);
    rangoFechas = proyectoFases.map(
      fase => {
        const rango: IRange = {
          inicio: fase.fechaInicio,
          fin: fase.fechaFin
        };
        return rango;
      }
    );

    this.formGroup.setValidators([
      DateValidator.isAfter('fechaInicio', 'fechaFin'),
      DateValidator.isBefore('fechaFin', 'fechaInicio'),
      RangeValidator.notOverlaps('fechaInicio', 'fechaFin', rangoFechas)
    ]);
  }

  protected getValue(): ProyectoPlazosModalComponentData {
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
      fechaInicio: new FormControl(this.data?.plazo?.fechaInicio, [Validators.required]),
      fechaFin: new FormControl(this.data?.plazo?.fechaFin, Validators.required),
      tipoFase: new FormControl(this.data?.plazo?.tipoFase, [Validators.required, new NullIdValidador().isValid()]),
      observaciones: new FormControl(this.data?.plazo?.observaciones, [Validators.maxLength(250)]),
      generaAviso1: new FormControl(!!this.data?.plazo?.aviso1),
      generaAviso2: new FormControl(!!this.data?.plazo?.aviso2),
      aviso1: new FormGroup({
        fechaEnvio: new FormControl(this.data?.plazo?.aviso1?.task?.instant, Validators.required),
        asunto: new FormControl(this.data?.plazo?.aviso1?.email?.subject, Validators.required),
        contenido: new FormControl(this.data?.plazo?.aviso1?.email?.content, Validators.required),
        incluirIpsProyecto: new FormControl(this.data?.plazo?.aviso1?.incluirIpsProyecto ?? false),
        destinatarios: new FormControl(this.data?.plazo?.aviso1?.email?.recipients, Validators.required)
      }),
      aviso2: new FormGroup({
        fechaEnvio: new FormControl(this.data?.plazo?.aviso2?.task?.instant, Validators.required),
        asunto: new FormControl(this.data?.plazo?.aviso2?.email?.subject, Validators.required),
        contenido: new FormControl(this.data?.plazo?.aviso2?.email?.content, Validators.required),
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

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  private buildAvisoFormGroup(mandatoryControl: FormControl, faseAviso: IProyectoFaseAviso, fgAviso: FormGroup): IProyectoFaseAviso {
    if (!!!faseAviso && mandatoryControl.value) {
      faseAviso = {
        email: {} as IGenericEmailText,
        task: {} as ISendEmailTask,
        incluirIpsProyecto: false
      };
    }

    if (!!mandatoryControl.value) {
      faseAviso.email.content = fgAviso.get('contenido').value;
      faseAviso.email.subject = fgAviso.get('asunto').value;
      faseAviso.email.recipients = fgAviso.get('destinatarios').value;
      faseAviso.incluirIpsProyecto = fgAviso.get('incluirIpsProyecto').value;
      faseAviso.task.instant = fgAviso.get('fechaEnvio').value;
    }
    else {
      faseAviso = null;
    }

    return faseAviso;
  }

  private initializeAvisos(): void {
    if (!!!this.data.plazo?.aviso1) {
      this.validarFecha(this.data.plazo?.fechaInicio, this.formGroup.controls?.generaAviso1 as FormControl, this.data.plazo?.aviso1);
      this.validarFecha(this.data.plazo?.fechaFin, this.formGroup.controls?.generaAviso1 as FormControl, this.data.plazo?.aviso1);
    }

    if (!!!this.data.plazo?.aviso2) {
      this.validarFecha(this.data.plazo?.fechaInicio, this.formGroup.controls?.generaAviso2 as FormControl, this.data.plazo?.aviso2);
      this.validarFecha(this.data.plazo?.fechaFin, this.formGroup.controls?.generaAviso2 as FormControl, this.data.plazo?.aviso2);
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

  private fillDefaultAviso(fgAviso: FormGroup, tituloConvocatoria: string): void {
    fgAviso.get('fechaEnvio').setValue(this.formGroup.get('fechaInicio').value);
    this.configService.getEmailRecipients(DEFAULT_PREFIX_RECIPIENTS_CSP_PRO_FASES + this.data.unidadGestionId).subscribe(
      (destinatarios) => {
        fgAviso.get('destinatarios').setValue(destinatarios);
      }
    );
    this.emailTplService.processProyectoFaseTemplate(
      this.data.tituloProyecto,
      tituloConvocatoria,
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
      if (generaAvisoControl.disabled && !this.data.readonly &&
        (!!!aviso?.task?.instant ||
          (!!aviso.task?.instant && DateTime.now() <= aviso.task.instant))
      ) {
        generaAvisoControl.enable();
      }
    }
  }

  private checkAndFillDefaultAviso(generaAvisoCtrl: FormControl, fgAviso: FormGroup, dataAviso: IProyectoFaseAviso): void {
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
          this.getTituloConvocatoria().subscribe((titulo: string) => this.fillDefaultAviso(fgAviso, titulo));
          if (generaAvisoCtrl === this.formGroup.controls?.generaAviso2) {
            this.avisosTabGroup.selectedIndex = 1;
          }
          fgAviso.controls?.fechaEnvio.markAsTouched();
        }
      }
    );
  }

  private fillEmailData(fgAviso: FormGroup, dataAviso: IProyectoFaseAviso) {
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

  private checkAndSetFechaEnvio(dataAviso: IProyectoFaseAviso, fgAviso: FormGroup, generaAvisoCtrl: FormControl): void {
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

  private getTituloConvocatoria(): Observable<string> {
    if (!this.data.convocatoriaId) {
      return of('');
    }
    return this.convocatoriaService.findById(this.data.convocatoriaId).pipe(
      map((convocatoria: IConvocatoria) => convocatoria.titulo)
    );
  }

}
