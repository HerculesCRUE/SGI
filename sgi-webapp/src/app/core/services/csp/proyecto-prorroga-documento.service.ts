import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IProyectoProrrogaDocumento } from '@core/models/csp/proyecto-prorroga-documento';
import { environment } from '@env';
import { CreateCtor, mixinCreate, mixinUpdate, SgiRestBaseService, UpdateCtor } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { IProyectoProrrogaDocumentoResponse } from './proyecto-prorroga-documento/proyecto-prorroga-documento-response';
import { PROYECTO_PRORROGA_DOCUMENTO_RESPONSE_CONVERTER } from './proyecto-prorroga-documento/proyecto-prorroga-documento-response.converter';

const _ProyectoProrrogaDocumentoServiceMixinBase:
  CreateCtor<IProyectoProrrogaDocumento, IProyectoProrrogaDocumento, IProyectoProrrogaDocumentoResponse, IProyectoProrrogaDocumentoResponse> &
  UpdateCtor<number, IProyectoProrrogaDocumento, IProyectoProrrogaDocumento, IProyectoProrrogaDocumentoResponse, IProyectoProrrogaDocumentoResponse> &
  typeof SgiRestBaseService =
  mixinUpdate(
    mixinCreate(
      SgiRestBaseService,
      PROYECTO_PRORROGA_DOCUMENTO_RESPONSE_CONVERTER,
      PROYECTO_PRORROGA_DOCUMENTO_RESPONSE_CONVERTER
    ),
    PROYECTO_PRORROGA_DOCUMENTO_RESPONSE_CONVERTER,
    PROYECTO_PRORROGA_DOCUMENTO_RESPONSE_CONVERTER
  );


@Injectable({
  providedIn: 'root'
})
export class ProyectoProrrogaDocumentoService extends _ProyectoProrrogaDocumentoServiceMixinBase {
  private static readonly MAPPING = '/prorrogadocumentos';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ProyectoProrrogaDocumentoService.MAPPING}`,
      http
    );
  }

  deleteById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${id}`);
  }

}
