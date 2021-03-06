import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAcreditacion } from '@core/models/prc/acreditacion';
import { IAutor } from '@core/models/prc/autor';
import { ICampoProduccionCientifica } from '@core/models/prc/campo-produccion-cientifica';
import { IIndiceImpacto } from '@core/models/prc/indice-impacto';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { IProyectoPrc } from '@core/models/prc/proyecto-prc';
import { IValorCampo } from '@core/models/prc/valor-campo';
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
import { ICampoProduccionCientificaResponse } from '../campo-produccion-cientifica/campo-produccion-cientifica-response';
import { CAMPO_PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER } from '../campo-produccion-cientifica/campo-produccion-cientifica-response.converter';
import { IEstadoProduccionCientificaRequest } from '../estado-produccion-cientifica/estado-produccion-cientifica-input';
import { IIndiceImpactoResponse } from '../indice-impacto/indice-impacto-response';
import { INDICE_IMPACTO_RESPONSE_CONVERTER } from '../indice-impacto/indice-impacto-response.converter';
import { IProyectoPrcResponse } from '../proyecto/proyecto-prc-response';
import { PROYECTO_PRC_RESPONSE_CONVERTER } from '../proyecto/proyecto-prc-response.converter';
import { IValorCampoResponse } from '../valor-campo/valor-campo-response';
import { VALOR_CAMPO_RESPONSE_CONVERTER } from '../valor-campo/valor-campo-response.converter';
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
   * Obtiene todas las Acreditaciones de una Producci??n cient??fica dado el id
   * @param id id de la Producci??n cient??fica
   * @param options opciones de busqueda
   * @returns Acreditaciones de una Producci??n cient??fica
   */
  findAcreditaciones(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IAcreditacion>> {
    return this.find<IAcreditacionResponse, IAcreditacion>(
      `${this.endpointUrl}/${id}/acreditaciones`,
      options,
      ACREDITACION_RESPONSE_CONVERTER);
  }

  /**
   * Obtiene todos los Autores de una Producci??n cient??fica dado el id
   * @param id id de la Producci??n cient??fica
   * @param options opciones de busqueda
   * @returns Autores de una Producci??n cient??fica
   */
  findAutores(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IAutor>> {
    return this.find<IAutorResponse, IAutor>(
      `${this.endpointUrl}/${id}/autores`,
      options,
      AUTOR_RESPONSE_CONVERTER);
  }

  /**
   * Obtiene todos los Indices de impacto de una Producci??n cient??fica dado el id
   * @param id id de la Producci??n cient??fica
   * @param options opciones de busqueda
   * @returns Indices de impacto de una Producci??n cient??fica
   */
  findIndicesImpacto(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IIndiceImpacto>> {
    return this.find<IIndiceImpactoResponse, IIndiceImpacto>(
      `${this.endpointUrl}/${id}/indices-impacto`,
      options,
      INDICE_IMPACTO_RESPONSE_CONVERTER);
  }

  /**
   * Obtiene todos los Proyectos de una Producci??n cient??fica dado el id
   * @param id id de la Producci??n cient??fica
   * @param options opciones de busqueda
   * @returns Proyectos de una Producci??n cient??fica
   */
  findProyectos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IProyectoPrc>> {
    return this.find<IProyectoPrcResponse, IProyectoPrc>(
      `${this.endpointUrl}/${id}/proyectos`,
      options,
      PROYECTO_PRC_RESPONSE_CONVERTER);
  }

  /**
   * Validar la Producci??n Cient??fica.
   * @param id id de la Producci??n Cient??fica.
   */
  validar(id: number): Observable<IProduccionCientifica> {
    return this.http.patch<IProduccionCientificaResponse>(`${this.endpointUrl}/${id}/validar`, { id })
      .pipe(
        map(response => PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER.toTarget(response))
      );
  }

  /**
   * Rechazar la Producci??n Cient??fica.
   * @param id id de la Producci??n Cient??fica.
   */
  rechazar(id: number, estadoProduccionCientifica: IEstadoProduccionCientificaRequest): Observable<IProduccionCientifica> {
    return this.http.patch<IProduccionCientificaResponse>(`${this.endpointUrl}/${id}/rechazar`, estadoProduccionCientifica)
      .pipe(
        map(response => PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER.toTarget(response))
      );
  }

  /**
   * Comprueba si Producci??n Cient??fica es accesible por el investigador.
   * @param id id de la Producci??n Cient??fica.
   */
  isAccesibleByInvestigador(id: number): Observable<boolean> {
    return this.http.head<boolean>(`${this.endpointUrl}/${id}`, { observe: 'response' })
      .pipe(
        map(response => response.status === 200)
      );
  }

  /**
   * Comprueba si Producci??n Cient??fica es modificable por el investigador.
   * @param id id de la Producci??n Cient??fica.
   */
  isEditableByInvestigador(id: number): Observable<boolean> {
    return this.http.head<boolean>(`${this.endpointUrl}/${id}/modificable`, { observe: 'response' })
      .pipe(
        map(response => response.status === 200)
      );
  }

  /**
   * Obtiene todos los Campos de producci??n cient??fica de una Producci??n cient??fica
   * @param campoProduccionCientifica campo de producci??n cient??fica
   * @param options opciones de busqueda
   * @returns Valores de un Campo de producci??n cient??fica
   */
  findAllCamposProduccionCientifca(
    produccionCientifica: IProduccionCientifica, options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<ICampoProduccionCientifica>> {
    return this.find<ICampoProduccionCientificaResponse, ICampoProduccionCientifica>(
      `${this.endpointUrl}/${produccionCientifica?.id}/campos`,
      options,
      CAMPO_PRODUCCION_CIENTIFICA_RESPONSE_CONVERTER
    );
  }

  /**
   * Obtiene todos los Valores de un Campo de producci??n cient??fica
   * @param campoProduccionCientifica campo de producci??n cient??fica
   * @param options opciones de busqueda
   * @returns Valores de un Campo de producci??n cient??fica
   */
  findValores(
    campoProduccionCientifica: ICampoProduccionCientifica, options?: SgiRestFindOptions
  ): Observable<SgiRestListResult<IValorCampo>> {
    return this.find<IValorCampoResponse, IValorCampo>(
      `${this.endpointUrl}/${campoProduccionCientifica?.produccionCientifica?.id}/campos/${campoProduccionCientifica.id}/valores`,
      options,
      VALOR_CAMPO_RESPONSE_CONVERTER
    );
  }
}
