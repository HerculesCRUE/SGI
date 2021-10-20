import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { IProyectoHito } from '@core/models/csp/proyecto-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { IsEntityValidator } from '@core/validators/is-entity-validador';
import { TipoHitoValidator } from '@core/validators/tipo-hito-validator';
import { TranslateService } from '@ngx-translate/core';
import { SgiRestListResult } from '@sgi/framework/http/types';
import { DateTime } from 'luxon';
import { NGXLogger } from 'ngx-logger';
import { BehaviorSubject } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_ERROR_INIT = marker('error.load');
const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_HITO_FECHA_KEY = marker('csp.proyecto-hito.fecha');
const PROYECTO_HITO_TIPO_KEY = marker('csp.proyecto-hito.tipo');
const PROYECTO_HITO_COMENTARIO_KEY = marker('csp.proyecto-hito.comentario');
const PROYECTO_HITO_KEY = marker('csp.proyecto-hito');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoHitosModalComponentData {
  hitos: IProyectoHito[];
  hito: IProyectoHito;
  idModeloEjecucion: number;
  readonly: boolean;
}
@Component({
  templateUrl: './proyecto-hitos-modal.component.html',
  styleUrls: ['./proyecto-hitos-modal.component.scss']
})
export class ProyectoHitosModalComponent extends
  BaseModalComponent<ProyectoHitosModalComponentData, ProyectoHitosModalComponent> implements OnInit, OnDestroy {

  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  fxLayoutProperties: FxLayoutProperties;

  tiposHitos$: BehaviorSubject<ITipoHito[]> = new BehaviorSubject<ITipoHito[]>([]);

  textSaveOrUpdate: string;

  msgParamFechaEntity = {};
  msgParamTipoEntity = {};
  msgParamComentarioEntity = {};
  title: string;

  constructor(
    private readonly logger: NGXLogger,
    public matDialogRef: MatDialogRef<ProyectoHitosModalComponent>,
    private modeloEjecucionService: ModeloEjecucionService,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoHitosModalComponentData,
    protected snackBarService: SnackBarService,
    private readonly translate: TranslateService) {
    super(snackBarService, matDialogRef, data);

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();

    const suscription = this.formGroup.controls.tipoHito.valueChanges.subscribe((value) => this.createValidatorDate(value));
    this.subscriptions.push(suscription);

    const suscriptionFecha = this.formGroup.controls.fecha.valueChanges.subscribe(() =>
      this.createValidatorDate(this.formGroup.controls.tipoHito.value));
    this.subscriptions.push(suscriptionFecha);

    this.textSaveOrUpdate = this.data?.hito?.tipoHito ? MSG_ACEPTAR : MSG_ANADIR;
    this.loadTiposHito();
    this.subscriptions.push(this.formGroup.get('fecha').valueChanges.subscribe(() => this.validarFecha())
    );
    this.validarFecha();
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_HITO_FECHA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      PROYECTO_HITO_COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.PLURAL });

    this.translate.get(
      PROYECTO_HITO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data?.hito?.tipoHito) {
      this.translate.get(
        PROYECTO_HITO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);
    } else {
      this.translate.get(
        PROYECTO_HITO_KEY,
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
  }


  /**
   * Si la fecha actual es inferior - Checkbox disabled
   * Si la fecha actual es superior - Checkbox enable
   */
  private validarFecha() {
    if (this.formGroup.get('fecha').value <= DateTime.now()) {
      this.formGroup.get('aviso').disable();
      this.formGroup.get('aviso').setValue(false);
    } else {
      this.formGroup.get('aviso').enable();
    }
  }

  /**
   * Validacion de fechas a la hora de seleccionar
   * un tipo de hito en el modal
   * @param tipoHito proyecto tipoHito
   */
  private createValidatorDate(tipoHito: ITipoHito): void {
    let fechas: DateTime[] = [];
    if (tipoHito && typeof tipoHito !== 'string') {
      const proyectoHitos = this.data.hitos.filter(hito =>
        hito.tipoHito.id === (tipoHito as ITipoHito).id &&
        (!hito.fecha.equals(this.data.hito.fecha)));
      fechas = proyectoHitos.map(hito => hito.fecha);
    }
    this.formGroup.setValidators([
      TipoHitoValidator.notInDate('fecha', fechas, this.data?.hitos?.map(hito => hito.tipoHito))
    ]);
  }


  loadTiposHito() {
    this.subscriptions.push(
      this.modeloEjecucionService.findModeloTipoHitoProyecto(this.data.idModeloEjecucion).subscribe(
        (res: SgiRestListResult<IModeloTipoHito>) => this.tiposHitos$.next(res.items.map(modelo => modelo.tipoHito)),
        (error) => {
          this.logger.error(error);
          this.snackBarService.showError(MSG_ERROR_INIT);
        })
    );
  }

  protected getDatosForm(): ProyectoHitosModalComponentData {
    this.data.hito.comentario = this.formGroup.controls.comentario.value;
    this.data.hito.fecha = this.formGroup.controls.fecha.value;
    this.data.hito.tipoHito = this.formGroup.controls.tipoHito.value;
    this.data.hito.generaAviso = this.formGroup.controls.aviso.value ? this.formGroup.controls.aviso.value : false;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoHito: new FormControl(this.data?.hito?.tipoHito, [Validators.required, IsEntityValidator.isValid()]),
      fecha: new FormControl(this.data?.hito?.fecha, [Validators.required]),
      comentario: new FormControl(this.data?.hito?.comentario, [Validators.maxLength(250)]),
      aviso: new FormControl(this.data?.hito?.generaAviso)
    });

    if (this.data.readonly) {
      formGroup.disable();
    }

    return formGroup;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

}
