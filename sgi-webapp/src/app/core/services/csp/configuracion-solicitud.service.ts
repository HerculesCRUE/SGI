import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CONFIGURACION_SOLICITUD_CONVERTER } from '@core/converters/csp/configuracion-solicitud.converter';
import { DOCUMENTO_REQUERIDO_SOLICITUD_CONVERTER } from '@core/converters/csp/documento-requerido-solicitud.converter';
import { IConfiguracionSolicitudBackend } from '@core/models/csp/backend/configuracion-solicitud-backend';
import { IDocumentoRequeridoSolicitudBackend } from '@core/models/csp/backend/documento-requerido-solicitud-backend';
import { IConfiguracionSolicitud } from '@core/models/csp/configuracion-solicitud';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiMutableRestService, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionSolicitudService extends SgiMutableRestService<number, IConfiguracionSolicitudBackend, IConfiguracionSolicitud>{
  private static readonly MAPPING = '/convocatoria-configuracionsolicitudes';

  constructor(protected http: HttpClient) {
    super(
      ConfiguracionSolicitudService.name,
      `${environment.serviceServers.csp}${ConfiguracionSolicitudService.MAPPING}`,
      http,
      CONFIGURACION_SOLICITUD_CONVERTER
    );
  }

  /**
   * Recupera los documentos requeridos de solicitudes
   * @param id convocatoria
   */
  findAllConvocatoriaDocumentoRequeridoSolicitud(id: number): Observable<SgiRestListResult<IDocumentoRequeridoSolicitud>> {
    return this.find<IDocumentoRequeridoSolicitudBackend, IDocumentoRequeridoSolicitud>(
      `${this.endpointUrl}/${id}/documentorequiridosolicitudes`,
      undefined,
      DOCUMENTO_REQUERIDO_SOLICITUD_CONVERTER
    );
  }

  /**
   * Recupera tipos de documento asociados a la fase de presentación de solicitudes de la convocatoria.
   *
   * @param id Id de la convocatoria
   */
  findAllTipoDocumentosFasePresentacion(id: number): Observable<SgiRestListResult<ITipoDocumento>> {
    return this.find<ITipoDocumento, ITipoDocumento>(`${this.endpointUrl}/${id}/tipodocumentofasepresentaciones`);
  }

  /**
   * Recupera la configuracion de solicitud asociada a la convocatoria.
   *
   * @param id Id de la convocatoria
   */
  findByConvocatoriaId(id: number): Observable<IConfiguracionSolicitud> {
    return this.http.get<IConfiguracionSolicitudBackend>(`${this.endpointUrl}/${id}`).pipe(
      map(response => CONFIGURACION_SOLICITUD_CONVERTER.toTarget(response))
    );

  }

}
