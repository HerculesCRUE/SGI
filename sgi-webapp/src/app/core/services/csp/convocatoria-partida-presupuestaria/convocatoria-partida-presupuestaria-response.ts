import { TipoPartida } from "@core/enums/tipo-partida";
import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IConvocatoriaPartidaPresupuestariaResponse {
  id: number;
  convocatoriaId: number;
  codigo: string;
  partidaRef: string;
  descripcion: I18nFieldValueResponse[];
  tipoPartida: TipoPartida;
}
