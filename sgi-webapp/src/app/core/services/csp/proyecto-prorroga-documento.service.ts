import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_PRORROGA_DOCUMENTO_CONVERTER } from '@core/converters/csp/proyecto-prorroga-documento.converter';
import { IProyectoProrrogaDocumentoBackend } from '@core/models/csp/backend/proyecto-prorroga-documento-backend';
import { IProyectoProrrogaDocumento } from '@core/models/csp/proyecto-prorroga-documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoProrrogaDocumentoService
  extends SgiMutableRestService<number, IProyectoProrrogaDocumentoBackend, IProyectoProrrogaDocumento> {
  private static readonly MAPPING = '/prorrogadocumentos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoProrrogaDocumentoService.name,
      `${environment.serviceServers.csp}${ProyectoProrrogaDocumentoService.MAPPING}`,
      http,
      PROYECTO_PRORROGA_DOCUMENTO_CONVERTER
    );
  }

}
