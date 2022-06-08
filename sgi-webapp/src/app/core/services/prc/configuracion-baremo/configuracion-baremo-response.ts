import { TipoBaremo, TipoFuente, TipoNodo, TipoPuntos } from '@core/models/prc/configuracion-baremo';

export interface IConfiguracionBaremoResponse {
  id: number;
  epigrafe: string;
  nombre: string;
  padreId: number;
  prioridad: number;
  tipoBaremo: TipoBaremo;
  tipoFuente: TipoFuente;
  tipoNodo: TipoNodo;
  tipoPuntos: TipoPuntos;
}
