import { I18nFieldValue } from '@core/i18n/i18n-field';
import { DateTime } from 'luxon';
import { IEstadoValidacionIP } from './estado-validacion-ip';
import { IProyectoProrroga } from './proyecto-prorroga';
import { ITipoFacturacion } from './tipo-facturacion';

export interface IProyectoFacturacion {
  id: number;
  comentario: I18nFieldValue[];
  estadoValidacionIP: IEstadoValidacionIP;
  fechaConformidad: DateTime;
  fechaEmision: DateTime;
  importeBase: number;
  numeroFacturaSge: string;
  numeroPrevision: number;
  porcentajeIVA: number;
  proyectoId: number;
  proyectoProrroga: IProyectoProrroga;
  proyectoSgeRef: string
  tipoFacturacion: ITipoFacturacion;
}
