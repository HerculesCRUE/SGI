import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { MSG_PARAMS } from '@core/i18n';
import { IDictamen } from '@core/models/eti/dictamen';
import { IEvaluacion } from '@core/models/eti/evaluacion';
import { IMemoria } from '@core/models/eti/memoria';
import { FxFlexProperties } from '@core/models/shared/flexLayout/fx-flex-properties';
import { FxLayoutProperties } from '@core/models/shared/flexLayout/fx-layout-properties';
import { TipoEvaluacionService } from '@core/services/eti/tipo-evaluacion.service';
import { Observable, of, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SeguimientoFormularioActionService } from '../seguimiento-formulario.action.service';
import { SeguimientoListadoAnteriorMemoriaComponent } from '../seguimiento-listado-anterior-memoria/seguimiento-listado-anterior-memoria.component';
import { SeguimientoEvaluacionFragment } from './seguimiento-evaluacion.fragment';

@Component({
  selector: 'sgi-seguimiento-evaluacion',
  templateUrl: './seguimiento-evaluacion.component.html',
  styleUrls: ['./seguimiento-evaluacion.component.scss']
})
export class SeguimientoEvaluacionComponent extends FormFragmentComponent<IMemoria> implements AfterViewInit, OnInit, OnDestroy {

  fxFlexProperties: FxFlexProperties;
  fxLayoutProperties: FxLayoutProperties;
  fxFlexPropertiesInline: FxFlexProperties;

  @ViewChild('evaluaciones') evaluaciones: SeguimientoListadoAnteriorMemoriaComponent;

  dictamenes$: Observable<IDictamen[]>;
  suscriptions: Subscription[] = [];

  formPart: SeguimientoEvaluacionFragment;

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(
    public actionService: SeguimientoFormularioActionService,
    private tipoEvaluacionService: TipoEvaluacionService
  ) {
    super(actionService.FRAGMENT.EVALUACIONES, actionService);
    this.fxFlexProperties = new FxFlexProperties();
    this.fxFlexProperties.sm = '0 1 calc(50%-10px)';
    this.fxFlexProperties.md = '0 1 calc(33%-10px)';
    this.fxFlexProperties.gtMd = '0 1 calc(32%-10px)';
    this.fxFlexProperties.order = '2';

    this.fxFlexPropertiesInline = new FxFlexProperties();
    this.fxFlexPropertiesInline.sm = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.md = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.gtMd = '0 1 calc(100%-10px)';
    this.fxFlexPropertiesInline.order = '3';

    this.fxLayoutProperties = new FxLayoutProperties();
    this.fxLayoutProperties.gap = '20px';
    this.fxLayoutProperties.layout = 'row wrap';
    this.fxLayoutProperties.xs = 'column';

    this.formPart = this.fragment as SeguimientoEvaluacionFragment;

    this.dictamenes$ = this.formPart.evaluacion$.pipe(
      switchMap(evaluacion => {
        if (evaluacion) {
          return tipoEvaluacionService.findAllDictamenByTipoEvaluacionAndRevisionMinima(
            evaluacion.tipoEvaluacion.id,
            evaluacion.esRevMinima
          ).pipe(
            map(response => response.items)
          );
        }
        return of([]);
      })
    );

  }

  ngOnInit() {
    super.ngOnInit();
    this.suscriptions.push(this.formGroup.controls.dictamen.valueChanges.subscribe((dictamen) => {
      this.actionService.setDictamen(dictamen);
    }));
  }

  ngAfterViewInit(): void {
    this.evaluaciones.memoriaId = this.actionService.getEvaluacion()?.memoria?.id;
    this.evaluaciones.evaluacionId = this.actionService.getEvaluacion()?.id;
    this.evaluaciones.ngAfterViewInit();
  }

  ngOnDestroy(): void {
    this.suscriptions.forEach((suscription) => suscription.unsubscribe());
  }

}
