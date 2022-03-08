import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { IEstadoProduccionCientifica, TIPO_ESTADO_PRODUCCION_MAP } from '@core/models/prc/estado-produccion-cientifica';

@Component({
  selector: 'sgi-cvn-estado',
  templateUrl: './cvn-estado.component.html',
  styleUrls: ['./cvn-estado.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CvnEstadoComponent implements OnInit {
  readonly TIPO_ESTADO_PRODUCCION_MAP = TIPO_ESTADO_PRODUCCION_MAP;

  @Input() readonly estado: IEstadoProduccionCientifica;

  constructor() { }

  ngOnInit(): void {
  }

}
