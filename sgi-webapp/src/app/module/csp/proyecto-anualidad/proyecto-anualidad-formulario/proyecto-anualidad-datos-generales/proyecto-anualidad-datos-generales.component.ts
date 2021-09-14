import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoAnualidad } from '@core/models/csp/proyecto-anualidad';
import { IProyectoAnualidadResumen } from '@core/models/csp/proyecto-anualidad-resumen';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { ProyectoAnualidadActionService } from '../../proyecto-anualidad.action.service';
import { ProyectoAnualidadDatosGeneralesFragment } from './proyecto-anualidad-datos-generales.fragment';

const PROYECTO_ANUALIDAD_KEY = marker('csp.proyecto-presupuesto.anualidad');

@Component({
  selector: 'sgi-proyecto-anualidad-datos-generales',
  templateUrl: './proyecto-anualidad-datos-generales.component.html',
  styleUrls: ['./proyecto-anualidad-datos-generales.component.scss']
})
export class ProyectoAnualidadDatosGeneralesComponent extends FormFragmentComponent<IProyectoAnualidad>
  implements OnInit, OnDestroy {

  formPart: ProyectoAnualidadDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  private subscriptions: Subscription[] = [];

  msgParamAnualidadEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(private readonly logger: NGXLogger,
    protected actionService: ProyectoAnualidadActionService,
    private snackBarService: SnackBarService,
    private readonly translate: TranslateService) {

    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ProyectoAnualidadDatosGeneralesFragment;
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(36%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

  }

  /**
   * Comprueba que la anulidad introducida no está duplicada
   *
   * @param anualidades Listado de anualidades.
   */
  static isDuplicated(anualidades: IProyectoAnualidadResumen[]): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const anualidadesFilter = anualidades.filter(
        element => element.anio === control.value);
      if (anualidadesFilter.length > 0) {
        return { duplicate: true } as ValidationErrors;
      }
      return null;
    };
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.setupI18N();

    if (!this.formPart.isAnualidadGenerica && !this.formPart.isEdit()) {
      this.formGroup.controls.anualidad.setValidators(ProyectoAnualidadDatosGeneralesComponent.isDuplicated(
        this.actionService.anualidades));
    }
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_ANUALIDAD_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamAnualidadEntity = { entity: value, ...MSG_PARAMS.GENDER.FEMALE });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
