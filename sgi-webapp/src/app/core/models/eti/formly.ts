import { IFormlyDefinicion } from './formly-definicion';

export interface IFormly {
  id: number;
  nombre: string;
  version: number;
  definicion: IFormlyDefinicion[];
}
