import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { BAREMO_PESO_MAX, BAREMO_PESO_MIN, IBaremo, TipoCuantia, TIPO_CUANTIA_MAP } from '@core/models/prc/baremo';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs';
import { NodeConfiguracionBaremo } from '../../convocatoria-baremacion-formulario/convocatoria-baremacion-baremos-puntuaciones/convocatoria-baremacion-baremos-puntuaciones.fragment';
import { BaremoComponent } from '../baremo.component';

const BAREMO_PESO_KEY = marker('prc.baremo.peso');
const BAREMO_TIPO_CUANTIA_KEY = marker('prc.baremo.tipo-cuantia');
const BAREMO_CUANTIA_KEY = marker('prc.baremo.cuantia');

@Component({
  selector: 'sgi-baremo-peso-cuantia',
  templateUrl: './baremo-peso-cuantia.component.html',
  styleUrls: ['./baremo-peso-cuantia.component.scss']
})
export class BaremoPesoCuantiaComponent implements OnInit, OnDestroy, BaremoComponent {
  @Input() readonly: boolean;
  @Input() node: NodeConfiguracionBaremo;
  @Output() baremoOutput: EventEmitter<IBaremo> = new EventEmitter<IBaremo>();
  @Output() cancel: EventEmitter<void> = new EventEmitter();

  private readonly PESO_MAXIMO = 100;
  private readonly PESO_MINIMO = 1;

  private subscriptions: Subscription[] = [];
  formGroup: FormGroup;
  msgParamPesoEntity = {};
  msgParamTipoCuantiaEntity = {};
  msgParamCuantiaEntity = {};

  get TIPO_CUANTIA_MAP() {
    return TIPO_CUANTIA_MAP;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(private readonly translate: TranslateService) { }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private initFormGroup(): void {
    const tipoCuantia = this.node?.baremo?.tipoCuantia;
    this.formGroup = new FormGroup({
      nombre: new FormControl({ value: this.node?.configuracionBaremo?.nombre ?? '', disabled: true }),
      peso: new FormControl(this.node?.baremo?.peso ?? '',
        [Validators.required, Validators.max(BAREMO_PESO_MAX), Validators.min(BAREMO_PESO_MIN)]
      ),
      tipoCuantia: new FormControl(tipoCuantia ?? null, [Validators.required]),
      cuantia: new FormControl({
        value: this.node?.baremo?.cuantia ?? '',
        disabled: !tipoCuantia || tipoCuantia === TipoCuantia.RANGO
      }),
    });
    if (this.readonly) {
      this.formGroup.disable();
    }

    this.formGroup.controls.tipoCuantia.valueChanges.subscribe(tipoCuantiaSelected => {
      if (tipoCuantiaSelected === TipoCuantia.PUNTOS) {
        this.formGroup.controls.cuantia.enable();
      } else {
        this.formGroup.controls.cuantia.disable();
      }
    });
  }

  private setupI18N(): void {
    this.translate.get(
      BAREMO_PESO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPesoEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      BAREMO_TIPO_CUANTIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamTipoCuantiaEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });

    this.translate.get(
      BAREMO_CUANTIA_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamCuantiaEntity = {
      entity: value, ...MSG_PARAMS.GENDER.FEMALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
  }

  showCuantiaControl(): boolean {
    return this.formGroup.controls.tipoCuantia.value === TipoCuantia.PUNTOS;
  }

  onAceptar(): void {
    this.formGroup.markAllAsTouched();
    if (!this.formGroup.valid) {
      return;
    }
    const baremo = {} as IBaremo;
    baremo.peso = this.formGroup.controls.peso.value;
    baremo.tipoCuantia = this.formGroup.controls.tipoCuantia.value;
    if (baremo.tipoCuantia === TipoCuantia.PUNTOS) {
      baremo.cuantia = this.formGroup.controls.cuantia.value;
    } else {
      baremo.cuantia = null;
    }
    baremo.configuracionBaremo = this.node.configuracionBaremo;

    this.baremoOutput.next(baremo);
  }

  onCancelar(): void {
    this.cancel.next();
  }
}
