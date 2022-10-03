import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IObraArtistica } from '@core/models/prc/obra-artistica';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IObraArtisticaResponse } from './obra-artistica-response';
import { OBRA_ARTISTICA_RESPONSE_CONVERTER } from './obra-artistica-response.converter';

// tslint:disable-next-line: variable-name
const _ObraArtisticaMixinBase:
  FindAllCtor<IObraArtistica, IObraArtisticaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    OBRA_ARTISTICA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ObraArtisticaService extends _ObraArtisticaMixinBase {

  private static readonly MAPPING = '/producciones-cientificas/obras-artisticas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ObraArtisticaService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra las obras artisticas a los que pertenece el investigador actual
   *
   * @param options opciones de búsqueda.
   */
  findObrasArtisticasInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<IObraArtistica>> {
    return this.find<IObraArtisticaResponse, IObraArtistica>(
      `${this.endpointUrl}/investigador`,
      options,
      OBRA_ARTISTICA_RESPONSE_CONVERTER
    );
  }

}
