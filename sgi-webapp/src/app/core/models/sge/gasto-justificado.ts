export interface IGastoJustificado {
  id: string;
  proyectoId: string;
  justificacionId: string;
  columnas: {
    [name: string]: string | number;
  };
}
