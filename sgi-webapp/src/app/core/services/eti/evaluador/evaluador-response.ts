import { I18nFieldValueResponse } from '@core/i18n/i18n-field-response';
import { IComiteResponse } from '@core/services/eti/comite/comite-response';
import { CargoComite } from '../../../models/eti/cargo-comite';

export interface IEvaluadorResponse {
  /** Id */
  id: number;
  /** Comité */
  comite: IComiteResponse;
  /** Cargo comité */
  cargoComite: CargoComite;
  /** Resumen */
  resumen: I18nFieldValueResponse[];
  /** Fecha Alta. */
  fechaAlta: string;
  /** Fecha Baja. */
  fechaBaja: string;
  /** Referencia persona */
  personaRef: string;
  /** Activo */
  activo: boolean;
}
