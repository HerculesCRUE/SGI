import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER } from '@core/converters/csp/proyecto-periodo-seguimiento-documento.converter';
import { IProyectoPeriodoSeguimientoDocumentoBackend } from '@core/models/csp/backend/proyecto-periodo-seguimiento-documento-backend';
import { IProyectoPeriodoSeguimientoDocumento } from '@core/models/csp/proyecto-periodo-seguimiento-documento';
import { environment } from '@env';
import { SgiMutableRestService } from '@sgi/framework/http';

@Injectable({
  providedIn: 'root'
})
export class ProyectoPeriodoSeguimientoDocumentoService
  extends SgiMutableRestService<number, IProyectoPeriodoSeguimientoDocumentoBackend, IProyectoPeriodoSeguimientoDocumento> {
  private static readonly MAPPING = '/proyectoperiodoseguimientodocumentos';

  constructor(protected http: HttpClient) {
    super(
      ProyectoPeriodoSeguimientoDocumentoService.name,
      `${environment.serviceServers.csp}${ProyectoPeriodoSeguimientoDocumentoService.MAPPING}`,
      http,
      PROYECTO_PERIODO_SEGUIMIENTO_DOCUMENTO_CONVERTER
    );
  }

}
