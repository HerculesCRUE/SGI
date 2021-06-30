export interface IInvencionResponse {
  id: number;
  titulo: string;
  fechaComunicacion: string;
  descripcion: string;
  comentarios: string;
  tipoProteccion: {
    id: number;
    nombre: string;
  }
  activo: boolean;
}