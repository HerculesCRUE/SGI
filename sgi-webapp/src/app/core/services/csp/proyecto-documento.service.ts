import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoDocumento } from '@core/models/csp/proyecto-documento';
import { IProyectoDocumentoResponse } from '@core/services/csp/proyecto-documento/proyecto-documento-response';
import { PROYECTO_DOCUMENTO_RESPONSE_CONVERTER } from '@core/services/csp/proyecto-documento/proyecto-documento-response.converter';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';

const _ProyectoDocumentoServiceMixinBase:
  CreateCtor<IProyectoDocumento, IProyectoDocumento, IProyectoDocumentoResponse, IProyectoDocumentoResponse> &
  UpdateCtor<number, IProyectoDocumento, IProyectoDocumento, IProyectoDocumentoResponse, IProyectoDocumentoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      PROYECTO_DOCUMENTO_RESPONSE_CONVERTER,
      PROYECTO_DOCUMENTO_RESPONSE_CONVERTER
    ),
    PROYECTO_DOCUMENTO_RESPONSE_CONVERTER,
    PROYECTO_DOCUMENTO_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class ProyectoDocumentoService extends _ProyectoDocumentoServiceMixinBase {
  private static readonly MAPPING = '/proyectodocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoDocumentoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
