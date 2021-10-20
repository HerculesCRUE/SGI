import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';


@Injectable()
export class NotificacionPresupuestoSgeActionService extends ActionService {

  constructor(
    route: ActivatedRoute,
  ) {
    super();
  }

}
