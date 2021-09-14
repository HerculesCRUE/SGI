import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IInformePatentabilidad } from '@core/models/pii/informe-patentabilidad';
import { IInvencion } from '@core/models/pii/invencion';
import { IInvencionAreaConocimiento } from '@core/models/pii/invencion-area-conocimiento';
import { IInvencionDocumento } from '@core/models/pii/invencion-documento';
import { IInvencionInventor } from '@core/models/pii/invencion-inventor';
import { IInvencionSectorAplicacion } from '@core/models/pii/invencion-sector-aplicacion';
import { environment } from '@env';
import {
  CreateCtor,
  FindAllCtor,
  FindByIdCtor,
  mixinCreate,
  mixinFindAll,
  mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IInformePatentabilidadResponse } from '../informe-patentabilidad/informe-patentabilidad-response';
import { INFORME_PATENTABILIDAD_RESPONSE_CONVERTER } from '../informe-patentabilidad/informe-patentabilidad-response.converter';
import { INVENCION_INVENTOR_REQUEST_CONVERTER } from '../invencion-inventor/invencion-inventor-request.converter';
import { IInvencionInventorResponse } from '../invencion-inventor/invencion-inventor-response';
import { INVENCION_INVENTOR_RESPONSE_CONVERTER } from '../invencion-inventor/invencion-inventor-response.converter';
import { INVENCION_AREACONOCIMIENTO_REQUEST_CONVERTER } from './invencion-area-conocimiento/invencion-area-conocimiento-request.converter';
import { IInvencionAreaConocimientoResponse } from './invencion-area-conocimiento/invencion-area-conocimiento-response';
import {
  INVENCION_AREACONOCIMIENTO_RESPONSE_CONVERTER
} from './invencion-area-conocimiento/invencion-area-conocimiento-response.converter';
import { IInvencionDocumentoResponse } from './invencion-documento/invencion-documento-response';
import { INVENCION_DOCUMENTO_RESPONSE_CONVERTER } from './invencion-documento/invencion-documento-response.converter';
import { IInvencionRequest } from './invencion-request';
import { INVENCION_REQUEST_CONVERTER } from './invencion-request.converter';
import { IInvencionResponse } from './invencion-response';
import { INVENCION_RESPONSE_CONVERTER } from './invencion-response.converter';
import { INVENCION_SECTORAPLICACION_REQUEST_CONVERTER } from './invencion-sector-aplicacion/invencion-sector-aplicacion-request.converter';
import { IInvencionSectorAplicacionResponse } from './invencion-sector-aplicacion/invencion-sector-aplicacion-response';
import {
  INVENCION_SECTORAPLICACION_RESPONSE_CONVERTER
} from './invencion-sector-aplicacion/invencion-sector-aplicacion-response.converter';
import { SOLICITUD_PROTECCION_RESPONSE_CONVERTER } from './solicitud-proteccion/solicitud-proteccion-response.converter';
import { ISolicitudProteccion } from '@core/models/pii/solicitud-proteccion';
import { ISolicitudProteccionResponse } from './solicitud-proteccion/solicitud-proteccion-response';
import { IInvencionGasto } from '@core/models/pii/invencion-gasto';
import { IInvencionGastoResponse } from './invencion-gasto/invencion-gasto-response';
import { INVENCION_GASTO_RESPONSE_CONVERTER } from './invencion-gasto/invencion-gasto-response.converter';

