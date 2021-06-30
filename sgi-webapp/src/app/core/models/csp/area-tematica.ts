export interface IAreaTematica {
  id: number;
  nombre: string;
  descripcion: string;
  padre: IAreaTematica;
  activo: boolean;
}

