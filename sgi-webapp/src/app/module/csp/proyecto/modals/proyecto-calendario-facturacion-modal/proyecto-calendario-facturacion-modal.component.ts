import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { DialogFormComponent } from '@core/component/dialog-form.component';
import { SelectValue } from '@core/component/select-common/select-common.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEstadoValidacionIP, TIPO_ESTADO_VALIDACION_MAP, TipoEstadoValidacion } from '@core/models/csp/estado-validacion-ip';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { IProyectoSge } from '@core/models/sge/proyecto-sge';
import { TipoFacturacionService } from '@core/services/csp/tipo-facturacion/tipo-facturacion.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { IProyectoFacturacionData } from '../../proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.fragment';

export enum DialogAction {
  NEW = 'NEW', EDIT = 'EDIT', VALIDAR_IP = 'VALIDAR_IP'
}
export interface IProyectoCalendarioFacturacionModalData {
  proyectoId: number;
  proyectoFacturacion: IProyectoFacturacionData;
  porcentajeIVA?: number;
  action: DialogAction;
  proyectosSge: IProyectoSge[];
  isCalendarioFacturacionSgeEnabled: boolean;
}

const PROYECTO_CALENDARIO_FACTURACION_KEY = marker('csp.proyecto-calendario-facturacion.item');
const PROYECTO_CALENDARIO_FACTURACION_FECHA_CONFORMIDAD_KEY = marker('csp.proyecto-calendario-facturacion.fecha-emision');
const PROYECTO_CALENDARIO_FACTURACION_FECHA_EMISION_KEY = marker('csp.proyecto-calendario-facturacion.fecha-conformidad');
const PROYECTO_CALENDARIO_FACTURACION_NUMERO_PREVISION_KEY = marker('csp.proyecto-calendario-facturacion.numero-prevision');
const PROYECTO_CALENDARIO_FACTURACION_IMPORTE_BASE_KEY = marker('csp.proyecto-calendario-facturacion.importe-base');
const PROYECTO_CALENDARIO_FACTURACION_PORCENTAJE_IVA_KEY = marker('csp.proyecto-calendario-facturacion.iva');
const PROYECTO_CALENDARIO_FACTURACION_COMENTARIO_KEY = marker('csp.proyecto-calendario-facturacion.comentario');
const PROYECTO_CALENDARIO_FACTURACION_MOTIVO_RECHAZO_KEY = marker('csp.proyecto-calendario-facturacion.motivo-rechazo');
const PROYECTO_CALENDARIO_FACTURACION_NUEVO_ESTADO_VALIDACION_IP_KEY = marker('csp.proyecto-calendario-facturacion.nuevo-estado-validacion-ip');
const PROYECTO_CALENDARIO_FACTURACION_VALIDACION_IP_KEY = marker('csp.proyecto-calendario-facturacion.validacion-ip');
const PROYECTO_CALENDARIO_FACTURACION_IDENTIFICADOR_SGE_KEY = marker('csp.proyecto-calendario-facturacion.identificador-sge');
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

const COMENTARIO_MAX_LENGTH = 1024;

@Component({
  selector: 'sgi-proyecto-calendario-facturacion-modal',
  templateUrl: './proyecto-calendario-facturacion-modal.component.html',
  styleUrls: ['./proyecto-calendario-facturacion-modal.component.scss']
})
export class ProyectoCalendarioFacturacionModalComponent extends DialogFormComponent<IProyectoCalendarioFacturacionModalData> implements OnInit {

  msgParamFechaEmisionEntity = {};
  msgParamComentarioEntity = {};
  msgParamNumeroPrevisionEntity = {};
  msgParamImporteBaseEntity = {};
  msgParamPorcentajeIVAEntity = {};
  msgParamNuevoEstadoValidacionIPEntity = {};
  msgParamMotivoRechazoEntity = {};
  msgParamFechaConformidadEntity = {};
  msgParamIdentificadorSge = {};

  textSaveOrUpdate: string;
  title: string;

  public tiposFacturacion$: BehaviorSubject<ITipoFacturacion[]> = new BehaviorSubject<ITipoFacturacion[]>([]);

  public get TIPO_VALIDACION_IP_MAP() {
    return TIPO_ESTADO_VALIDACION_MAP;
  }

  public get DIALOG_ACTION() {
    return DialogAction;
  }

  public readonly showMensajeMotivoRechazo$ = new BehaviorSubject<boolean>(false);
  public readonly showIndentificadorSge$ = new BehaviorSubject<boolean>(false);

  public readonly TIPO_ESTADO_VALIDACION_ESTADO_NOTIFICADA_MAP: Map<TipoEstadoValidacion, string> = new Map([
    [TipoEstadoValidacion.VALIDADA, marker('csp.tipo-estado-validacion.VALIDADA')],
    [TipoEstadoValidacion.RECHAZADA, marker('csp.tipo-estado-validacion.RECHAZADA')]
  ]);

