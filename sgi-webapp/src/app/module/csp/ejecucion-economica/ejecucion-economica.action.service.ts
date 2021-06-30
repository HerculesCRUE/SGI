import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IProyectoProyectoSge } from '@core/models/csp/proyecto-proyecto-sge';
import { ActionService } from '@core/services/action-service';
import { ProyectoProyectoSgeService } from '@core/services/csp/proyecto-proyecto-sge.service';
import { NGXLogger } from 'ngx-logger';

export interface IEjecucionEconomicaData {
  readonly: boolean;
  proyectoProyectoSge: IProyectoProyectoSge;
}

@Injectable()
export class EjecucionEconomicaActionService extends ActionService {

  public readonly FRAGMENT = {
  };

  private readonly data: IEjecucionEconomicaData;

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    private proyectoProyectoSgeService: ProyectoProyectoSgeService,
  ) {
    super();
  }

}
