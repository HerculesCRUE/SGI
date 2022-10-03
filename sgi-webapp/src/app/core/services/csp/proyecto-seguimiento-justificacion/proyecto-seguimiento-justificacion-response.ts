export interface IProyectoSeguimientoJustificacionResponse {
  id: number;
  proyectoProyectoSge: {
    id: number;
    proyectoId: number;
    proyectoSgeRef: string;
  };
  importeJustificado: number;
  importeJustificadoCD: number;
  importeJustificadoCI: number;
  importeAceptado: number;
  importeAceptadoCD: number;
  importeAceptadoCI: number;
  importeRechazado: number;
  importeRechazadoCD: number;
  importeRechazadoCI: number;
  importeAlegado: number;
  importeAlegadoCD: number;
  importeAlegadoCI: number;
  importeReintegrar: number;
  importeReintegrarCD: number;
  importeReintegrarCI: number;
  importeReintegrado: number;
  importeReintegradoCD: number;
  importeReintegradoCI: number;
  interesesReintegrados: number;
  interesesReintegrar: number;
  fechaReintegro: string;
  justificanteReintegro: string;
  importeNoEjecutado: number;
  importeNoEjecutadoCD: number;
  importeNoEjecutadoCI: number;
}
