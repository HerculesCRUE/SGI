import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoResponsableEconomico } from '@core/models/csp/proyecto-responsable-economico';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { PROYECTO_RESPONSABLE_ECONOMICO_REQUEST_CONVERTER } from './proyecto-responsable-economico-request.converter';
import { IProyectoResponsableEconomicoResponse } from './proyecto-responsable-economico-response';
import { PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER } from './proyecto-responsable-economico-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoResponsableEconomicoServiceMixinBase:
  FindByIdCtor<number, IProyectoResponsableEconomico, IProyectoResponsableEconomicoResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    SgiRestBaseService,
    PROYECTO_RESPONSABLE_ECONOMICO_REQUEST_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoResponsableEconomicoService extends _ProyectoResponsableEconomicoServiceMixinBase {
  private static readonly MAPPING = '/proyectoresponsableseconomicos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoResponsableEconomicoService.MAPPING}`,
      http
    );
  }

  updateProyectoResponsablesEconomicos(solicitudId: number, responsablesEconomicos: IProyectoResponsableEconomico[]):
    Observable<IProyectoResponsableEconomico[]> {
    return this.http.patch<IProyectoResponsableEconomicoResponse[]>(
      `${this.endpointUrl}/${solicitudId}`,
      PROYECTO_RESPONSABLE_ECONOMICO_REQUEST_CONVERTER.fromTargetArray(responsablesEconomicos)
    ).pipe(
      map(response => PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
