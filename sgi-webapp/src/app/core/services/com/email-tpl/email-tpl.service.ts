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

  processProyectoHitoTemplate(
    tituloProyecto: string,
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
    params.push({ name: 'CSP_PROYECTO_TITULO', value: tituloProyecto });

    return this.processTemplate('CSP_PROYECTO_HITO', params);
  }

  /**
   * 
   * @param tituloConvocatoria 
   * @param fechaInicioFase 
   * @param fechaFinFase 
   * @param tipoFase 
   * @param observaciones 
   * @returns Observable of IProcessedEmailTpl
   */
  processConvocatoriaFaseTemplate(
    tituloConvocatoria: string,
    fechaInicioFase: DateTime,
    fechaFinFase: DateTime,
    tipoFase: string,
    observaciones: string
  ): Observable<IProcessedEmailTpl> {
    const params: IEmailParam[] = [];
    params.push({ name: 'CSP_CONV_FASE_FECHA_INICIO', value: LuxonUtils.toBackend(fechaInicioFase) });
    params.push({ name: 'CSP_CONV_FASE_FECHA_FIN', value: LuxonUtils.toBackend(fechaFinFase) });
    params.push({ name: 'CSP_CONV_TIPO_FASE', value: tipoFase });
    params.push({ name: 'CSP_CONV_FASE_OBSERVACIONES', value: observaciones });
    params.push({ name: 'CSP_CONV_FASE_TITULO', value: tituloConvocatoria });

    return this.processTemplate('CSP_COM_CONVOCATORIA_FASE', params);
  }

  /**
   * 
   * @param tituloProyecto
   * @param tituloConvocatoria 
   * @param fechaInicioFase 
   * @param fechaFinFase 
   * @param tipoFase 
   * @param observaciones 
   * @returns Observable of IProcessedEmailTpl
   */
  processProyectoFaseTemplate(
    tituloProyecto: string,
    tituloConvocatoria: string,
    fechaInicioFase: DateTime,
    fechaFinFase: DateTime,
    tipoFase: string,
    observaciones: string
  ): Observable<IProcessedEmailTpl> {
    const params: IEmailParam[] = [];
    params.push({ name: 'CSP_PRO_FASE_FECHA_INICIO', value: LuxonUtils.toBackend(fechaInicioFase) });
    params.push({ name: 'CSP_PRO_FASE_FECHA_FIN', value: LuxonUtils.toBackend(fechaFinFase) });
    params.push({ name: 'CSP_PRO_TIPO_FASE', value: tipoFase });
    params.push({ name: 'CSP_PRO_FASE_OBSERVACIONES', value: observaciones });
    params.push({ name: 'CSP_PRO_FASE_TITULO_CONVOCATORIA', value: tituloConvocatoria });
    params.push({ name: 'CSP_PRO_FASE_TITULO_PROYECTO', value: tituloProyecto });

    return this.processTemplate('CSP_COM_PROYECTO_FASE', params);
  }
}
