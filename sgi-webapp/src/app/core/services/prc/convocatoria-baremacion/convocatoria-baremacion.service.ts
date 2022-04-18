import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor, mixinCreate,
  mixinFindById, mixinUpdate, SgiRestBaseService,
  SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IConvocatoriaBaremacionRequest } from './convocatoria-baremacion-request';
import { CONVOCATORIA_BAREMACION_REQUEST_CONVERTER } from './convocatoria-baremacion-request.converter';
import { IConvocatoriaBaremacionResponse } from './convocatoria-baremacion-response';
import { CONVOCATORIA_BAREMACION_RESPONSE_CONVERTER } from './convocatoria-baremacion-response.converter';

// tslint:disable-next-line: variable-name
const _ConvocatoriaBaremacionMixinBase:
  FindByIdCtor<number, IConvocatoriaBaremacion, IConvocatoriaBaremacionResponse> &
  CreateCtor<IConvocatoriaBaremacion, IConvocatoriaBaremacion, IConvocatoriaBaremacionRequest, IConvocatoriaBaremacionResponse> &
  UpdateCtor<number, IConvocatoriaBaremacion, IConvocatoriaBaremacion, IConvocatoriaBaremacionRequest, IConvocatoriaBaremacionResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinCreate(
      mixinUpdate(
        SgiRestBaseService,
        CONVOCATORIA_BAREMACION_REQUEST_CONVERTER,
        CONVOCATORIA_BAREMACION_RESPONSE_CONVERTER
      ),
      CONVOCATORIA_BAREMACION_REQUEST_CONVERTER,
      CONVOCATORIA_BAREMACION_RESPONSE_CONVERTER
    ),
    CONVOCATORIA_BAREMACION_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ConvocatoriaBaremacionService extends _ConvocatoriaBaremacionMixinBase {

  private static readonly MAPPING = '/convocatoriabaremacion';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.prc}${ConvocatoriaBaremacionService.MAPPING}`,
      http,
    );
  }

  /**
   * Muestra activos y no activos
   *
   * @param options opciones de búsqueda.
   */
  findTodos(options?: SgiRestFindOptions): Observable<SgiRestListResult<IConvocatoriaBaremacion>> {
    return this.find<IConvocatoriaBaremacionResponse, IConvocatoriaBaremacion>(
      `${this.endpointUrl}/todos`,
      options,
      CONVOCATORIA_BAREMACION_RESPONSE_CONVERTER
    );
  }

  /**
   * Activar la ConvocatoriaBaremacion
   * @param id id de la ConvocatoriaBaremacion.
   */
  activar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/activar`, { id });
  }

  /**
   * Desactivar la ConvocatoriaBaremacion
   * @param id id de la ConvocatoriaBaremacion.
   */
  desactivar(id: number): Observable<void> {
    return this.http.patch<void>(`${this.endpointUrl}/${id}/desactivar`, { id });
  }
}
