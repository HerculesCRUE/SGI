import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IAutorizacionRequest {
  observaciones: I18nFieldValueRequest[];
  responsableRef: string;
  tituloProyecto: I18nFieldValueRequest[];
  entidadRef: string;
  horasDedicacion: number;
  datosResponsable: string;
  datosEntidad: string;
  datosConvocatoria: I18nFieldValueRequest[];
  convocatoriaId: number;
}
