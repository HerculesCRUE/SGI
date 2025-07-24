import { marker } from '@biesbjerg/ngx-translate-extract-marker';

export enum CARGO_COMITE {
  PRESIDENTE = 1,
  VOCAL = 2,
  SECRETARIO = 3
}

export const CARGO_COMITE_MAP: Map<CARGO_COMITE, string> = new Map([
  [CARGO_COMITE.PRESIDENTE, marker(`eti.cargo-comite.PRESIDENTE`)],
  [CARGO_COMITE.VOCAL, marker(`eti.cargo-comite.VOCAL`)],
  [CARGO_COMITE.SECRETARIO, marker(`eti.cargo-comite.SECRETARIO`)]
]);

export class CargoComite {
  /** Id. */
  id: number;

  /** Nombre. */
  nombre: string;

  /** Activo */
  activo: boolean;

  constructor() {
    this.id = null;
    this.nombre = '';
    this.activo = true;
  }
}