  constructor(
    private readonly logger: NGXLogger,
    @Inject(MAT_DIALOG_DATA) public data: IProyectoCalendarioFacturacionModalData,
    matDialogRef: MatDialogRef<ProyectoCalendarioFacturacionModalComponent>,
    private readonly translate: TranslateService,
    private tipoFacturacionService: TipoFacturacionService
  ) {
    super(matDialogRef, !!data?.proyectoFacturacion?.id);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18n();
    this.loadTiposFacturacion();
  }

  protected getValue(): IProyectoCalendarioFacturacionModalData {

    if (!this.data.proyectoFacturacion) {
      this.data.proyectoFacturacion = {} as IProyectoFacturacionData;
    }

    this.data.proyectoFacturacion = {
      ...this.data.proyectoFacturacion,
      numeroPrevision: this.formGroup.controls.numeroPrevision.value,
      estadoValidacionIP: {
        id: this.data.action === DialogAction.VALIDAR_IP ? null : this.data.proyectoFacturacion?.estadoValidacionIP?.id,
        estado: this.data.action === DialogAction.VALIDAR_IP
          ? this.formGroup.controls.nuevoEstadoValidacionIP.value
          : this.formGroup.controls.validacionIP.value,
        comentario: this.data.action === DialogAction.VALIDAR_IP
          && this.formGroup.controls.nuevoEstadoValidacionIP.value === TipoEstadoValidacion.RECHAZADA
          ? this.formGroup.controls.mensajeMotivoRechazo.value
          : this.data.proyectoFacturacion?.estadoValidacionIP?.comentario,
        proyectoFacturacionId: this.data.proyectoFacturacion.id
      } as IEstadoValidacionIP,
      fechaEmision: this.formGroup.controls.fechaEmision.value,
      fechaConformidad: this.formGroup.controls.fechaConformidad.value,
      importeBase: this.formGroup.controls.importeBase.value,
      porcentajeIVA: this.formGroup.controls.porcentajeIVA.value,
      comentario: this.formGroup.controls.comentario.value,
      tipoFacturacion: this.formGroup.controls.tipoFacturacion.value,
      proyectoProrroga: this.formGroup.controls.proyectoProrroga.value,
      proyectoSgeRef: this.formGroup.controls.identificadorSge.value?.id
    };
    return this.data;
  }

  protected buildFormGroup(): FormGroup {

    const data = this.data?.proyectoFacturacion;

    const identificadorSgeUnico = (this.data.proyectosSge?.length ?? 0) !== 1 ? null : this.data.proyectosSge[0];

    const form = new FormGroup({
      numeroPrevision: new FormControl({ value: data?.numeroPrevision, disabled: true }, [Validators.required]),
      validacionIP: new FormControl({ value: data?.estadoValidacionIP?.estado, disabled: true }, [Validators.required]),
      fechaConformidad: new FormControl(data?.fechaConformidad),
      fechaEmision: new FormControl(data?.fechaEmision, [Validators.required]),
      importeBase: new FormControl(data?.importeBase, [Validators.required]),
      porcentajeIVA: new FormControl(isNaN(data?.porcentajeIVA) ? this.data?.porcentajeIVA : data?.porcentajeIVA, [Validators.required, Validators.pattern('^[0-9]*$'), Validators.min(0), Validators.max(100)]),
      comentario: new FormControl(data?.comentario, [Validators.maxLength(COMENTARIO_MAX_LENGTH)]),
      tipoFacturacion: new FormControl(data?.tipoFacturacion),
      proyectoProrroga: new FormControl(data?.proyectoProrroga),
      nuevoEstadoValidacionIP: new FormControl(null),
      mensajeMotivoRechazo: new FormControl(''),
      identificadorSge: new FormControl(data?.proyectoSgeRef ? { id: data.proyectoSgeRef } as IProyectoSge : identificadorSgeUnico)
    });


    if (this.data.isCalendarioFacturacionSgeEnabled && (this.data.action === DialogAction.VALIDAR_IP || this.data.action === DialogAction.EDIT) && identificadorSgeUnico) {
      form.controls.identificadorSge.disable({ emitEvent: false });
    }

    if (form.controls.validacionIP.value === TipoEstadoValidacion.VALIDADA) {
      form.controls.fechaConformidad.setValidators(Validators.required);
      form.controls.identificadorSge.setValidators(Validators.required);
      this.showIndentificadorSge$.next(true);
    }

    if (this.data.action === DialogAction.VALIDAR_IP) {
      form.controls.numeroPrevision.disable({ emitEvent: false });
      form.controls.validacionIP.disable({ emitEvent: false });
      form.controls.fechaConformidad.disable({ emitEvent: false });
      form.controls.fechaEmision.disable({ emitEvent: false });
      form.controls.importeBase.disable({ emitEvent: false });
      form.controls.porcentajeIVA.disable({ emitEvent: false });
      form.controls.comentario.disable({ emitEvent: false });
      form.controls.tipoFacturacion.disable({ emitEvent: false });
      form.controls.proyectoProrroga.disable({ emitEvent: false });
      form.controls.nuevoEstadoValidacionIP.setValidators([Validators.required]);
    }

    if (
      this.data.action !== DialogAction.NEW
      && this.data.isCalendarioFacturacionSgeEnabled
      && this.data.proyectoFacturacion.estadoValidacionIP?.estado === TipoEstadoValidacion.VALIDADA
      && this.data.proyectoFacturacion.numeroFacturaEmitida
    ) {
      form.disable();
    }

    this.subscriptions.push(
      form.controls.nuevoEstadoValidacionIP.valueChanges
        .subscribe((newEstado: TipoEstadoValidacion) => {
          const isRechazada = newEstado === TipoEstadoValidacion.RECHAZADA;
          this.showMensajeMotivoRechazo$.next(isRechazada);
          form.controls.mensajeMotivoRechazo.setValidators(isRechazada
            ? [Validators.required, Validators.maxLength(COMENTARIO_MAX_LENGTH)] : []);
          form.controls.mensajeMotivoRechazo.updateValueAndValidity();

          if (this.data.isCalendarioFacturacionSgeEnabled) {
            const isValidada = newEstado === TipoEstadoValidacion.VALIDADA;
            this.showIndentificadorSge$.next(newEstado === TipoEstadoValidacion.VALIDADA);
            form.controls.identificadorSge.setValidators(isValidada ? [Validators.required] : []);
            form.controls.identificadorSge.updateValueAndValidity();
          }
        })
    );

    return form;
  }

