import { IConceptoGastoCodigoEc } from './concepto-gasto-codigo-ec';
import { IProyectoConceptoGasto } from './proyecto-concepto-gasto';

export interface IProyectoConceptoGastoCodigoEc extends IConceptoGastoCodigoEc {
  proyectoConceptoGasto: IProyectoConceptoGasto;
  convocatoriaConceptoGastoCodigoEcId: number;
}
