import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ISolicitudProyectoResponsableEconomico } from '@core/models/csp/solicitud-proyecto-responsable-economico';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_REQUEST_CONVERTER } from './solicitud-proyecto-responsable-economico-request.converter';
import { ISolicitudProyectoResponsableEconomicoResponse } from './solicitud-proyecto-responsable-economico-response';
import { SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER } from './solicitud-proyecto-responsable-economico-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudProyectoResponsableEconomicoServiceMixinBase:
  FindByIdCtor<number, ISolicitudProyectoResponsableEconomico, ISolicitudProyectoResponsableEconomicoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudProyectoResponsableEconomicoService extends _SolicitudProyectoResponsableEconomicoServiceMixinBase {
  private static readonly MAPPING = '/solicitudproyectoresponsableseconomicos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${SolicitudProyectoResponsableEconomicoService.MAPPING}`,
      http
    );
  }

  updateSolicitudProyectoResponsablesEconomicos(solicitudId: number, responsablesEconomicos: ISolicitudProyectoResponsableEconomico[]):
    Observable<ISolicitudProyectoResponsableEconomico[]> {
    return this.http.patch<ISolicitudProyectoResponsableEconomicoResponse[]>(`${this.endpointUrl}/${solicitudId}`,
      SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_REQUEST_CONVERTER.fromTargetArray(responsablesEconomicos)
    ).pipe(
      map(response => SOLICITUD_PROYECTO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
