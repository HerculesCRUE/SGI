import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IDireccionTesis } from '@core/models/prc/direccion-tesis';
import { environment } from '@env';
import { FindAllCtor, mixinFindAll, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IDireccionTesisResponse } from './direccion-tesis-response';
import { DIRECCION_TESIS_RESPONSE_CONVERTER } from './direccion-tesis-response.converter';

// tslint:disable-next-line: variable-name
const _DireccionTesisMixinBase:
  FindAllCtor<IDireccionTesis, IDireccionTesisResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    SgiRestBaseService,
    DIRECCION_TESIS_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class DireccionTesisService extends _DireccionTesisMixinBase {

  private static readonly MAPPING = '/producciones-cientificas/direcciones-tesis';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${DireccionTesisService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra las direcciones de tesis a los que pertenece el investigador actual
   *
   * @param options opciones de búsqueda.
   */
  findDireccionesTesisInvestigador(options?: SgiRestFindOptions): Observable<SgiRestListResult<IDireccionTesis>> {
    return this.find<IDireccionTesisResponse, IDireccionTesis>(
      `${this.endpointUrl}/investigador`,
      options,
      DIRECCION_TESIS_RESPONSE_CONVERTER
    );
  }

}
