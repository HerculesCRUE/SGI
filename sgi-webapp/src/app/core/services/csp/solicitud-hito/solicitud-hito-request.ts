import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface ISolicitudHitoRequest {
  /** Fecha inicio  */
  fecha: string;
  /** Tipo de hito */
  tipoHitoId: number;
  /** Comentario */
  comentario: I18nFieldValueRequest[];
  /** Id de Solicitud */
  solicitudId: number;
  aviso: {
    fechaEnvio: string;
    asunto: string;
    contenido: string;
    destinatarios: {
      nombre: string,
      email: string
    }[],
    incluirIpsSolicitud: boolean;
  };
}
