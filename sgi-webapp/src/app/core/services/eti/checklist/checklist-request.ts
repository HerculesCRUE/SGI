export interface IChecklistRequest {
  personaRef: string;
  formlyId: number;
  respuesta: {
    [name: string]: any;
  };
}
