import { Component } from '@angular/core';
import { FragmentComponent } from '@core/component/fragment.component';
import { SolicitudActionService } from '../../solicitud.action.service';
import { SolicitudAutoevaluacionFragment } from './solicitud-autoevaluacion.fragment';

@Component({
  selector: 'sgi-solicitud-autoevaluacion',
  templateUrl: './solicitud-autoevaluacion.component.html',
  styleUrls: ['./solicitud-autoevaluacion.component.scss']
})
export class SolicitudAutoevaluacionComponent extends FragmentComponent {

  readonly formPart: SolicitudAutoevaluacionFragment;

  constructor(
    actionService: SolicitudActionService
  ) {
    super(actionService.FRAGMENT.AUTOEVALUACION, actionService);
    this.formPart = this.fragment as SolicitudAutoevaluacionFragment;
  }

}