  private setupI18n(): void {
    switch (this.data.action) {
      case DialogAction.EDIT:
        this.translate.get(
          PROYECTO_CALENDARIO_FACTURACION_KEY,
          MSG_PARAMS.CARDINALIRY.SINGULAR
        ).subscribe((value) => this.title = value);
        break;
      case DialogAction.NEW:
        this.translate.get(
          PROYECTO_CALENDARIO_FACTURACION_KEY,
          MSG_PARAMS.CARDINALIRY.SINGULAR
        ).pipe(
          switchMap((value) => {
            return this.translate.get(
              TITLE_NEW_ENTITY,
              { entity: value, ...MSG_PARAMS.GENDER.MALE }
            );
          })
        ).subscribe((value) => this.title = value);
        break;
      case DialogAction.VALIDAR_IP:
        this.translate.get(
          PROYECTO_CALENDARIO_FACTURACION_VALIDACION_IP_KEY,
          MSG_PARAMS.CARDINALIRY.SINGULAR
        ).subscribe((value) => this.title = value);
        break;
    }

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_FECHA_EMISION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaEmisionEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_FECHA_CONFORMIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaConformidadEntity =
      { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity =
      { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_NUMERO_PREVISION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(value => this.msgParamNumeroPrevisionEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_IMPORTE_BASE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(value => this.msgParamImporteBaseEntity = {
      entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.MALE
    });

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_PORCENTAJE_IVA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(value => this.msgParamPorcentajeIVAEntity = {
      entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.MALE
    });

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_NUEVO_ESTADO_VALIDACION_IP_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(value => this.msgParamNuevoEstadoValidacionIPEntity = {
      entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.MALE
    });

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_MOTIVO_RECHAZO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe(value => this.msgParamMotivoRechazoEntity = {
      entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.MALE
    });

    this.translate.get(
      PROYECTO_CALENDARIO_FACTURACION_IDENTIFICADOR_SGE_KEY
    ).subscribe((value) => this.msgParamIdentificadorSge = {
      entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.MALE
    });

    this.textSaveOrUpdate = this.data?.proyectoFacturacion?.id ? MSG_ACEPTAR : MSG_ANADIR;
  }

  public canShowValidacionIP(): boolean {
    return !!this.data.proyectoFacturacion?.id;
  }

  public canShowFechaConformidad(): boolean {
    return this.data?.action === DialogAction.EDIT && this.data?.proyectoFacturacion?.estadoValidacionIP.estado === TipoEstadoValidacion.VALIDADA;
  }

  displayerIdentificadorSge(proyectoSge: IProyectoSge): string {
    return proyectoSge?.id;
  }

  sorterIdentificadorSge(o1: SelectValue<IProyectoSge>, o2: SelectValue<IProyectoSge>): number {
    return o1?.displayText.toString().localeCompare(o2?.displayText.toString());
  }

  comparerIdentificadorSge(o1: IProyectoSge, o2: IProyectoSge): boolean {
    if (o1 && o2) {
      return o1?.id === o2?.id;
    }
    return o1 === o2;
  }

  private loadTiposFacturacion() {
    this.subscriptions.push(
      this.tipoFacturacionService.findAll()
        .pipe(
          map(response => response.items),
          catchError(error => {
            this.logger.error(error);
            return error;
          })
        ).subscribe((tipos: ITipoFacturacion[]) => this.tiposFacturacion$.next(tipos)));
  }
}
