import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { BaseModalComponent } from '@core/component/base-modal.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoPlazos } from '@core/models/csp/proyecto-plazo';
import { ITipoFase } from '@core/models/csp/tipos-configuracion';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { DateValidator } from '@core/validators/date-validator';
import { NullIdValidador } from '@core/validators/null-id-validador';
import { IRange, RangeValidator } from '@core/validators/range-validator';
import { TranslateService } from '@ngx-translate/core';
import { DateTime } from 'luxon';
import { switchMap } from 'rxjs/operators';

const MSG_ANADIR = marker('btn.add');
const MSG_ACEPTAR = marker('btn.ok');
const PROYECTO_PLAZO_FECHA_INICIO_KEY = marker('csp.proyecto.plazo.fecha-inicio');
const PROYECTO_PLAZO_FECHA_FIN_KEY = marker('csp.proyecto.plazo.fecha-fin');
const PROYECTO_PLAZO_TIPO_FASE_KEY = marker('csp.proyecto.plazo.tipo-fase');
const PROYECTO_PLAZO_OBSERVACIONES_KEY = marker('csp.proyecto.plazo.observaciones');
const PROYECTO_FASE_KEY = marker('csp.proyecto-fase');
const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface ProyectoPlazosModalComponentData {
  plazos: IProyectoPlazos[];
  plazo: IProyectoPlazos;
  idModeloEjecucion: number;
  readonly: boolean;
}

@Component({
  selector: 'sgi-proyecto-plazos-modal',
  templateUrl: './proyecto-plazos-modal.component.html',
  styleUrls: ['./proyecto-plazos-modal.component.scss']
})
export class ProyectoPlazosModalComponent extends
  BaseModalComponent<ProyectoPlazosModalComponentData, ProyectoPlazosModalComponent> implements OnInit, OnDestroy {

  fxLayoutProperties: FxLayoutProperties;
  fxLayoutProperties2: FxLayoutProperties;

  textSaveOrUpdate: string;

  msgParamFechaInicioEntity = {};
  msgParamFechaFinEntity = {};
  msgParamTipoFaseEntity = {};
  msgParamObservacionesEntity = {};
  title: string;

  constructor(
    protected snackBarService: SnackBarService,
    @Inject(MAT_DIALOG_DATA) public data: ProyectoPlazosModalComponentData,
    public matDialogRef: MatDialogRef<ProyectoPlazosModalComponent>,
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
    this.setupI18N();

    this.createValidatorDate(this.data?.plazo?.tipoFase);
    this.subscriptions.push(this.formGroup.controls.tipoFase.valueChanges.subscribe((value) => this.createValidatorDate(value)));
    this.subscriptions.push(this.formGroup.controls.fechaFin.valueChanges.subscribe((value) => this.validatorGeneraAviso(value)));

    this.validatorGeneraAviso(this.formGroup.controls.fechaFin.value);
    this.textSaveOrUpdate = this.data.plazo?.tipoFase ? MSG_ACEPTAR : MSG_ANADIR;
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
  }


  /**
   * Validamos fecha para activar o inactivar el checkbox generaAviso
   */
  private validatorGeneraAviso(fechaFinInput: DateTime) {
    const fechaActual = DateTime.now();
    const fechaFin = fechaFinInput;
    if (fechaFin <= fechaActual) {
      this.formGroup.get('generaAviso').disable();
      this.formGroup.get('generaAviso').setValue(false);
    } else {
      this.formGroup.get('generaAviso').enable();
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

  protected getDatosForm(): ProyectoPlazosModalComponentData {
    this.data.plazo.fechaInicio = this.formGroup.controls.fechaInicio.value;
    this.data.plazo.fechaFin = this.formGroup.controls.fechaFin.value;
    this.data.plazo.tipoFase = this.formGroup.controls.tipoFase.value;
    this.data.plazo.observaciones = this.formGroup.controls.observaciones.value;
    this.data.plazo.generaAviso = this.formGroup.controls.generaAviso.value;
    return this.data;
  }

  protected getFormGroup(): FormGroup {
    const formGroup = new FormGroup({
      fechaInicio: new FormControl(this.data?.plazo?.fechaInicio, [Validators.required]),
      fechaFin: new FormControl(this.data?.plazo?.fechaFin, Validators.required),
      tipoFase: new FormControl(this.data?.plazo?.tipoFase, [Validators.required, new NullIdValidador().isValid()]),
      observaciones: new FormControl(this.data?.plazo?.observaciones, [Validators.maxLength(250)]),
      generaAviso: new FormControl(this.data?.plazo?.generaAviso)
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
