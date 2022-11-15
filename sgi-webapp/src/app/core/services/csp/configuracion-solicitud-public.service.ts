import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DOCUMENTO_REQUERIDO_SOLICITUD_CONVERTER } from '@core/converters/csp/documento-requerido-solicitud.converter';
import { IDocumentoRequeridoSolicitudBackend } from '@core/models/csp/backend/documento-requerido-solicitud-backend';
import { IDocumentoRequeridoSolicitud } from '@core/models/csp/documento-requerido-solicitud';
import { ITipoDocumento } from '@core/models/csp/tipos-configuracion';
import { environment } from '@env';
import { SgiRestBaseService, SgiRestListResult } from '@sgi/framework/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConfiguracionSolicitudPublicService extends SgiRestBaseService {
  private static readonly MAPPING = '/convocatoria-configuracionsolicitudes';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.csp}${ConfiguracionSolicitudPublicService.PUBLIC_PREFIX}${ConfiguracionSolicitudPublicService.MAPPING}`,
      http
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

}
