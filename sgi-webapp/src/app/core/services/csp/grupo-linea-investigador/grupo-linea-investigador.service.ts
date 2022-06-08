import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { environment } from '@env';
import { FindByIdCtor, mixinFindById, SgiRestBaseService } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { GRUPO_LINEA_INVESTIGADOR_REQUEST_CONVERTER } from './grupo-linea-investigador-request.converter';
import { IGrupoLineaInvestigadorResponse } from './grupo-linea-investigador-response';
import { GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER } from './grupo-linea-investigador-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoLineaInvestigadorMixinBase:
  FindByIdCtor<number, IGrupoLineaInvestigador, IGrupoLineaInvestigadorResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoLineaInvestigadorService extends _GrupoLineaInvestigadorMixinBase {
  private static readonly MAPPING = '/gruposlineasinvestigadores';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoLineaInvestigadorService.MAPPING}`,
      http,
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Actualiza el listado de GrupoLineaInvestigador asociados a un grupo
   *
   * @param id Id del IGrupoLineaInvestigador
   * @param entities Listado de IGrupoLineaInvestigador
   */
  updateList(id: number, entities: IGrupoLineaInvestigador[]): Observable<IGrupoLineaInvestigador[]> {
    return this.http.patch<IGrupoLineaInvestigadorResponse[]>(
      `${this.endpointUrl}/${id}`,
      GRUPO_LINEA_INVESTIGADOR_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

}
