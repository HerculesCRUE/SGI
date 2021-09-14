import { Component, OnDestroy, OnInit } from '@angular/core';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IProyectoAgrupacionGasto } from '@core/models/csp/proyecto-agrupacion-gasto';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { SnackBarService } from '@core/services/snack-bar.service';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { element } from 'protractor';
import { merge, Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';
import { PROYECTO_AGRUPACION_GASTO_DATA_KEY } from '../../proyecto-agrupacion-gasto-data.resolver';
import { ProyectoAgrupacionGastoActionService } from '../../proyecto-agrupacion-gasto.action.service';
import { ProyectoAgrupacionGastoDatosGeneralesFragment } from './proyecto-agrupacion-gasto-datos-generales.fragment';

const PROYECTO_AGRUPACION_GASTO_KEY = marker('csp.proyecto-agrupacion-gasto');
const PROYECTO_AGRUPACION_GASTO_NOMBRE_KEY = marker('csp.proyecto-agrupacion-gasto.nombre');

@Component({
  selector: 'sgi-proyecto-agrupacion-gasto-datos-generales',
  templateUrl: './proyecto-agrupacion-gasto-datos-generales.component.html',
  styleUrls: ['./proyecto-agrupacion-gasto-datos-generales.component.scss']
})
export class ProyectoAgrupacionGastoDatosGeneralesComponent extends FormFragmentComponent<IProyectoAgrupacionGasto>
  implements OnInit, OnDestroy {
  formPart: ProyectoAgrupacionGastoDatosGeneralesFragment;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexProperties: FxFlexProperties;
  private subscriptions: Subscription[] = [];
  msgParamEntity = {};
  msgParamNombreEntity = {};


  constructor(
    protected actionService: ProyectoAgrupacionGastoActionService,
    private readonly translate: TranslateService
  ) {
    super(actionService.FRAGMENT.DATOS_GENERALES, actionService);
    this.formPart = this.fragment as ProyectoAgrupacionGastoDatosGeneralesFragment;

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

  ngOnInit(): void {
    super.ngOnInit();
    this.setupI18N();
    this.subscriptions.push(
      this.formGroup.get('nombre').valueChanges
        .pipe(
          tap(() => this.checkNombre())
        ).subscribe()
    );
  }

  private setupI18N(): void {
    this.translate.get(
      PROYECTO_AGRUPACION_GASTO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.FEMALE });

    this.translate.get(
      PROYECTO_AGRUPACION_GASTO_NOMBRE_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamNombreEntity = { entity: value, ...MSG_PARAMS.CARDINALIRY.SINGULAR, ...MSG_PARAMS.GENDER.MALE });
  }


  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private checkNombre(): void {
    const nombreForm = this.formGroup.get('nombre');
    const agrupaciones = this.actionService.agrupaciones.filter(
      element => element.nombre === nombreForm.value);
    if (agrupaciones.length !== 0 && (nombreForm.value !== this.formPart.nombre)) {
      nombreForm.setErrors({ notUnique: true });
    }
  }


}
