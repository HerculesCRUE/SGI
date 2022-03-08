export interface IConfiguracionCampo {
  id: number;
  codigo: string;
  epigrafe: string;
  fechaReferenciaFin: boolean;
  fechaReferenciaInicio: boolean;
  tipoFormato: TipoFormato;
  validacionAdicional: boolean;
}

export enum TipoFormato {
  ENUMERADO = 'ENUMERADO',
  FECHA = 'FECHA',
  TEXTO = 'TEXTO',
  NUMERO = 'NUMERO',
  BOOLEANO = 'BOOLEANO'
}
