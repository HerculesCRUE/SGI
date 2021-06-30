interface TipoConfiguracion {
  id: number;
  nombre: string;
  descripcion: string;
  activo: boolean;
}

// tslint:disable: no-empty-interface
export interface IModeloEjecucion extends TipoConfiguracion {
}

export interface ITipoDocumento extends TipoConfiguracion {
}

export interface ITipoHito extends TipoConfiguracion {
}

export interface ITipoFinalidad extends TipoConfiguracion {
}

export interface ITipoFase extends TipoConfiguracion {
}

export interface ITipoEnlace extends TipoConfiguracion {
}

export interface ITipoFinanciacion extends TipoConfiguracion {
}

export interface IConceptoGasto extends TipoConfiguracion {
}

export interface ITipoUnidadGestion extends TipoConfiguracion {
}
