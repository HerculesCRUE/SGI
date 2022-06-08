import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { IBaremo } from '@core/models/prc/baremo';
import { TranslateService } from '@ngx-translate/core';
import { NodeConfiguracionBaremo } from '../../convocatoria-baremacion-formulario/convocatoria-baremacion-baremos-puntuaciones/convocatoria-baremacion-baremos-puntuaciones.fragment';
import { BaremoComponent } from '../baremo.component';

const BAREMO_PUNTOS_KEY = marker('prc.baremo.puntos');

@Component({
  selector: 'sgi-baremo-puntos',
  templateUrl: './baremo-puntos.component.html',
  styleUrls: ['./baremo-puntos.component.scss']
})
export class BaremoPuntosComponent implements OnInit, BaremoComponent {
  @Input() readonly: boolean;
  @Input() node: NodeConfiguracionBaremo;
  @Output() baremoOutput: EventEmitter<IBaremo> = new EventEmitter<IBaremo>();
  @Output() cancel: EventEmitter<void> = new EventEmitter();

  formGroup: FormGroup;
  msgParamPuntosEntity = {};

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
      puntos: new FormControl(this.node?.baremo?.puntos ?? '', [Validators.required]),
    });
    if (this.readonly) {
      this.formGroup.disable();
    }
  }

  private setupI18N(): void {
    this.translate.get(
      BAREMO_PUNTOS_KEY,
      MSG_PARAMS.CARDINALIRY.SINGULAR
    ).subscribe((value) => this.msgParamPuntosEntity = {
      entity: value, ...MSG_PARAMS.GENDER.MALE, ...MSG_PARAMS.CARDINALIRY.SINGULAR
    });
  }

  onAceptar(): void {
    this.formGroup.markAllAsTouched();
    if (!this.formGroup.valid) {
      return;
    }
    const baremo = {} as IBaremo;
    baremo.puntos = this.formGroup.controls.puntos.value;
    baremo.configuracionBaremo = this.node.configuracionBaremo;

    this.baremoOutput.next(baremo);
  }

  onCancelar(): void {
    this.cancel.next();
  }
}
