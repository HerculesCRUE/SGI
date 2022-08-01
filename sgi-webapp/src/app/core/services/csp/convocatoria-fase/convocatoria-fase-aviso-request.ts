export interface IConvocatoriaFaseAvisoRequest {
  fechaEnvio: string;
  asunto: string;
  contenido: string;
  destinatarios: {
    nombre: string,
    email: string
  }[],
  incluirIpsSolicitud: boolean;
  incluirIpsProyecto: boolean;
}
