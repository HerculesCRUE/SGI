import { I18nFieldValue } from '@core/i18n/i18n-field';
import { IGrupo } from './grupo';

export interface IGrupoEquipoInstrumental {
  id: number;
  grupo: IGrupo;
  nombre: I18nFieldValue[];
  numRegistro: string;
  descripcion: I18nFieldValue[];
}
