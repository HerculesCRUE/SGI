import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { I18N_FIELD_REQUEST_CONVERTER, I18N_FIELD_RESPONSE_CONVERTER } from '@core/i18n/i18n-field.converter';
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
    tituloConvocatoria: I18nFieldValue[],
    fechaInicio: DateTime,
    nombreHito: I18nFieldValue[],
    observaciones: I18nFieldValue[]
  ): Observable<IProcessedEmailTpl> {
    const params: IEmailParam[] = [];
    params.push({ name: 'CSP_HITO_FECHA', value: LuxonUtils.toBackend(fechaInicio) });
    params.push({ name: 'CSP_HITO_TIPO', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(nombreHito)) });
    params.push({ name: 'CSP_HITO_OBSERVACIONES', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(observaciones)) });
    params.push({ name: 'CSP_CONVOCATORIA_TITULO', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(tituloConvocatoria)) });

    return this.processTemplate('CSP_CONVOCATORIA_HITO_EMAIL', params);
  }

  processSolicitudHitoTemplate(
    tituloSolicitud: I18nFieldValue[],
    tituloConvocatoria: I18nFieldValue[],
    fechaInicio: DateTime,
    nombreHito: I18nFieldValue[],
    observaciones: I18nFieldValue[]
  ): Observable<IProcessedEmailTpl> {
    const params: IEmailParam[] = [];
    params.push({ name: 'CSP_HITO_FECHA', value: LuxonUtils.toBackend(fechaInicio) });
    params.push({ name: 'CSP_HITO_TIPO', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(nombreHito)) });
    params.push({ name: 'CSP_HITO_OBSERVACIONES', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(observaciones)) });
    params.push({ name: 'CSP_CONVOCATORIA_TITULO', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(tituloConvocatoria)) });
    params.push({ name: 'CSP_SOLICITUD_TITULO', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(tituloSolicitud)) });

    return this.processTemplate('CSP_SOLICITUD_HITO', params);
  }

  processProyectoHitoTemplate(
    tituloProyecto: I18nFieldValue[],
    tituloConvocatoria: I18nFieldValue[],
    fechaInicio: DateTime,
    nombreHito: I18nFieldValue[],
    observaciones: I18nFieldValue[]
  ): Observable<IProcessedEmailTpl> {
    const params: IEmailParam[] = [];
    params.push({ name: 'CSP_HITO_FECHA', value: LuxonUtils.toBackend(fechaInicio) });
    params.push({ name: 'CSP_HITO_TIPO', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(nombreHito)) });
    params.push({ name: 'CSP_HITO_OBSERVACIONES', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(observaciones)) });
    params.push({ name: 'CSP_CONVOCATORIA_TITULO', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(tituloConvocatoria)) });
    params.push({ name: 'CSP_PROYECTO_TITULO', value: JSON.stringify(I18N_FIELD_REQUEST_CONVERTER.fromTargetArray(tituloProyecto)) });

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
    tituloConvocatoria: I18nFieldValue[],
    fechaInicioFase: DateTime,
    fechaFinFase: DateTime,
    tipoFase: I18nFieldValue[],
    observaciones: I18nFieldValue[]
  ): Observable<IProcessedEmailTpl> {
    const params: IEmailParam[] = [];
    params.push({ name: 'CSP_CONV_FASE_FECHA_INICIO', value: LuxonUtils.toBackend(fechaInicioFase) });
    params.push({ name: 'CSP_CONV_FASE_FECHA_FIN', value: LuxonUtils.toBackend(fechaFinFase) });
    params.push({ name: 'CSP_CONV_TIPO_FASE', value: JSON.stringify(I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(tipoFase)) });
    params.push({ name: 'CSP_CONV_FASE_OBSERVACIONES', value: JSON.stringify(I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(observaciones)) });
    params.push({ name: 'CSP_CONV_FASE_TITULO', value: JSON.stringify(I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(tituloConvocatoria)) });

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
    tituloProyecto: I18nFieldValue[],
    tituloConvocatoria: I18nFieldValue[],
    fechaInicioFase: DateTime,
    fechaFinFase: DateTime,
    tipoFase: I18nFieldValue[],
    observaciones: I18nFieldValue[]
  ): Observable<IProcessedEmailTpl> {
    const params: IEmailParam[] = [];
    params.push({ name: 'CSP_PRO_FASE_FECHA_INICIO', value: LuxonUtils.toBackend(fechaInicioFase) });
    params.push({ name: 'CSP_PRO_FASE_FECHA_FIN', value: LuxonUtils.toBackend(fechaFinFase) });
    params.push({ name: 'CSP_PRO_TIPO_FASE', value: JSON.stringify(I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(tipoFase)) });
    params.push({ name: 'CSP_PRO_FASE_OBSERVACIONES', value: JSON.stringify(I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(observaciones)) });
    params.push({ name: 'CSP_PRO_FASE_TITULO_CONVOCATORIA', value: JSON.stringify(I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(tituloConvocatoria)) });
    params.push({ name: 'CSP_PRO_FASE_TITULO_PROYECTO', value: JSON.stringify(I18N_FIELD_RESPONSE_CONVERTER.fromTargetArray(tituloProyecto)) });

    return this.processTemplate('CSP_COM_PROYECTO_FASE', params);
  }
}
