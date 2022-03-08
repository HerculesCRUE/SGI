import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAcreditacion } from '@core/models/prc/acreditacion';
import { IAutor } from '@core/models/prc/autor';
import { IIndiceImpacto } from '@core/models/prc/indice-impacto';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { IProyectoPrc } from '@core/models/prc/proyecto-prc';
import { environment } from '@env';
import {
  FindByIdCtor, mixinFindById, SgiRestBaseService,
  SgiRestFindOptions, SgiRestListResult
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IAcreditacionResponse } from '../acreditacion/acreditacion-response';
import { ACREDITACION_RESPONSE_CONVERTER } from '../acreditacion/acreditacion-response.converter';
import { IAutorResponse } from '../autor/autor-response';
import { AUTOR_RESPONSE_CONVERTER } from '../autor/autor-response.converter';
import { IEstadoProduccionCientificaRequest } from '../estado-produccion-cientifica/estado-produccion-cientifica-input';
import { IIndiceImpactoResponse } from '../indice-impacto/indice-impacto-response';
import { INDICE_IMPACTO_RESPONSE_CONVERTER } from '../indice-impacto/indice-impacto-response.converter';
import { IProyectoPrcResponse } from '../proyecto/proyecto-prc-response';
import { PROYECTO_PRC_RESPONSE_CONVERTER } from '../proyecto/proyecto-prc-response.converter';
import { IProduccionCientificaResponse } from './produccion-cientifica-response';
import { PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER } from './produccion-cientifica-response.converter';

// tslint:disable-next-line: variable-name
const _ProduccionCientificaServiceMixinBase:
  FindByIdCtor<number, IProduccionCientifica, IProduccionCientificaResponse> &
  typeof SgiRestBaseService = mixinFindById(
    SgiRestBaseService,
    PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProduccionCientificaService extends _ProduccionCientificaServiceMixinBase {

  private static readonly MAPPING = '/producciones-cientificas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ProduccionCientificaService.MAPPING}`,
      http,
    );
  }

  /**
   * Obtiene todas las Acreditaciones de una Producción científica dado el id
   * @param id id de la Producción científica
   * @param options opciones de busqueda
   * @returns Acreditaciones de una Producción científica
   */
  findAcreditaciones(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IAcreditacion>> {
    return this.find<IAcreditacionResponse, IAcreditacion>(
      `${this.endpointUrl}/${id}/acreditaciones`,
      options,
      ACREDITACION_RESPONSE_CONVERTER);
  }

  /**
   * Obtiene todos los Autores de una Producción científica dado el id
   * @param id id de la Producción científica
   * @param options opciones de busqueda
   * @returns Autores de una Producción científica
   */
  findAutores(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IAutor>> {
    return this.find<IAutorResponse, IAutor>(
      `${this.endpointUrl}/${id}/autores`,
      options,
      AUTOR_RESPONSE_CONVERTER);
  }

  /**
   * Obtiene todos los Indices de impacto de una Producción científica dado el id
   * @param id id de la Producción científica
   * @param options opciones de busqueda
   * @returns Indices de impacto de una Producción científica
   */
  findIndicesImpacto(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IIndiceImpacto>> {
    return this.find<IIndiceImpactoResponse, IIndiceImpacto>(
      `${this.endpointUrl}/${id}/indices-impacto`,
      options,
      INDICE_IMPACTO_RESPONSE_CONVERTER);
  }

  /**
   * Obtiene todos los Proyectos de una Producción científica dado el id
   * @param id id de la Producción científica
   * @param options opciones de busqueda
   * @returns Proyectos de una Producción científica
   */
  findProyectos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoPrc>> {
    return this.find<IProyectoPrcResponse, IProyectoPrc>(
      `${this.endpointUrl}/${id}/proyectos`,
      options,
      PROYECTO_PRC_RESPONSE_CONVERTER);
  }

  /**
   * Validar la Producción Científica.
   * @param id id de la Producción Científica.
   */
  validar(id: number): Observable<IProduccionCientifica> {
    return this.http.patch<IProduccionCientificaResponse>(`${this.endpointUrl}/${id}/validar`, { id })
      .pipe(
        map(response => PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER.toTarget(response))
      );
  }

  /**
   * Rechazar la Producción Científica.
   * @param id id de la Producción Científica.
   */
  rechazar(id: number, estadoProduccionCientifica: IEstadoProduccionCientificaRequest): Observable<IProduccionCientifica> {
    return this.http.patch<IProduccionCientificaResponse>(`${this.endpointUrl}/${id}/rechazar`, estadoProduccionCientifica)
      .pipe(
        map(response => PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER.toTarget(response))
      );
  }
}
