import { IConfiguracionCampo } from './configuracion-campo';
import { IProduccionCientifica } from './produccion-cientifica';

export interface ICampoProduccionCientifica {
  id: number;
  produccionCientifica: IProduccionCientifica;
  codigo: string;
}

export interface ICampoProduccionCientificaWithConfiguracion {
  campoProduccionCientifica: ICampoProduccionCientifica;
  configuracionCampo: IConfiguracionCampo;
}
