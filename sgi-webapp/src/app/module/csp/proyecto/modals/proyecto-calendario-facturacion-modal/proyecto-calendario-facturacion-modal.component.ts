import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IEstadoValidacionIP, TipoEstadoValidacion, TIPO_ESTADO_VALIDACION_MAP } from '@core/models/csp/estado-validacion-ip';
import { ITipoFacturacion } from '@core/models/csp/tipo-facturacion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ProyectoFacturacionService } from '@core/services/csp/proyecto-facturacion/proyecto-facturacion.service';
import { TipoFacturacionService } from '@core/services/csp/tipo-facturacion/tipo-facturacion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { IProyectoFacturacionData } from '../../proyecto-formulario/proyecto-calendario-facturacion/proyecto-calendario-facturacion.fragment';

export enum DialogAction {
  NEW = 'NEW', EDIT = 'EDIT', VALIDAR_IP = 'VALIDAR_IP'
}
export interface IProyectoCalendarioFacturacionModalData {
  proyectoFacturacion: IProyectoFacturacionData;
  porcentajeIVA?: number;
  action: DialogAction;
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
const TITLE_NEW_ENTITY = marker('title.new.entity');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');

const COMENTARIO_MAX_LENGTH = 1024;
@Component({
  selector: 'sgi-proyecto-calendario-facturacion-modal',
  templateUrl: './proyecto-calendario-facturacion-modal.component.html',
  styleUrls: ['./proyecto-calendario-facturacion-modal.component.scss']
})
export class ProyectoCalendarioFacturacionModalComponent extends
  BaseModalComponent<IProyectoCalendarioFacturacionModalData, ProyectoCalendarioFacturacionModalComponent> implements OnInit, OnDestroy {

  fxLayoutProperties: FxLayoutProperties;
  fxLayoutProperties2: FxLayoutProperties;

  msgParamFechaEmisionEntity = {};
  msgParamComentarioEntity = {};
  msgParamNumeroPrevisionEntity = {};
  msgParamImporteBaseEntity = {};
  msgParamPorcentajeIVAEntity = {};
  msgParamNuevoEstadoValidacionIPEntity = {};
  msgParamMotivoRechazoEntity = {};
  msgParamFechaConformidadEntity = {};

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

  public readonly TIPO_ESTADO_VALIDACION_ESTADO_NOTIFICADA_MAP: Map<TipoEstadoValidacion, string> = new Map([
    [TipoEstadoValidacion.VALIDADA, marker('csp.tipo-estado-validacion.VALIDADA')],
    [TipoEstadoValidacion.RECHAZADA, marker('csp.tipo-estado-validacion.RECHAZADA')]
  ]);

  constructor(
    private readonly logger: NGXLogger,
    protected snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: IProyectoCalendarioFacturacionModalData,
    public matDialogRef: MatDialogRef<ProyectoCalendarioFacturacionModalComponent>,
    private proyectoFacturacionService: ProyectoFacturacionService,
    private readonly translate: TranslateService,
    private tipoFacturacionService: TipoFacturacionService
  ) {
    super(snackBarService, matDialogRef, data);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18n();
    this.initLayout();
    this.loadTiposFacturacion();
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

  protected getDatosForm(): IProyectoCalendarioFacturacionModalData {

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
      tipoFacturacion: this.formGroup.controls.hitoFacturacion.value
    };
    return this.data;
  }

  protected getFormGroup(): FormGroup {

    const data = this.data?.proyectoFacturacion;

    const form = new FormGroup({
      numeroPrevision: new FormControl({ value: data?.numeroPrevision, disabled: true }, [Validators.required]),
      validacionIP: new FormControl({ value: data?.estadoValidacionIP?.estado, disabled: true }, [Validators.required]),
      fechaConformidad: new FormControl(data?.fechaConformidad),
      fechaEmision: new FormControl(data?.fechaEmision, [Validators.required]),
      importeBase: new FormControl(data?.importeBase, [Validators.required]),
      porcentajeIVA: new FormControl(isNaN(data?.porcentajeIVA) ? this.data?.porcentajeIVA : data?.porcentajeIVA, [Validators.required, Validators.pattern('^[0-9]*$'), Validators.min(0), Validators.max(100)]),
      comentario: new FormControl(data?.comentario, [Validators.maxLength(COMENTARIO_MAX_LENGTH)]),
      hitoFacturacion: new FormControl(data?.tipoFacturacion),
      nuevoEstadoValidacionIP: new FormControl(null),
      mensajeMotivoRechazo: new FormControl('')
    });

    if (form.controls.validacionIP.value === TipoEstadoValidacion.VALIDADA) {
      form.controls.fechaConformidad.setValidators(Validators.required);
    }

    if (this.data.action === DialogAction.VALIDAR_IP) {
      form.controls.numeroPrevision.disable({ emitEvent: false });
      form.controls.validacionIP.disable({ emitEvent: false });
      form.controls.fechaConformidad.disable({ emitEvent: false });
      form.controls.fechaEmision.disable({ emitEvent: false });
      form.controls.importeBase.disable({ emitEvent: false });
      form.controls.porcentajeIVA.disable({ emitEvent: false });
      form.controls.comentario.disable({ emitEvent: false });
      form.controls.hitoFacturacion.disable({ emitEvent: false });
      form.controls.nuevoEstadoValidacionIP.setValidators([Validators.required]);
    }

    this.subscriptions.push(
      form.controls.nuevoEstadoValidacionIP.valueChanges
        .subscribe((newEstado: TipoEstadoValidacion) => {
          const isRechazada = newEstado === TipoEstadoValidacion.RECHAZADA;
          this.showMensajeMotivoRechazo$.next(isRechazada);
          form.controls.mensajeMotivoRechazo.setValidators(isRechazada
            ? [Validators.required, Validators.maxLength(COMENTARIO_MAX_LENGTH)] : []);

          form.controls.mensajeMotivoRechazo.updateValueAndValidity();
        })
    );

    return form;
  }

  private initLayout(): void {

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.layout = 'row';
    this.fxLayoutProperties.layoutAlign = 'row';
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.xs = 'column';

    this.fxLayoutProperties2 = new FxLayoutProperties();
    this.fxLayoutProperties2.gap = '20px';
    this.fxLayoutProperties2.layout = 'row';
    this.fxLayoutProperties2.xs = 'column';
  }

  private setupI18n(): void {
    switch(this.data.action){
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


    this.textSaveOrUpdate = this.data?.proyectoFacturacion?.id ? MSG_ACEPTAR : MSG_ANADIR;
  }

  public canShowValidacionIP(): boolean {
    return !!this.data.proyectoFacturacion?.id;
  }

  public canShowFechaConformidad(): boolean {
    return this.data?.action === DialogAction.EDIT && this.data?.proyectoFacturacion?.estadoValidacionIP.estado === TipoEstadoValidacion.VALIDADA;
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
        ).subscribe( (tipos: ITipoFacturacion[]) => this.tiposFacturacion$.next(tipos)));
  }
}
