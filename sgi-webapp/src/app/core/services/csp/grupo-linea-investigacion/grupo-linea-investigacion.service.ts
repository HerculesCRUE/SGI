import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IGrupoLineaClasificacion } from '@core/models/csp/grupo-linea-clasificacion';
import { IGrupoLineaEquipoInstrumental } from '@core/models/csp/grupo-linea-equipo-instrumental';
import { IGrupoLineaInvestigacion } from '@core/models/csp/grupo-linea-investigacion';
import { IGrupoLineaInvestigador } from '@core/models/csp/grupo-linea-investigador';
import { environment } from '@env';
import {
  CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { from, Observable } from 'rxjs';
import { map, mergeMap, reduce } from 'rxjs/operators';
import { IGrupoLineaClasificacionResponse } from '../grupo-linea-clasificacion/grupo-linea-clasificacion-response';
import { GRUPO_LINEA_CLASIFICACION_RESPONSE_CONVERTER } from '../grupo-linea-clasificacion/grupo-linea-clasificacion-response.converter';
import { IGrupoLineaEquipoInstrumentalResponse } from '../grupo-linea-equipo-instrumental/grupo-linea-equipo-instrumental-response';
import { GRUPO_LINEA_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER } from '../grupo-linea-equipo-instrumental/grupo-linea-equipo-instrumental-response.converter';
import { IGrupoLineaInvestigadorResponse } from '../grupo-linea-investigador/grupo-linea-investigador-response';
import { GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER } from '../grupo-linea-investigador/grupo-linea-investigador-response.converter';
import { IGrupoLineaInvestigacionRequest } from './grupo-linea-investigacion-request';
import { GRUPO_LINEA_INVESTIGACION_REQUEST_CONVERTER } from './grupo-linea-investigacion-request.converter';
import { IGrupoLineaInvestigacionResponse } from './grupo-linea-investigacion-response';
import { GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER } from './grupo-linea-investigacion-response.converter';

// tslint:disable-next-line: variable-name
const _GrupoLineaInvestigacionMixinBase:
  CreateCtor<IGrupoLineaInvestigacion, IGrupoLineaInvestigacion, IGrupoLineaInvestigacionRequest, IGrupoLineaInvestigacionResponse> &
  UpdateCtor<
    number,
    IGrupoLineaInvestigacion,
    IGrupoLineaInvestigacion,
    IGrupoLineaInvestigacionRequest,
    IGrupoLineaInvestigacionResponse
  > &
  FindByIdCtor<number, IGrupoLineaInvestigacion, IGrupoLineaInvestigacionResponse> &
  FindAllCtor<IGrupoLineaInvestigacion, IGrupoLineaInvestigacionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          GRUPO_LINEA_INVESTIGACION_REQUEST_CONVERTER,
          GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER
        ),
        GRUPO_LINEA_INVESTIGACION_REQUEST_CONVERTER,
        GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER
      ),
      GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER
    ),
    GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class GrupoLineaInvestigacionService extends _GrupoLineaInvestigacionMixinBase {
  private static readonly MAPPING = '/gruposlineasinvestigacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${GrupoLineaInvestigacionService.MAPPING}`,
      http,
    );
  }

  /**
   * Busca todos lineas de los grupos
   * 
   * @param ids lista de identificadores
   * @returns la lista de lineas
   */
  findAllByIdIn(ids: number[]): Observable<SgiRestListResult<IGrupoLineaInvestigacion>> {
    const options: SgiRestFindOptions = {
      filter: new RSQLSgiRestFilter('id', SgiRestFilterOperator.IN, ids.map(id => id.toString()))
    };

    return this.findAll(options);
  }

  /**
   * Busca todos las lineas de los grupos dividiendo la lista de ids en lotes con el tamaño maximo de batchSize 
   * y haciendo tantas peticiones como lotes se generen para hacer la busqueda
   *
   * @param ids lista de identificadores
   * @param batchSize tamaño maximo de los lotes
   * @param maxConcurrentBatches número máximo de llamadas paralelas para recuperar los lotes (por defecto 10)
   * @returns la lista de lineas
   */
  findAllInBactchesByIdIn(ids: number[], batchSize: number, maxConcurrentBatches: number = 10): Observable<IGrupoLineaInvestigacion[]> {
    const batches: number[][] = [];
    for (let i = 0; i < ids.length; i += batchSize) {
      batches.push(ids.slice(i, i + batchSize));
    }

    return from(batches).pipe(
      mergeMap(batch =>
        this.findAllByIdIn(batch).pipe(
          map(response => response.items)
        ),
        maxConcurrentBatches
      ),
      reduce((acc, items) => acc.concat(items), [] as IGrupoLineaInvestigacion[])
    );
  }

  public deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

  /**
   * Actualiza el listado de GrupoLineaInvestigacion asociados a un grupo
   *
   * @param id Id del IGrupoLineaInvestigacion
   * @param entities Listado de IGrupoLineaInvestigacion
   */
  updateList(id: number, entities: IGrupoLineaInvestigacion[]): Observable<IGrupoLineaInvestigacion[]> {
    return this.http.patch<IGrupoLineaInvestigacionResponse[]>(
      `${this.endpointUrl}/${id}`,
      GRUPO_LINEA_INVESTIGACION_REQUEST_CONVERTER.fromTargetArray(entities)
    ).pipe(
      map(response => GRUPO_LINEA_INVESTIGACION_RESPONSE_CONVERTER.toTargetArray(response))
    );
  }

  /**
   * Comprueba si existe el grupo lineas de investigación
   *
   * @param id Identificador del grupo linea de investigación
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Comprueba si tiene permisos de edición del grupo líneas de investigación
   *
   * @param id Id del grupo linea de investigación
   */
  modificable(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/modificable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera la lista de líneas de investigadores
   * @param id Identificador del grupo de investigación
   * @param options opciones de búsqueda.
   */
  findLineasInvestigadores(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoLineaInvestigador>> {
    return this.find<IGrupoLineaInvestigadorResponse, IGrupoLineaInvestigador>(
      `${this.endpointUrl}/${id}/lineas-investigadores`,
      options,
      GRUPO_LINEA_INVESTIGADOR_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera los IGrupoLineaClasificacion del IGrupoLineaInvestigacion
   *
   * @param id Identificador del grupo de investigación
   * @param options opciones de busqueda
   * @returns observable con la lista de IGrupoLineaClasificacion del IGrupoLineaInvestigacion
   */
  findClasificaciones(id: number, options?: SgiRestFindOptions):
    Observable<SgiRestListResult<IGrupoLineaClasificacion>> {
    return this.find<IGrupoLineaClasificacionResponse, IGrupoLineaClasificacion>(
      `${this.endpointUrl}/${id}/clasificaciones`,
      options,
      GRUPO_LINEA_CLASIFICACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Recupera la lista de equipos instrumentales
   *
   * @param id Identificador del grupo de investigación
   * @param options opciones de búsqueda.
   */
  findLineasEquiposInstrumentales(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IGrupoLineaEquipoInstrumental>> {
    return this.find<IGrupoLineaEquipoInstrumentalResponse, IGrupoLineaEquipoInstrumental>(
      `${this.endpointUrl}/${id}/lineas-equipos-instrumentales`,
      options,
      GRUPO_LINEA_EQUIPO_INSTRUMENTAL_RESPONSE_CONVERTER
    );
  }

}
