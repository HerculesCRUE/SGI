import { TipoPartida } from '@core/enums/tipo-partida';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IPartidaPresupuestariaSge } from '../sge/partida-presupuestaria-sge';

export interface IPartidaPresupuestaria {
  id: number;
  codigo: string;
  partidaSge: IPartidaPresupuestariaSge;
  descripcion: I18nFieldValue[];
  tipoPartida: TipoPartida;
}