// tslint:disable-next-line: variable-name
const _InvencionServiceMixinBase:
  FindAllCtor<IInvencion, IInvencionResponse> &
  FindByIdCtor<number, IInvencion, IInvencionResponse> &
  CreateCtor<IInvencion, IInvencion, IInvencionRequest, IInvencionResponse> &
  UpdateCtor<number, IInvencion, IInvencion, IInvencionRequest, IInvencionResponse> &
  typeof SgiRestBaseService = mixinFindAll(
    mixinFindById(
      mixinCreate(
        mixinUpdate(
          SgiRestBaseService,
          INVENCION_REQUEST_CONVERTER,
          INVENCION_RESPONSE_CONVERTER
        ),
        INVENCION_REQUEST_CONVERTER,
        INVENCION_RESPONSE_CONVERTER
      ),
      INVENCION_RESPONSE_CONVERTER
    ),
    INVENCION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class InvencionService extends _InvencionServiceMixinBase {

  private static readonly MAPPING = '/invenciones';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.pii}${InvencionService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IInvencion>> {
    return this.find<IInvencionResponse, IInvencion>(
      `${this.endpointUrl}/todos`,
      options,
      INVENCION_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar la invención
   * @param options id de la invención.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar la invención
   * @param options id de la invención.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }

  /**
   * Comprueba si existe una invencion
   *
   * @param id Id de la invencion
   */
  exists(id: number): Observable<boolean> {
    const url = `${this.endpointUrl}/${id}`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  /**
   * Recupera los sectores de aplicación asociados a la Invencion con el id indicado
   * @param id Identificador de la Invencion
   */
  findSectoresAplicacion(id: number): Observable<IInvencionSectorAplicacion[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/sectoresaplicacion`;
    const params = new HttpParams().set('id', id.toString());
    return this.http.get<IInvencionSectorAplicacionResponse[]>(endpointUrl, { params })
      .pipe(
        map(response => {
          return INVENCION_SECTORAPLICACION_RESPONSE_CONVERTER.toTargetArray(response ?? []);
        })
      );
  }

  /**
   * Actualiza los sectores de aplicación asociados a la Invencion con el id indicado
   * @param id Identificador del Invencion
   * @param sectoresAplicacion sectores de aplicacion a actualizar
   */
  updateSectoresAplicacion(id: number, sectoresAplicacion: IInvencionSectorAplicacion[]): Observable<IInvencionSectorAplicacion[]> {
    return this.http.patch<IInvencionSectorAplicacionResponse[]>(`${this.endpointUrl}/${id}/sectoresaplicacion`,
      INVENCION_SECTORAPLICACION_REQUEST_CONVERTER.fromTargetArray(sectoresAplicacion)
    ).pipe(
      map((response => INVENCION_SECTORAPLICACION_RESPONSE_CONVERTER.toTargetArray(response ?? [])))
    );
  }

  /**
   * Obtiene todos los documentos de una invención dado el id
   * @param id id de la invencion
   * @param options opciones de busqueda
   * @returns documentos de una invencion
   */
  findAllInvencionDocumentos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IInvencionDocumento>> {
    return this.find<IInvencionDocumentoResponse, IInvencionDocumento>(
      `${this.endpointUrl}/${id}/invenciondocumentos`,
      options,
      INVENCION_DOCUMENTO_RESPONSE_CONVERTER);
  }

  /**
   * Recupera las áreas de conocimiento asociadas a la Invencion con el id indicado
   * @param id Identificador de la Invencion
   */
  findAreasConocimiento(id: number): Observable<IInvencionAreaConocimiento[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/areasconocimiento`;
    return this.http.get<IInvencionAreaConocimientoResponse[]>(endpointUrl)
      .pipe(
        map(response => {
          return INVENCION_AREACONOCIMIENTO_RESPONSE_CONVERTER.toTargetArray(response);
        })
      );
  }

  /**
   * Actualiza las áreas de conocimiento  asociadas a la Invencion con el id indicado
   * @param id Identificador del Invencion
   * @param areasConocimiento areas de conocimiento a actualizar
   */
  updateAreasConocimiento(id: number, areasConocimiento: IInvencionAreaConocimiento[]): Observable<IInvencionAreaConocimiento[]> {
    return this.http.patch<IInvencionAreaConocimientoResponse[]>(`${this.endpointUrl}/${id}/areasconocimiento`,
      INVENCION_AREACONOCIMIENTO_REQUEST_CONVERTER.fromTargetArray(areasConocimiento)
    ).pipe(
      map((response => INVENCION_AREACONOCIMIENTO_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Recupera los informes de patentabilidad asociados a la Invencion con el id indicado
   * @param id Identificador de la Invencion
   */
  findInformesPatentabilidad(id: number): Observable<IInformePatentabilidad[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/informespatentabilidad`;
    return this.http.get<IInformePatentabilidadResponse[]>(endpointUrl)
      .pipe(
        map(response => {
          return INFORME_PATENTABILIDAD_RESPONSE_CONVERTER.toTargetArray(response);
        })
      );
  }

  /**
   * Recupera los {@link IInvencionInventor} asociados a la {@link IInvencion} con el id indicado.
   *
   * @param id Id de la {@link IInvencion}
   */
  findInventores(id: number): Observable<IInvencionInventor[]> {

    const endpointUrl = `${this.endpointUrl}/${id}/invencion-inventores`;
    const params = new HttpParams().set('id', id.toString());

    return this.http.get<IInvencionInventorResponse[]>(endpointUrl, { params })
      .pipe(
        map(response => {
          return INVENCION_INVENTOR_RESPONSE_CONVERTER.toTargetArray(response ?? []);
        })
      );
  }

  /**
   * Persiste los cambios efectuados a las entidades {@link IInvencionInventor}.
   *
   * @param id Id de la {@link IInvencion}
   * @param invencionInventores Listado de {@link IInvencionInventor} modificados
   * @returns Los {@link IInvencionInventor} asociados a la {@link IInvencion}
   */
  bulkSaveOrUpdateInvencionInventores =
    (idInvencion: number, invencionInventores: IInvencionInventor[]) =>
      this.http.patch<IInvencionInventorResponse[]>(`${this.endpointUrl}/${idInvencion}/invencion-inventores`,
        INVENCION_INVENTOR_REQUEST_CONVERTER.fromTargetArray(invencionInventores)
      ).pipe(
        map((response => INVENCION_INVENTOR_RESPONSE_CONVERTER.toTargetArray(response ?? [])))
      )


  /**
   * Obtiene la lista de solicitudes de proteccion asociadas a una invencion
   * @param id id de la invencion
   * @param options opciones de busqueda
   * @returns lista de solicitudes de proteccion asociadas a la invencion
   */
  public findAllSolicitudesProteccion(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudProteccion>> {
    return this.find<ISolicitudProteccionResponse, ISolicitudProteccion>(
      `${this.endpointUrl}/${id}/solicitudesproteccion`,
      options,
      SOLICITUD_PROTECCION_RESPONSE_CONVERTER
    );
  }

  /**
 * Recupera los Gastos asociados a la Invencion con el id indicado.
 *
 * @param id Id de la Invencion
 */
  findGastos(id: number): Observable<IInvencionGasto[]> {
    const endpointUrl = `${this.endpointUrl}/${id}/gastos`;
    return this.http.get<IInvencionGastoResponse[]>(endpointUrl)
      .pipe(
        map(response => {
          return INVENCION_GASTO_RESPONSE_CONVERTER.toTargetArray(response);
        })
      );
  }
}
