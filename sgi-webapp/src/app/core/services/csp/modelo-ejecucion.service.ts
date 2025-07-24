import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MODELO_UNIDAD_CONVERTER } from '@core/converters/csp/modelo-unidad.converter';
import { IModeloUnidadBackend } from '@core/models/csp/backend/modelo-unidad-backend';
import { IModeloTipoDocumento } from '@core/models/csp/modelo-tipo-documento';
import { IModeloTipoEnlace } from '@core/models/csp/modelo-tipo-enlace';
import { IModeloTipoFase } from '@core/models/csp/modelo-tipo-fase';
import { IModeloTipoFinalidad } from '@core/models/csp/modelo-tipo-finalidad';
import { IModeloTipoHito } from '@core/models/csp/modelo-tipo-hito';
import { IModeloUnidad } from '@core/models/csp/modelo-unidad';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { CreateCtor, FindAllCtor, FindByIdCtor, RSQLSgiRestFilter, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor, mixinCreate, mixinFindAll, mixinFindById, mixinUpdate } from '@sgi/framework/http/';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IModeloEjecucionResponse } from './modelo-ejecucion/modelo-ejecucion-response';
import { MODELO_EJECUCION_RESPONSE_CONVERTER } from './modelo-ejecucion/modelo-ejecucion-response.converter';
import { IModeloTipoDocumentoResponse } from './modelo-tipo-documento/modelo-tipo-documento-response';
import { MODELO_TIPO_DOCUMENTO_RESPONSE_CONVERTER } from './modelo-tipo-documento/modelo-tipo-documento-response.converter';
import { IModeloTipoEnlaceResponse } from './modelo-tipo-enlace/modelo-tipo-enlace-response';
import { MODELO_TIPO_ENLACE_RESPONSE_CONVERTER } from './modelo-tipo-enlace/modelo-tipo-enlace-response.converter';
import { IModeloTipoFaseResponse } from './modelo-tipo-fase/modelo-tipo-fase-response';
import { MODELO_TIPO_FASE_RESPONSE_CONVERTER } from './modelo-tipo-fase/modelo-tipo-fase-response.converter';
import { IModeloTipoHitoResponse } from './modelo-tipo-hito/modelo-tipo-hito-response';
import { MODELO_TIPO_HITO_RESPONSE_CONVERTER } from './modelo-tipo-hito/modelo-tipo-hito-response.converter';

const _ModeloEjecucionServiceMixinBase:
  CreateCtor<IModeloEjecucion, IModeloEjecucion, IModeloEjecucionResponse, IModeloEjecucionResponse> &
  UpdateCtor<number, IModeloEjecucion, IModeloEjecucion, IModeloEjecucionResponse, IModeloEjecucionResponse> &
  FindByIdCtor<number, IModeloEjecucion, IModeloEjecucionResponse> &
  FindAllCtor<IModeloEjecucion, IModeloEjecucionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinUpdate(
        mixinCreate(
          SgiRestBaseService,
          MODELO_EJECUCION_RESPONSE_CONVERTER,
          MODELO_EJECUCION_RESPONSE_CONVERTER
        ),
        MODELO_EJECUCION_RESPONSE_CONVERTER,
        MODELO_EJECUCION_RESPONSE_CONVERTER
      ),
      MODELO_EJECUCION_RESPONSE_CONVERTER
    ),
    MODELO_EJECUCION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ModeloEjecucionService extends _ModeloEjecucionServiceMixinBase {
  private static readonly MAPPING = '/modeloejecuciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ModeloEjecucionService.MAPPING}`,
      http
    );
  }

  findModeloTipoEnlace(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoEnlace>> {
    return this.find<IModeloTipoEnlaceResponse, IModeloTipoEnlace>(`${this.endpointUrl}/${id}/modelotipoenlaces`, options, MODELO_TIPO_ENLACE_RESPONSE_CONVERTER);
  }

  hasProyectosAsociados(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}/proyectos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  exists(filter: RSQLSgiRestFilter): Observable<boolean> {
    const url = `${this.endpointUrl}`;
    let params = new HttpParams();
    params = params.append('q', filter?.toString());
    return this.http.head(url, { params, observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  findModeloTipoFinalidad(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoFinalidad>> {
    return this.find<IModeloTipoFinalidad, IModeloTipoFinalidad>(`${this.endpointUrl}/${id}/modelotipofinalidades`, options);
  }

  /**
   * Muestra todos los modelos tipo de fase del modelo de ejecución
   * @param id modelo de ejecucion
   * @param options opciones de búsqueda.
   */
  findModeloTipoFaseModeloEjecucion(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoFase>> {
    return this.find<IModeloTipoFaseResponse, IModeloTipoFase>(
      `${this.endpointUrl}/${id}/modelotipofases`,
      options,
      MODELO_TIPO_FASE_RESPONSE_CONVERTER
    );
  }

  /**
   * Muestra todos los modelos tipo de documento
   * @param id modelo de ejecucion
   * @param options opciones de búsqueda.
   */
  findModeloTipoDocumento(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoDocumento>> {
    return this.find<IModeloTipoDocumentoResponse, IModeloTipoDocumento>(
      `${this.endpointUrl}/${id}/modelotipodocumentos`,
      options,
      MODELO_TIPO_DOCUMENTO_RESPONSE_CONVERTER
    );
  }

  findModeloTipoHito(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloTipoHito>> {
    return this.find<IModeloTipoHitoResponse, IModeloTipoHito>(
      `${this.endpointUrl}/${id}/modelotipohitos`,
      options,
      MODELO_TIPO_HITO_RESPONSE_CONVERTER
    );
  }

  /**
   * Encuentra unidades de gestion
   */
  findModeloTipoUnidadGestion(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloUnidad>> {
    return this.find<IModeloUnidadBackend, IModeloUnidad>(`${this.endpointUrl}/${id}/modelounidades`, options, MODELO_UNIDAD_CONVERTER);
  }

  /**
   * Muestra activos y no activos
   * @param options opciones de búsqueda.
   */
  findAllTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IModeloEjecucion>> {
    return this.find<IModeloEjecucionResponse, IModeloEjecucion>(`${this.endpointUrl}/todos`,
      options,
      MODELO_EJECUCION_RESPONSE_CONVERTER);
  }

  /**
   * Desactivar modelo ejecucion
   * @param options opciones de búsqueda.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, undefined);
  }

  /**
   * Reactivar modelo ejecucion
   * @param options opciones de búsqueda.
   */
  reactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/reactivar`, undefined);
  }

}
