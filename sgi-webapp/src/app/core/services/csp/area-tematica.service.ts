import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IAreaTematicaResponse } from './area-tematica/area-tematica-response';
import { AREA_TEMATICA_RESPONSE_CONVERTER } from './area-tematica/area-tematica-response.converter';


// tslint:disable-next-line: variable-name
const _TipoAreaTematicaServiceMixinBase:
  CreateCtor<IAreaTematica, IAreaTematica, IAreaTematicaResponse, IAreaTematicaResponse> &
  UpdateCtor<number, IAreaTematica, IAreaTematica, IAreaTematicaResponse, IAreaTematicaResponse> &
  FindByIdCtor<number, IAreaTematica, IAreaTematicaResponse> &
  FindAllCtor<IAreaTematica, IAreaTematicaResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          AREA_TEMATICA_RESPONSE_CONVERTER,
          AREA_TEMATICA_RESPONSE_CONVERTER
        ),
        AREA_TEMATICA_RESPONSE_CONVERTER,
        AREA_TEMATICA_RESPONSE_CONVERTER
      ),
      AREA_TEMATICA_RESPONSE_CONVERTER
    ),
    AREA_TEMATICA_RESPONSE_CONVERTER

  );

@Injectable({
  providedIn: 'root'
})
export class AreaTematicaService extends _TipoAreaTematicaServiceMixinBase {
  private static readonly MAPPING = '/areatematicas';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${AreaTematicaService.MAPPING}`,
      http
    );
  }

  /**
   * Muestra las areas temáticas que tienen padre a NULL
   * @param options opciones de búsqueda.
   */
  findAllGrupo(options?: SgiRestFindOptions): Observable<SgiRestListResult<IAreaTematica>> {
    return this.find<IAreaTematicaResponse, IAreaTematica>(
      `${this.endpointUrl}/grupo`,
      options,
      AREA_TEMATICA_RESPONSE_CONVERTER
    );
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IAreaTematica>> {
    return this.find<IAreaTematicaResponse, IAreaTematica>(
      `${this.endpointUrl}/grupo/todos`,
      options,
      AREA_TEMATICA_RESPONSE_CONVERTER
    );
  }

  /**
   * Encuentra todos los hijos del padre (mat-tree)
   * @param id numero padre
   * @param options opciones de busqueda
   */
  findAllHijosArea(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IAreaTematica>> {
    return this.find<IAreaTematicaResponse, IAreaTematica>(
      `${this.endpointUrl}/${id}/hijos`,
      options,
      AREA_TEMATICA_RESPONSE_CONVERTER
    );
  }

  /**
   * Desactivar area tematica
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar area tematica
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }
}