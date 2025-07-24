import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IPersona } from '../sgp/persona';
import { CargoComite } from './cargo-comite';
import { IComite } from './comite';

export interface IEvaluador {
  /** Id */
  id: number;
  /** Comité */
  comite: IComite;
  /** Cargo comité */
  cargoComite: CargoComite;
  /** Resumen */
  resumen: I18nFieldValue[];
  /** Fecha Alta. */
  fechaAlta: DateTime;
  /** Fecha Baja. */
  fechaBaja: DateTime;
  /** Persona */
  persona: IPersona;
  /** Activo */
  activo: boolean;
}
