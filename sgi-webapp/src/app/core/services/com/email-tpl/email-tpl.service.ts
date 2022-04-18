import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LuxonUtils } from '@core/utils/luxon-utils';
import { environment } from '@env';
import { SgiRestBaseService } from '@sgi/framework/http';
import { DateTime } from 'luxon';
import { Observable } from 'rxjs';
import { IEmailParam } from './email-param';
import { IProcessedEmailTpl } from './processed-email-tpl-response';


@Injectable({
  providedIn: 'root'
})
export class EmailTplService extends SgiRestBaseService {
  private static readonly MAPPING = '/emailtpls';

  constructor(protected http: HttpClient) {
    super(
      `${environment.serviceServers.com}${EmailTplService.MAPPING}`,
      http
    );
  }

  processTemplate(name: string, params: IEmailParam[]): Observable<IProcessedEmailTpl> {
    return this.http.post<IProcessedEmailTpl>(`${this.endpointUrl}/${name}/process`, params);
  }

  processConvocatoriaHitoTemplate(
    tituloConvocatoria: string,
    fechaInicio: DateTime,
    nombreHito: string,
    observaciones: string
  ): Observable<IProcessedEmailTpl> {
    const params: IEmailParam[] = [];
    params.push({ name: 'CSP_HITO_FECHA', value: LuxonUtils.toBackend(fechaInicio) });
    params.push({ name: 'CSP_HITO_TIPO', value: nombreHito });
    params.push({ name: 'CSP_HITO_OBSERVACIONES', value: observaciones });
    params.push({ name: 'CSP_CONVOCATORIA_TITULO', value: tituloConvocatoria });

    return this.processTemplate('CSP_CONVOCATORIA_HITO_EMAIL', params);
  }

  processSolicitudHitoTemplate(
    tituloSolicitud: string,
    tituloConvocatoria: string,
    fechaInicio: DateTime,
    nombreHito: string,
    observaciones: string
  ): Observable<IProcessedEmailTpl> {
    const params: IEmailParam[] = [];
    params.push({ name: 'CSP_HITO_FECHA', value: LuxonUtils.toBackend(fechaInicio) });
    params.push({ name: 'CSP_HITO_TIPO', value: nombreHito });
    params.push({ name: 'CSP_HITO_OBSERVACIONES', value: observaciones });
    params.push({ name: 'CSP_CONVOCATORIA_TITULO', value: tituloConvocatoria });
    params.push({ name: 'CSP_SOLICITUD_TITULO', value: tituloSolicitud });

    return this.processTemplate('CSP_SOLICITUD_HITO', params);
  }
}
