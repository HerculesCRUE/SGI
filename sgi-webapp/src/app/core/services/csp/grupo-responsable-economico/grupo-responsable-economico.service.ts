import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoResponsableEconomico } from '@core/models/csp/grupo-responsable-economico';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { GRUPO_RESPONSABLE_ECONOMICO_REQUEST_CONVERTER } from './grupo-responsable-economico-request.converter';
import { IGrupoResponsableEconomicoResponse } from './grupo-responsable-economico-response';
import { GRUPO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER } from './grupo-responsable-economico-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoResponsableEconomicoMixinBase:
  FindByIdCtor<number, IGrupoResponsableEconomico, IGrupoResponsableEconomicoResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    GRUPO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoResponsableEconomicoService extends _GrupoResponsableEconomicoMixinBase {
  private static readonly MAPPING = '/gruposresponsableseconomicos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoResponsableEconomicoService.MAPPING}`,
      http,
    );
  }

  /**
   * Actualiza el listado de IDGrupoResponsableEconomico asociados a un IGrupoResponsableEconomico
   *
   * @param id Id del IGrupoResponsableEconomico
   * @param entities Listado de IGrupoResponsableEconomico
   */
  updateList(id: number, entities: IGrupoResponsableEconomico[]): Observable<IGrupoResponsableEconomico[]> {
    return this.http.patch<IGrupoResponsableEconomicoResponse[]>(
      `${this.endpointUrl}/${id}`,
      GRUPO_RESPONSABLE_ECONOMICO_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => GRUPO_RESPONSABLE_ECONOMICO_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
