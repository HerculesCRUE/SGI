import { Component, Inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaHito } from '@core/models/csp/convocatoria-hito';
import { ITipoHito } from '@core/models/csp/tipos-configuracion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TipoHitoValidator } from '@core/validators/tipo-hito-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_HITO_KEY = marker('csp.hito');
const CONVOCATORIA_HITO_COMENTARIO_KEY = marker('csp.hito.comentario');
const CONVOCATORIA_HITO_FECHA_INICIO_KEY = marker('csp.hito.fecha');
const CONVOCATORIA_HITO_TIPO_KEY = marker('csp.hito.tipo');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ConvocatoriaHitosModalComponentData {
  hitos: IConvocatoriaHito[];
  hito: IConvocatoriaHito;
  idModeloEjecucion: number;
  readonly: boolean;
  canEdit: boolean;
}

@Component({
  templateUrl: './convocatoria-hitos-modal.component.html',
  styleUrls: ['./convocatoria-hitos-modal.component.scss']
})

export class ConvocatoriaHitosModalComponent extends
  BaseModalComponent<ConvocatoriaHitosModalComponentData, ConvocatoriaHitosModalComponent> implements OnInit, OnDestroy {
  @ViewChild(MatAutocompleteTrigger) autocomplete: MatAutocompleteTrigger;

  fxLayoutProperties: FxLayoutProperties;

  tipoHitos$: Observable<ITipoHito[]>;

  textSaveOrUpdate: string;
  title: string;

  msgParamComentarioEntity = {};
  msgParamFechaInicioEntity = {};
  msgParamTipoEntity = {};

  constructor(
    public matDialogRef: MatDialogRef<ConvocatoriaHitosModalComponent>,
    modeloEjecucionService: ModeloEjecucionService,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaHitosModalComponentData,
    protected snackBarService: SnackBarService,
    private readonly translate: TranslateService) {
    super(snackBarService, matDialogRef, data);
    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.tipoHitos$ = modeloEjecucionService.findModeloTipoHitoConvocatoria(this.data.idModeloEjecucion).pipe(
      map(response => response.items.map(modeloTipoHito => modeloTipoHito.tipoHito))
    );
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.createValidatorDate(this.data?.hito?.tipoHito);

    const suscription = this.formGroup.controls.tipoHito.valueChanges.subscribe((value) => this.createValidatorDate(value));
    this.subscriptions.push(suscription);

    const suscriptionFecha = this.formGroup.controls.fechaInicio.valueChanges.subscribe(() =>
      this.createValidatorDate(this.formGroup.controls.tipoHito.value));
    this.subscriptions.push(suscriptionFecha);

    this.textSaveOrUpdate = this.data?.hito?.tipoHito ? MSG_ACEPTAR : MSG_ANADIR;

    this.validarFecha(this.formGroup.get('fechaInicio').value);

    this.subscriptions.push(this.formGroup.get('fechaInicio').valueChanges.subscribe(
      (value) => this.validarFecha(value)));
  }

  private setupI18N(): void {
    this.translate.get(
      CONVOCATORIA_HITO_TIPO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_HITO_FECHA_INICIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamFechaInicioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    this.translate.get(
      CONVOCATORIA_HITO_COMENTARIO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamComentarioEntity = { entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR });

    if (this.data.hito?.tipoHito) {
      this.translate.get(
        CONVOCATORIA_HITO_KEY,
        MSG_PARAMS.CARDINALIRY.SINGULAR
      ).subscribe((value) => this.title = value);

    } else {
      this.translate.get(
        CONVOCATORIA_HITO_KEY,
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
   * Validacion de fechas a la hora de seleccionar
   * un tipo de hito en el modal
   * @param tipoHito convocatoria tipoHito
   */
  private createValidatorDate(tipoHito: ITipoHito): void {
    let fechas: DateTime[] = [];
    if (tipoHito) {
      const convocatoriasHitos = this.data.hitos.filter(hito =>
        hito.tipoHito.id === tipoHito.id &&
        this.data.hito.fecha &&
        !hito.fecha.equals(this.data.hito.fecha));
      fechas = convocatoriasHitos.map(hito => hito.fecha);
    }
    this.formGroup.setValidators([
      TipoHitoValidator.notInDate('fechaInicio', fechas, this.data?.hitos?.map(hito => hito.tipoHito))
    ]);
  }

  /**
   * Si la fecha actual es inferior - Checkbox disabled
   * Si la fecha actual es superior - Checkbox enable
   */
  private validarFecha(date: DateTime) {
    if (date <= DateTime.now()) {
      this.formGroup.get('aviso').disable();
      this.formGroup.get('aviso').setValue(false);
    } else {
      this.formGroup.get('aviso').enable();
    }
  }

  protected getDatosForm(): ConvocatoriaHitosModalComponentData {
    this.data.hito.comentario = this.formGroup.controls.comentario.value;
    this.data.hito.fecha = this.formGroup.controls.fechaInicio.value;
    this.data.hito.tipoHito = this.formGroup.controls.tipoHito.value;
    this.data.hito.generaAviso = this.formGroup.controls.aviso.value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      tipoHito: new FormControl(this.data?.hito?.tipoHito, Validators.required),
      fechaInicio: new FormControl(this.data?.hito?.fecha, Validators.required),
      comentario: new FormControl(this.data?.hito?.comentario, Validators.maxLength(250)),
      aviso: new FormControl(this.data?.hito?.generaAviso ?? false)
    });
    if (!this.data.canEdit) {
      formGroup.disable();
    }
    return formGroup;
  }

  ngOnDestroy(): void {
    super.ngOnDestroy();
  }

}
