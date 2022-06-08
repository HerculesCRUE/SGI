import { Component, OnInit, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IBaremo, BAREMO_PESO_MAX, BAREMO_PESO_MIN } from '@core/models/prc/baremo';
import { TranslateService } from '@ngx-translate/core';
import { NodeConfiguracionBaremo } from '../../convocatoria-baremacion-formulario/convocatoria-baremacion-baremos-puntuaciones/convocatoria-baremacion-baremos-puntuaciones.fragment';
import { BaremoComponent } from '../baremo.component';

const BAREMO_PESO_KEY = marker('prc.baremo.peso');

@Component({
  selector: 'sgi-baremo-peso',
  templateUrl: './baremo-peso.component.html',
  styleUrls: ['./baremo-peso.component.scss']
})
export class BaremoPesoComponent implements OnInit, BaremoComponent {
  @Input() readonly: boolean;
  @Input() node: NodeConfiguracionBaremo;
  @Output() baremoOutput: EventEmitter<IBaremo> = new EventEmitter<IBaremo>();
  @Output() cancel: EventEmitter<void> = new EventEmitter();

  formGroup: FormGroup;
  msgParamPesoEntity = {};

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

  constructor(private readonly translate: TranslateService) { }

  ngOnInit(): void {
    this.initFormGroup();
    this.setupI18N();
  }

  private initFormGroup(): void {
    this.formGroup = new FormGroup({
      nombre: new FormControl({ value: this.node?.configuracionBaremo?.nombre ?? '', disabled: true }),
      peso: new FormControl(this.node?.baremo?.peso ?? '',
        [Validators.required, Validators.max(BAREMO_PESO_MAX), Validators.min(BAREMO_PESO_MIN)]
      ),
    });
    if (this.readonly) {
      this.formGroup.disable();
    }
  }

  private setupI18N(): void {
    this.translate.get(
      BAREMO_PESO_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPesoEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
  }

  onAceptar(): void {
    this.formGroup.markAllAsTouched();
    if (!this.formGroup.valid) {
      return;
    }
    const baremo = {} as IBaremo;
    baremo.peso = this.formGroup.controls.peso.value;
    baremo.configuracionBaremo = this.node.configuracionBaremo;

    this.baremoOutput.next(baremo);
  }

  onCancelar(): void {
    this.cancel.next();
  }
}
