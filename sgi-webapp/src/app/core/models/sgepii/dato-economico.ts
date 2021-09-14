export interface IDatoEconomico {
  id: string;
  tipo: 'Gasto' | 'Ingreso';
  columnas: {
    [name: string]: string | number;
  };
}
