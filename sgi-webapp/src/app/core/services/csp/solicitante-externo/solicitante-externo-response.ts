
export interface ISolicitanteExternoResponse {
  id: number;
  solicitudId: number;
  nombre: string;
  apellidos: string;
  tipoDocumentoRef: string;
  numeroDocumento: string;
  sexoRef: string;
  fechaNacimiento: string;
  paisNacimientoRef: string;
  telefono: string;
  email: string;
  direccion: string;
  paisContactoRef: string;
  comunidadRef: string;
  provinciaRef: string;
  ciudad: string;
  codigoPostal: string;
}
