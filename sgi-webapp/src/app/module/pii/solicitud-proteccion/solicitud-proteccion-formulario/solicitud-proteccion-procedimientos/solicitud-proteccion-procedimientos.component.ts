import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormFragmentComponent } from '@core/component/fragment.component';
import { IProcedimiento } from '@core/models/pii/procedimiento';
import { SolicitudProteccionActionService } from '../../solicitud-proteccion.action.service';

@Component({
  selector: 'sgi-solicitud-proteccion-procedimientos',
  templateUrl: './solicitud-proteccion-procedimientos.component.html',
  styleUrls: ['./solicitud-proteccion-procedimientos.component.scss']
})
export class SolicitudProteccionProcedimientosComponent extends FormFragmentComponent<IProcedimiento> implements OnInit, OnDestroy {

  constructor(
    protected actionService: SolicitudProteccionActionService,
  ) {
    super(actionService.FRAGMENT.PROCEDIMIENTOS, actionService);
  }

  ngOnDestroy(): void {
  }

  ngOnInit(): void {
  }

}
