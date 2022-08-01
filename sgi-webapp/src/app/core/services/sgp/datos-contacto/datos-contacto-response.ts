export interface IDatosContactoResponse {
  paisContacto: {
    id: string;
    nombre: string;
  };
  comAutonomaContacto: {
    id: string;
    nombre: string;
    paisId: string;
  };
  provinciaContacto: {
    id: string;
    nombre: string;
    comunidadAutonomaId: string;
  };
  ciudadContacto: string;
  codigoPostalContacto: string;
  emails: {
    email: string;
    principal: boolean;
  }[];
  telefonos: string[];
  moviles: string[];
  direccionContacto: string;
}
