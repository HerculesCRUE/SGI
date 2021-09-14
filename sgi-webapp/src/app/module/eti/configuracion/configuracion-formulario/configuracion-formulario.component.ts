import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { HttpProblem } from '@core/errors/http-problem';
import { MSG_PARAMS } from '@core/i18n';
import { IConfiguracion } from '@core/models/eti/configuracion';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { ConfiguracionService } from '@core/services/eti/configuracion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_SUCCESS = marker('msg.update.entity.success');
const MSG_ERROR = marker('error.update.entity');
const CONFIGURACION_KEY = marker('eti.configuracion')

@Component({
  selector: 'sgi-configuracion-formulario',
  templateUrl: './configuracion-formulario.component.html',
  styleUrls: ['./configuracion-formulario.component.scss']
})
export class ConfiguracionFormularioComponent implements OnInit, OnDestroy {
  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;

  formGroup: FormGroup;

  private suscripciones: Subscription[] = [];
  private configuracion: IConfiguracion;

  private initialFormValue: IConfiguracion;

  updateEnable = false;

  textoUpdateSuccess: string;
  textoUpdateError: string;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    private readonly logger: NGXLogger,
    private service: ConfiguracionService,
    private snackBarService: SnackBarService,
    private readonly translate: TranslateService
  ) {

    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(100%-50px)';
    this.fxFlexProperties.md = '0 1 calc(50%-50px)';
    this.fxFlexProperties.gtMd = '0 1 calc(50%-50px)';
    this.fxFlexProperties.order = '1';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '50px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';
  }

  ngOnInit() {
    this.initFormGroup();
    this.setupI18N();
    this.loadConfiguracion();
  }

  private setupI18N(): void {

    this.translate.get(
      CONFIGURACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_SUCCESS,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateSuccess = value);

    this.translate.get(
      CONFIGURACION_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).pipe(
      switchMap((value) => {
        return this.translate.get(
          MSG_ERROR,
          { entity: value, ...MSG_PARAMS.GENDER.MALE }
        );
      })
    ).subscribe((value) => this.textoUpdateError = value);
  }

  /**
   * Inicializa el formGroup
   */
  private initFormGroup() {
    this.formGroup = new FormGroup({
      mesesArchivadaInactivo: new FormControl('', [Validators.required]),
      diasArchivadaPendienteCorrecciones: new FormControl('', [Validators.required]),
      diasLimiteEvaluador: new FormControl('', [Validators.required]),
      mesesAvisoProyectoCEEA: new FormControl('', [Validators.required]),
      mesesAvisoProyectoCEISH: new FormControl('', [Validators.required]),
      mesesAvisoProyectoCEIAB: new FormControl('', [Validators.required])
    });
    const formChangesSubscription = this.formGroup.statusChanges.subscribe(status => this.hasChanges(status));
    this.suscripciones.push(formChangesSubscription);
  }

  private setForm(configuracion: IConfiguracion) {
    this.configuracion = configuracion;
    this.formGroup.controls.mesesArchivadaInactivo.setValue(configuracion.mesesArchivadaInactivo);
    this.formGroup.controls.diasArchivadaPendienteCorrecciones.setValue(configuracion.diasArchivadaPendienteCorrecciones);
    this.formGroup.controls.diasLimiteEvaluador.setValue(configuracion.diasLimiteEvaluador);
    this.formGroup.controls.mesesAvisoProyectoCEEA.setValue(configuracion.mesesAvisoProyectoCEEA);
    this.formGroup.controls.mesesAvisoProyectoCEISH.setValue(configuracion.mesesAvisoProyectoCEISH);
    this.formGroup.controls.mesesAvisoProyectoCEIAB.setValue(configuracion.mesesAvisoProyectoCEIAB);
    this.initialFormValue = Object.assign({}, this.formGroup.value);
  }

  private getForm(): IConfiguracion {
    this.configuracion.mesesArchivadaInactivo = this.formGroup.value.mesesArchivadaInactivo;
    this.configuracion.diasArchivadaPendienteCorrecciones = this.formGroup.value.diasArchivadaPendienteCorrecciones;
    this.configuracion.diasLimiteEvaluador = this.formGroup.value.diasLimiteEvaluador;
    this.configuracion.mesesAvisoProyectoCEEA = this.formGroup.value.mesesAvisoProyectoCEEA;
    this.configuracion.mesesAvisoProyectoCEISH = this.formGroup.value.mesesAvisoProyectoCEISH;
    this.configuracion.mesesAvisoProyectoCEIAB = this.formGroup.value.mesesAvisoProyectoCEIAB;
    return this.configuracion;
  }

  private loadConfiguracion() {
    const configuracionFindSubscription = this.service.getConfiguracion().subscribe(
      configuracion => {
        this.setForm(configuracion);
      });
    this.suscripciones.push(configuracionFindSubscription);
  }

  update() {
    const configuracionUpdateSubscription = this.service.update(this.configuracion.id, this.getForm()).subscribe(
      (value) => {
        this.snackBarService.showSuccess(this.textoUpdateSuccess);
        this.configuracion = value;
        this.updateEnable = false;
      },
      (error) => {
        this.logger.error(error);
        if (error instanceof HttpProblem) {
          this.snackBarService.showError(error);
        }
        else {
          this.snackBarService.showError(this.textoUpdateError);
        }
      }
    );
    this.suscripciones.push(configuracionUpdateSubscription);
  }

  cancel() {
    this.setForm(this.configuracion);
    this.updateEnable = false;
  }

  private hasChanges(status: string) {
    if (status === 'VALID' && this.initialFormValue) {
      this.updateEnable = (!this.isEquals(this.initialFormValue, this.formGroup.value));
    }
  }

  private isEquals(initFormValue: IConfiguracion, formValue: IConfiguracion): boolean {
    if (initFormValue && formValue) {
      if (initFormValue.mesesArchivadaInactivo === formValue.mesesArchivadaInactivo
        && initFormValue.mesesAvisoProyectoCEEA === formValue.mesesAvisoProyectoCEEA
        && initFormValue.mesesAvisoProyectoCEISH === formValue.mesesAvisoProyectoCEISH
        && initFormValue.mesesAvisoProyectoCEIAB === formValue.mesesAvisoProyectoCEIAB
        && initFormValue.diasArchivadaPendienteCorrecciones === formValue.diasArchivadaPendienteCorrecciones
        && initFormValue.diasLimiteEvaluador === formValue.diasLimiteEvaluador) {
        return true;
      }
    }
    return false;
  }

  ngOnDestroy(): void {
    this.suscripciones?.forEach(x => x.unsubscribe());
  }

}
