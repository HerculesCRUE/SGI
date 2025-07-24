import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { environment } from '@env';
import { CreateCtor, SgiRestBaseService, UpdateCtor, mixinCreate, mixinUpdate } from '@sgi/framework/http';
import { IProyectoPeriodoSeguimientoDocumentoResponse } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-documento-response';
import { PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_RESPONSE_CONVERTER } from './proyecto-periodo-seguimiento/proyecto-periodo-seguimiento-documento-response.converter';
import { Observable } from 'rxjs';

const _ProyectoPeriodoSeguimientoDocumentoServiceMixinBase:
  CreateCtor<IProyectoPeriodoSeguimientoDocumento, IProyectoPeriodoSeguimientoDocumento, IProyectoPeriodoSeguimientoDocumentoResponse, IProyectoPeriodoSeguimientoDocumentoResponse> &
  UpdateCtor<number, IProyectoPeriodoSeguimientoDocumento, IProyectoPeriodoSeguimientoDocumento, IProyectoPeriodoSeguimientoDocumentoResponse, IProyectoPeriodoSeguimientoDocumentoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_RESPONSE_CONVERTER,
      PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_RESPONSE_CONVERTER
    ),
    PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_RESPONSE_CONVERTER,
    PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoPeriodoSeguimientoDocumentoService extends _ProyectoPeriodoSeguimientoDocumentoServiceMixinBase {
  private static readonly MAPPING = '/proyectoperiodoseguimientodocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoPeriodoSeguimientoDocumentoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
