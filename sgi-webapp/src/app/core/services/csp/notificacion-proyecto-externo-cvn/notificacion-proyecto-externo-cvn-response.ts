export interface INotificacionProyectoExternoCVNResponse {
  id: number;
  titulo: string;
  autorizacionId: number;
  proyectoId: number;
  ambitoGeografico: string;
  codExterno: string;
  datosEntidadParticipacion: string;
  datosResponsable: string;
  documentoRef: string;
  entidadParticipacionRef: string;
  fechaInicio: string;
  fechaFin: string;
  gradoContribucion: string;
  importeTotal: number;
  nombrePrograma: string;
  porcentajeSubvencion: number;
  proyectoCVNId: string;
  responsableRef: string;
  urlDocumentoAcreditacion: string;
  solicitanteRef: string;
}
