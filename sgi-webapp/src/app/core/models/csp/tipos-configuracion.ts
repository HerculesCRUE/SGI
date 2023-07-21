interface TipoConfiguracion {
  id: number;
  nombre: string;
  descripcion?: string;
  activo: boolean;
}

// tslint:disable: no-empty-interface
export interface IModeloEjecucion extends TipoConfiguracion {
  externo: boolean;
  contrato: boolean;
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

export interface ITipoUnidadGestion extends TipoConfiguracion {
}

export interface ITipoAmbitoGeografico extends TipoConfiguracion {
}

export interface ITipoRegimenConcurrencia extends TipoConfiguracion {
}

export interface ITipoOrigenFuenteFinanciacion extends TipoConfiguracion {
}
