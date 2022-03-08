import { ICampoProduccionCientifica } from './campo-produccion-cientifica';

export interface IValorCampo {
  id: number;
  valor: string;
  orden: number;
  campoProduccionCientifica: ICampoProduccionCientifica;
}
