import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ITipoProteccion } from '@core/models/pii/tipo-proteccion';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById,
  mixinUpdate, RSQLSgiRestFilter, SgiRestBaseService, SgiRestFilterOperator, SgiRestFindOptions,
  SgiRestListResult,
  UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { ITipoProteccionRequest } from './tipo-proteccion-request';
import { TIPO_PROTECCION_REQUEST_CONVERTER } from './tipo-proteccion-request.converter';
import { ITipoProteccionResponse } from './tipo-proteccion-response';
import { TIPO_PROTECCION_RESPONSE_CONVERTER } from './tipo-proteccion-response.converter';

// tslint:disable-next-line: variable-name
const _TipoProteccionServiceMixinBase:
  CreateCtor<ITipoProteccion, ITipoProteccion, ITipoProteccionRequest, ITipoProteccionResponse> &
  UpdateCtor<number, ITipoProteccion, ITipoProteccion, ITipoProteccionRequest, ITipoProteccionResponse> &
  FindByIdCtor<number, ITipoProteccion, ITipoProteccionResponse> &
  FindAllCtor<ITipoProteccion, ITipoProteccionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          TIPO_PROTECCION_REQUEST_CONVERTER,
          TIPO_PROTECCION_RESPONSE_CONVERTER
        ),
        TIPO_PROTECCION_REQUEST_CONVERTER,
        TIPO_PROTECCION_RESPONSE_CONVERTER
      ),
      TIPO_PROTECCION_RESPONSE_CONVERTER
    ),
    TIPO_PROTECCION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class TipoProteccionService extends _TipoProteccionServiceMixinBase {
  private static readonly MAPPING = '/tiposproteccion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${TipoProteccionService.MAPPING}`,
      http,
    );
  }

  private tiposProteccionSearchByNombre(nombre: string, padreId?: number): SgiRestFindOptions {
    const filter = new RSQLSgiRestFilter('nombre', SgiRestFilterOperator.EQUALS, `${nombre}`);
    if (padreId) {
      filter.and('padreId', SgiRestFilterOperator.EQUALS, `${padreId}`);
    }
    const options: SgiRestFindOptions = {
      filter
    };

    return options;
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoProteccion>> {
    return this.find<ITipoProteccionResponse, ITipoProteccion>(
      `${this.endpointUrl}/todos`,
      options,
      TIPO_PROTECCION_RESPONSE_CONVERTER
    );
  }

  /**
   * Busca los Subtipos pertenecientes al {@link ITipoProteccion} pasado por parámetro.
   *
   * @param id del {@link ITipoProteccion} padre 
   * @returns lista de {@link ITipoProteccion}.
   */
  findSubtipos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoProteccion>> {
    return this.find<ITipoProteccionResponse, ITipoProteccion>(
      `${this.endpointUrl}/${id}/subtipos`,
      options,
      TIPO_PROTECCION_RESPONSE_CONVERTER
    );
  }

  /**
   * Busca los Subtipos pertenecientes al {@link ITipoProteccion} pasado por parámetro sin importar su estado.
   *
   * @param id del {@link ITipoProteccion} padre
   * @returns lista de {@link ITipoProteccion}.
   */
  findAllSubtipos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ITipoProteccion>> {
    return this.find<ITipoProteccionResponse, ITipoProteccion>(
      `${this.endpointUrl}/${id}/subtipos/todos`,
      options,
      TIPO_PROTECCION_RESPONSE_CONVERTER
    );
  }

  /**
   * Busca los {@link ITipoProteccion} con el nombre pasado por parámetro.
   *
   * @param idTipoProteccion Id del {@link ITipoProteccion} padre.
   * @param nombre Nombre del {@link ITipoProteccion}
   * @returns Entidad {@link ITipoProteccion} encontrada.
   */
  finTiposProteccionByNombre(idTipoProteccion: number, nombre: string): Observable<SgiRestListResult<ITipoProteccion>> {

    return this.find<ITipoProteccionResponse, ITipoProteccion>(
      `${this.endpointUrl}/${idTipoProteccion}/subtipos`,
      this.tiposProteccionSearchByNombre(nombre),
      TIPO_PROTECCION_RESPONSE_CONVERTER
    );
  }

  /**
   * Busca los {@link ITipoProteccion} que sean Subtipos con el nombre y el padre pasados por parámetro.
   *
   * @param nombre Nombre del {@link ITipoProteccion}
   * @param padreId Id del {@link ITipoProteccion} padre
   * @returns Entidades {@link ITipoProteccion} encontradas.
   */
  findSubtiposProteccionByNombre(nombre: string, padreId: number):
    Observable<SgiRestListResult<ITipoProteccion>> {

    return this.find<ITipoProteccionResponse, ITipoProteccion>(
      `${this.endpointUrl}/`,
      this.tiposProteccionSearchByNombre(nombre, padreId),
      TIPO_PROTECCION_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar un Tipo de Resultado
   * @param options opciones de búsqueda.
   */
  activate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar Tipo de Protección
   * @param options Opciones de búsqueda.
   */
  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

  /**
   * TODO FIXME Implementar metodo consultando Backend
   * Retorna un {@link boolean} con valor True si se puede editar el {@link ITipoPropiedad} al editar un {@link ITipoProteccion}
   * @param options Opciones de búsqueda.
   */
  hasAssociatedSolicitudProteccion(id: number): boolean {
    return false;
  }

}
