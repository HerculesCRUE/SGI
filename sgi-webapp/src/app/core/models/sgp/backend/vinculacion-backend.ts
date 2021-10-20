import { ICategoriaProfesional } from '../categoria-profesional';

export interface IVinculacionBackend {
  id: string;
  categoriaProfesional: ICategoriaProfesional;
  fechaObtencionCategoria: string;
}
