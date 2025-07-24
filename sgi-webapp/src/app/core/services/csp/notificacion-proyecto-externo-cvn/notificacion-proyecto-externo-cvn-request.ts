import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface INotificacionProyectoExternoCVNRequest {
  titulo: I18nFieldValueRequest[];
  autorizacionId: number;
  proyectoId: number;
  ambitoGeografico: string;
  codExterno: string;
  datosEntidadParticipacion: string;
  datosResponsable: string;
  documentoRef: string;
  entidadParticipacionRef: string;
  fechaInicio: string;
  fechaFin: string;
  gradoContribucion: string;
  importeTotal: number;
  nombrePrograma: string;
  porcentajeSubvencion: number;
  proyectoCVNId: string;
  responsableRef: string;
  urlDocumentoAcreditacion: string;
  solicitanteRef: string;
}
