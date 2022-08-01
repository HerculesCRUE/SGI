import { ICategoriaProfesional } from '../categoria-profesional';

export interface IVinculacionBackend {
  id: string;
  categoriaProfesional: ICategoriaProfesional;
  fechaObtencionCategoria: string;
  departamento: {
    id: string;
    nombre: string;
  },
  centro: {
    id: string;
    nombre: string;
  }
}
