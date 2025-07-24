import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoProrroga } from '@core/models/csp/proyecto-prorroga';
import { IProyectoProrrogaDocumento } from '@core/models/csp/proyecto-prorroga-documento';
import { environment } from '@env';
import { CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IProyectoProrrogaDocumentoResponse } from './proyecto-prorroga-documento/proyecto-prorroga-documento-response';
import { PROYECTO_PRORROGA_DOCUMENTO_RESPONSE_CONVERTER } from './proyecto-prorroga-documento/proyecto-prorroga-documento-response.converter';
import { IProyectoProrrogaResponse } from './proyecto-prorroga/proyecto-prorroga-response';
import { PROYECTO_PRORROGA_RESPONSE_CONVERTER } from './proyecto-prorroga/proyecto-prorroga-response.converter';

// tslint:disable-next-line: variable-name
const _ProyectoProrrogaServiceMixinBase:
  CreateCtor<IProyectoProrroga, IProyectoProrroga, IProyectoProrrogaResponse, IProyectoProrrogaResponse> &
  UpdateCtor<number, IProyectoProrroga, IProyectoProrroga, IProyectoProrrogaResponse, IProyectoProrrogaResponse> &
  FindByIdCtor<number, IProyectoProrroga, IProyectoProrrogaResponse> &
  typeof SgiRestBaseService =
  mixinFindById(
    mixinUpdate(
      mixinCreate(
        SgiRestBaseService,
        PROYECTO_PRORROGA_RESPONSE_CONVERTER,
        PROYECTO_PRORROGA_RESPONSE_CONVERTER),
      PROYECTO_PRORROGA_RESPONSE_CONVERTER,
      PROYECTO_PRORROGA_RESPONSE_CONVERTER
    ),
    PROYECTO_PRORROGA_RESPONSE_CONVERTER)



@Injectable({
  providedIn: 'root'
})
export class ProyectoProrrogaService extends _ProyectoProrrogaServiceMixinBase {
  private static readonly MAPPING = '/proyecto-prorrogas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoProrrogaService.MAPPING}`,
      http
    );
  }

  /**
   * Comprueba si existe un proyecto prorroga
   *
   * @param id Id del proyecto prorroga
   * @retrurn true/false
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(x => x.status === 200)
    );
  }

  /**
   * Recupera los documentos del proyecto periodo de seguimiento
   * @param id Id del proyecto periodo de seguimiento
   * @return la lista de ProyectoPeridoSeguimientoDocumento
   */
  findDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoProrrogaDocumento>> {
    return this.find<IProyectoProrrogaDocumentoResponse, IProyectoProrrogaDocumento>(
      `${this.endpointUrl}/${id}/prorrogadocumentos`,
      options,
      PROYECTO_PRORROGA_DOCUMENTO_RESPONSE_CONVERTER
    );
  }

  /**
   * Comprueba si existe documentos asociados al proyecto prorroga
   *
   * @param id Id del proyecto prorroga
   * @retrurn true/false
   */
  existsDocumentos(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/prorrogadocumentos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }
}
