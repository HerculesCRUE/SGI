import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IEstadoSolicitud } from '@core/models/csp/estado-solicitud';
import { ISolicitanteExterno } from '@core/models/csp/solicitante-externo';
import { ISolicitud } from '@core/models/csp/solicitud';
import { ISolicitudDocumento } from '@core/models/csp/solicitud-documento';
import { ISolicitudModalidad } from '@core/models/csp/solicitud-modalidad';
import { ISolicitudRrhh } from '@core/models/csp/solicitud-rrhh';
import { ESTADO_SOLICITUD_RESPONSE_CONVERTER } from '@core/services/csp/estado-solicitud/estado-solicitud-response.converter';
import { ISolicitudDocumentoResponse } from '@core/services/csp/solicitud-documento/solicitud-documento-response';
import { SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER } from '@core/services/csp/solicitud-documento/solicitud-documento-response.converter';
import { SOLICITUD_MODALIDAD_RESPONSE_CONVERTER } from '@core/services/csp/solicitud-modalidad/solicitud-modalidad-response.converter';
import { ISolicitudResponse } from '@core/services/csp/solicitud/solicitud-response';
import { environment } from '@env';
import {
  CreateCtor, FindByIdCtor, mixinCreate, mixinFindById, mixinUpdate, SgiRestBaseService, SgiRestFindOptions, SgiRestListResult, UpdateCtor
} from '@sgi/framework/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { IEstadoSolicitudResponse } from './estado-solicitud/estado-solicitud-response';
import { ISolicitanteExternoResponse } from './solicitante-externo/solicitante-externo-response';
import { SOLICITANTE_EXTERNO_RESPONSE_CONVERTER } from './solicitante-externo/solicitante-externo-response.converter';
import { SolicitudModalidadService } from './solicitud-modalidad.service';
import { ISolicitudModalidadResponse } from './solicitud-modalidad/solicitud-modalidad-response';
import { ISolicitudRrhhResponse } from './solicitud-rrhh/solicitud-rrhh-response';
import { SOLICITUD_RRHH_RESPONSE_CONVERTER } from './solicitud-rrhh/solicitud-rrhh-response.converter';
import { SOLICITUD_RESPONSE_CONVERTER } from './solicitud/solicitud-response.converter';

// tslint:disable-next-line: variable-name
const _SolicitudMixinBase:
  FindByIdCtor<string, ISolicitud, ISolicitudResponse> &
  CreateCtor<ISolicitud, ISolicitud, ISolicitudResponse, ISolicitudResponse> &
  UpdateCtor<string, ISolicitud, ISolicitud, ISolicitudResponse, ISolicitudResponse> &
  typeof SgiRestBaseService = mixinFindById(
    mixinCreate(
      mixinUpdate(
        SgiRestBaseService,
        SOLICITUD_RESPONSE_CONVERTER
      ),
      SOLICITUD_RESPONSE_CONVERTER
    ),
    SOLICITUD_RESPONSE_CONVERTER
  );

@Injectable({
  providedIn: 'root'
})
export class SolicitudPublicService extends _SolicitudMixinBase {
  private static readonly MAPPING = '/solicitudes';
  private static readonly PUBLIC_PREFIX = '/public';

  constructor(
    protected http: HttpClient,
  ) {
    super(
      `${environment.serviceServers.csp}${SolicitudPublicService.PUBLIC_PREFIX}${SolicitudPublicService.MAPPING}`,
      http
    );
  }

  cambiarEstado(solicitudId: string, estadoSolicitud: IEstadoSolicitud): Observable<IEstadoSolicitud> {
    return this.http.patch<IEstadoSolicitudResponse>(`${this.endpointUrl}/${solicitudId}/cambiar-estado`,
      ESTADO_SOLICITUD_RESPONSE_CONVERTER.fromTarget(estadoSolicitud)
    ).pipe(
      map((response => ESTADO_SOLICITUD_RESPONSE_CONVERTER.toTarget(response)))
    );
  }

  findDocumentos(solicitudId: string, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudDocumento>> {
    return this.find<ISolicitudDocumentoResponse, ISolicitudDocumento>(
      `${this.endpointUrl}/${solicitudId}/solicituddocumentos`,
      options,
      SOLICITUD_DOCUMENTO_RESPONSE_CONVERTER
    );
  }

  findEstadoSolicitud(solicitudId: string, options?: SgiRestFindOptions): Observable<SgiRestListResult<IEstadoSolicitud>> {
    return this.find<IEstadoSolicitudResponse, IEstadoSolicitud>(
      `${this.endpointUrl}/${solicitudId}/estadosolicitudes`,
      options,
      ESTADO_SOLICITUD_RESPONSE_CONVERTER
    );
  }

  findSolicitudRrhh(solicitudId: string): Observable<ISolicitudRrhh> {
    return this.http.get<ISolicitudRrhhResponse>(
      `${this.endpointUrl}/${solicitudId}/solicitudrrhh`
    ).pipe(
      map(response => SOLICITUD_RRHH_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  findSolicitanteExterno(id: string): Observable<ISolicitanteExterno> {
    return this.http.get<ISolicitanteExternoResponse>(
      `${this.endpointUrl}/${id}/solicitanteexterno`
    ).pipe(
      map(response => SOLICITANTE_EXTERNO_RESPONSE_CONVERTER.toTarget(response))
    );
  }

  findAllSolicitudModalidades(solicitudPublicId: string, options?: SgiRestFindOptions): Observable<SgiRestListResult<ISolicitudModalidad>> {
    return this.find<ISolicitudModalidadResponse, ISolicitudModalidad>(
      `${this.endpointUrl}/${solicitudPublicId}${SolicitudModalidadService.MAPPING}`,
      options,
      SOLICITUD_MODALIDAD_RESPONSE_CONVERTER
    );
  }

  getPublicId(solicitudPublicId: string, numeroDocumento: string): Observable<string> {
    const url = `${this.endpointUrl}/publicId`;
    let params = new HttpParams();
    params = params.append('uuid', solicitudPublicId);
    params = params.append('numeroDocumento', numeroDocumento);
    return this.http.get<string>(url, { params });
  }

  modificable(solicitudPublicId: string): Observable<boolean> {
    const url = `${this.endpointUrl}/${solicitudPublicId}/modificable`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

  modificableEstadoAndDocumentosByInvestigador(solicitudPublicId: string): Observable<boolean> {
    const url = `${this.endpointUrl}/${solicitudPublicId}/modificableestadoanddocumentos`;
    return this.http.head(url, { observe: 'response' }).pipe(
      map(response => response.status === 200)
    );
  }

}
