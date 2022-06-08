import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IBaremo } from '@core/models/prc/baremo';
import { IConvocatoriaBaremacion } from '@core/models/prc/convocatoria-baremacion';
import { IModulador, Tipo } from '@core/models/prc/modulador';
import { IRango, TipoRango } from '@core/models/prc/rango';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor, mixinCreate,
  mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BAREMO_REQUEST_CONVERTER } from '../baremo/baremo-request.converter';
import { IBaremoResponse } from '../baremo/baremo-response';
import { BAREMO_RESPONSE_CONVERTER } from '../baremo/baremo-response.converter';
import { IModuladorResponse } from '../modulador/modulador-response';
import { MODULADOR_RESPONSE_CONVERTER } from '../modulador/modulador-response.converter';
import { RANGO_REQUEST_CONVERTER } from '../rango/rango-request.converter';
import { IRangoResponse } from '../rango/rango-response';
import { RANGO_RESPONSE_CONVERTER } from '../rango/rango-response.converter';
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

  private static readonly MAPPING = '/convocatoriasbaremacion';

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

  /**
   * Obtiene la lista de años en los que hay ConvocatoriaBaremacion
   */
  findAniosWithConvocatoriasBaremacion(): Observable<number[]> {
    return this.get<number[]>(`${this.endpointUrl}/anios`);
  }

  clone(convocatoriaId: number): Observable<number> {
    const url = `${this.endpointUrl}/${convocatoriaId}/clone`;
    return this.http.post<number>(url, {});
  }

  /**
   * Busca todos los Baremo asociados a una ConvocatoriaBaremacion
   *
   * @param id id de la ConvocatoriaBaremacion
   * @param options opciones de búsqueda.
   */
  findBaremos(id: number, options?: SgiRestFindOptions): Observable<SgiRestListResult<IBaremo>> {
    return this.find<IBaremoResponse, IBaremo>(
      `${this.endpointUrl}/${id}/baremos`,
      options,
      BAREMO_RESPONSE_CONVERTER
    );
  }

  /**
   * Actualiza los Baremo asociadas a la ConvocatoriaBaremacion con el id indicado
   * @param id id de la ConvocatoriaBaremacion
   * @param baremos los Baremo a actualizar
   */
  updateBaremos(id: number, baremos: IBaremo[]): Observable<IBaremo[]> {
    return this.http.patch<IBaremoResponse[]>(
      `${this.endpointUrl}/${id}/baremos`,
      BAREMO_REQUEST_CONVERTER.fromTargetArray(baremos)
    ).pipe(
      map((response => BAREMO_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Rangos del tipo TipoRango de la ConvocatoriaBaremacion
   *
   * @param id id de la ConvocatoriaBaremacion.
   * @param tipoRango TipoRango.
   */
  findRangosTipo(id: number, tipoRango: TipoRango): Observable<SgiRestListResult<IRango>> {
    return this.find<IRangoResponse, IRango>(
      `${this.endpointUrl}/${id}/rangos/${tipoRango}`,
      null,
      RANGO_RESPONSE_CONVERTER
    );
  }

  /**
   * Actualiza los Rangos asociados a la ConvocatoriaBaremacion con el id indicado.
   *
   * @param id id de la ConvocatoriaBaremacion.
   * @param rangos Rangos a actualizar
   * @param tipoRango TipoRango.
   */
  updateRangos(id: number, rangos: IRango[], tipoRango: TipoRango): Observable<IRango[]> {
    return this.http.patch<IRangoResponse[]>(`${this.endpointUrl}/${id}/rangos/${tipoRango}`,
      RANGO_REQUEST_CONVERTER.fromTargetArray(rangos)
    ).pipe(
      map((response => RANGO_RESPONSE_CONVERTER.toTargetArray(response)))
    );
  }

  /**
   * Moduladores del tipo TipoRango de la ConvocatoriaBaremacion
   *
   * @param id id de la ConvocatoriaBaremacion.
   * @param tipo TipoModulador.
   */
  findModuladores(id: number, tipo: Tipo): Observable<SgiRestListResult<IModulador>> {
    return this.find<IModuladorResponse, IModulador>(
      `${this.endpointUrl}/${id}/moduladores/${tipo}`,
      null,
      MODULADOR_RESPONSE_CONVERTER
    );
  }

}
