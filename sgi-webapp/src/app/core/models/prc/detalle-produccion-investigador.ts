import { ITipoProduccionInvestigador } from './tipo-produccion-investigador';

export interface IDetalleProduccionInvestigador {
  investigador: string;
  anio: number;
  tipos: ITipoProduccionInvestigador[];
}
