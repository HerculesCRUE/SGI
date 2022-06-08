import { EventEmitter } from '@angular/core';
import { IBaremo } from '@core/models/prc/baremo';
import { NodeConfiguracionBaremo } from '../convocatoria-baremacion-formulario/convocatoria-baremacion-baremos-puntuaciones/convocatoria-baremacion-baremos-puntuaciones.fragment';

export interface BaremoComponent {
  readonly: boolean;
  node: NodeConfiguracionBaremo;
  baremoOutput: EventEmitter<IBaremo>;
  cancel: EventEmitter<void>;
}
