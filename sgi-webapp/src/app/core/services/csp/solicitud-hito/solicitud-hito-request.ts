export interface ISolicitudHitoRequest {
  /** Fecha inicio  */
  fecha: string;
  /** Tipo de hito */
  tipoHitoId: number;
  /** Comentario */
  comentario: string;
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
