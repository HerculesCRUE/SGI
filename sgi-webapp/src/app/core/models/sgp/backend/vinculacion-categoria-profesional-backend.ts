import { ICategoriaProfesional } from '../categoria-profesional';

export interface IVinculacionCategoriaProfesionalBackend {
  id: string;
  categoriaProfesional: ICategoriaProfesional;
  fechaObtencion: string;
}
