import { TipoPartida } from "@core/enums/tipo-partida";
import { I18nFieldValueRequest } from "@core/i18n/i18n-field-request";

export interface IConvocatoriaPartidaPresupuestariaRequest {
  convocatoriaId: number;
  codigo: string;
  partidaRef: string;
  descripcion: I18nFieldValueRequest[];
  tipoPartida: TipoPartida;
}
