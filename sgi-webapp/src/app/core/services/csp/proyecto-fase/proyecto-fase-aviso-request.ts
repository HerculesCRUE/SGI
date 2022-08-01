export interface IProyectoFaseAvisoRequest {
  fechaEnvio: string;
  asunto: string;
  contenido: string;
  destinatarios: {
    nombre: string,
    email: string
  }[],
  incluirIpsProyecto: boolean;
}
