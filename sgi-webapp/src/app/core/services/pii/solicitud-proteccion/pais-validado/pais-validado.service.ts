import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IPaisValidado } from '@core/models/pii/pais-validado';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IPaisValidadoRequest } from './pais-validado-request';
import { PAIS_VALIDADO_REQUEST_CONVERTER } from './pais-validado-request.converter';
import { IPaisValidadoResponse } from './pais-validado-response';
import { PAIS_VALIDADO_RESPONSE_CONVERTER } from './pais-validado-response.converter';

// tslint:disable-next-line: variable-name
const _PaisValidadoMixinBase:
  FindAllCtor<IPaisValidado, IPaisValidadoResponse> &
  FindByIdCtor<number, IPaisValidado, IPaisValidadoResponse> &
  CreateCtor<IPaisValidado, IPaisValidado, IPaisValidadoRequest, IPaisValidadoResponse> &
  UpdateCtor<number, IPaisValidado, IPaisValidado, IPaisValidadoRequest, IPaisValidadoResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        mixinUpdate(
          SgiRestBaseService,
          PAIS_VALIDADO_REQUEST_CONVERTER,
          PAIS_VALIDADO_RESPONSE_CONVERTER
        ),
        PAIS_VALIDADO_REQUEST_CONVERTER,
        PAIS_VALIDADO_RESPONSE_CONVERTER
      ),
      PAIS_VALIDADO_RESPONSE_CONVERTER
    ),
    PAIS_VALIDADO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class PaisValidadoService extends _PaisValidadoMixinBase {

  private static readonly MAPPING = '/paisesvalidados';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${PaisValidadoService.MAPPING}`,
      http,
    );
  }

  /**
   * Elimina un {@link IPaisValidado}
   *
   * @param paisValidadoId Id del {@link IPaisValidado} a eliminar
   * @returns Observable of void
   */
  deleteById(paisValidadoId: number): Observable<void> {
    const endpointUrl = `${this.endpointUrl}/${paisValidadoId}`;
    return this.http.delete<void>(endpointUrl);
  }

}
