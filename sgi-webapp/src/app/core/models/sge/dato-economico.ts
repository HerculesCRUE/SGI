import { DateTime } from 'luxon';

export interface IDatoEconomico {
  id: string;
  proyectoId: string;
  partidaPresupuestaria: string;
  codigoEconomico: any;
  anualidad: string;
  tipo: string;
  fechaDevengo: DateTime;
  clasificacionSGE: {
    id: string,
    nombre: string
  };
  columnas: {
    [name: string]: string | number;
  };
}
