import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IConvocatoriaFase } from '@core/models/csp/convocatoria-fase';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateValidator } from '@core/validators/date-validator';
import { IRange, RangeValidator } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { switchMap, tap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const CONVOCATORIA_FASE_KEY = marker('csp.convocatoria-fase');
const CONVOCATORIA_FASES_FECHA_FIN_KEY = marker('csp.convocatoria-fase.fecha-fin');
const CONVOCATORIA_FASES_FECHA_INICIO_KEY = marker('csp.convocatoria-fase.fecha-inicio');
const CONVOCATORIA_FASES_OBSERVACIONES_KEY = marker('csp.convocatoria-fase.observaciones');
const CONVOCATORIA_FASES_TIPO_KEY = marker('csp.convocatoria-fase.tipo');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ConvocatoriaPlazosFaseModalComponentData {
  plazos: IConvocatoriaFase[];
  plazo: IConvocatoriaFase;
  idModeloEjecucion: number;
  readonly: boolean;
  canEdit: boolean;
}

@Component({
  templateUrl: './convocatoria-plazos-fase-modal.component.html',
  styleUrls: ['./convocatoria-plazos-fase-modal.component.scss']
})
export class ConvocatoriaPlazosFaseModalComponent extends
  BaseModalComponent<ConvocatoriaPlazosFaseModalComponentData, ConvocatoriaPlazosFaseModalComponent> implements OnInit, OnDestroy {
  fxLayoutProperties: FxLayoutProperties;
  fxLayoutProperties2: FxLayoutProperties;

  textSaveOrUpdate: string;
  title: string;

  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  msgParamTipoEntity = {};
  msgParamObservacionesEntity = {};

  constructor(
    protected snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: ConvocatoriaPlazosFaseModalComponentData,
    public matDialogRef: MatDialogRef<ConvocatoriaPlazosFaseModalComponent>,
    private readonly translate: TranslateService
  ) {
    super(snackBarService, matDialogRef, data);

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

  ngOnInit(): void {
    super.ngOnInit();

    this.createValidatorDate(this.data?.plazo?.tipoFase);
    const suscription = this.formGroup.controls.tipoFase.valueChanges.pipe(tap((value) => this.createValidatorDate(value))).subscribe();
    this.subscriptions.push(suscription);

    this.setupI18N();
    this.textSaveOrUpdate = this.data.plazo.fechaInicio ? MSG_ACEPTAR : MSG_ANADIR;
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
      DateValidator.isAfter('fechaInicio', 'fechaFin'),
      DateValidator.isBefore('fechaFin', 'fechaInicio'),
      RangeValidator.notOverlaps('fechaInicio', 'fechaFin', rangoFechas)
    ]);
  }

  protected getDatosForm(): ConvocatoriaPlazosFaseModalComponentData {
    this.data.plazo.fechaInicio = this.formGroup.controls.fechaInicio.value;
    this.data.plazo.fechaFin = this.formGroup.controls.fechaFin.value;
    this.data.plazo.tipoFase = this.formGroup.controls.tipoFase.value;
    this.data.plazo.observaciones = this.formGroup.controls.observaciones.value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      fechaInicio: new FormControl(this.data?.plazo?.fechaInicio, Validators.required),
      fechaFin: new FormControl(this.data?.plazo?.fechaFin, Validators.required),
      tipoFase: new FormControl(this.data?.plazo?.tipoFase, Validators.required),
      observaciones: new FormControl(this.data?.plazo?.observaciones, Validators.maxLength(250))
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
