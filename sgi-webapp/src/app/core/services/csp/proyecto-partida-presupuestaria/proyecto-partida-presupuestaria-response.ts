import { TipoPartida } from "@core/enums/tipo-partida";
import { I18nFieldValueResponse } from "@core/i18n/i18n-field-response";

export interface IProyectoPartidaPresupuestariaResponse {
  id: number;
  proyectoId: number;
  convocatoriaPartidaId: number;
  codigo: string;
  partidaRef: string;
  descripcion: I18nFieldValueResponse[];
  tipoPartida: TipoPartida;
}
