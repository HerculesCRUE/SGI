import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ActionService } from '@core/services/action-service';
import { GrupoEquipoService } from '@core/services/csp/grupo-equipo/grupo-equipo.service';
import { GrupoService } from '@core/services/csp/grupo/grupo.service';
import { RolProyectoService } from '@core/services/csp/rol-proyecto.service';
import { PalabraClaveService } from '@core/services/sgo/palabra-clave.service';
import { VinculacionService } from '@core/services/sgp/vinculacion.service';
import { NGXLogger } from 'ngx-logger';
import { GRUPO_ROUTE_PARAMS } from './autorizacion-route-params';
import { GRUPO_DATA_KEY } from './grupo-data.resolver';
import { GrupoDatosGeneralesFragment } from './grupo-formulario/grupo-datos-generales/grupo-datos-generales.fragment';

export interface IGrupoData {
  isInvestigador: boolean
}

@Injectable()
export class GrupoActionService extends ActionService implements OnDestroy {

  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales'
  };

  private datosGenerales: GrupoDatosGeneralesFragment;

  private readonly data: IGrupoData;
  public readonly id: number;

  get isInvestigador(): boolean {
    return this.data.isInvestigador;
  }

  constructor(
    logger: NGXLogger,
    route: ActivatedRoute,
    grupoService: GrupoService,
    grupoEquipoService: GrupoEquipoService,
    palabraClaveService: PalabraClaveService,
    rolProyectoService: RolProyectoService,
    vinculacionService: VinculacionService
  ) {
    super();
    this.id = Number(route.snapshot.paramMap.get(GRUPO_ROUTE_PARAMS.ID));

    if (this.id) {
      this.enableEdit();
      this.data = route.snapshot.data[GRUPO_DATA_KEY];
    }

    this.datosGenerales = new GrupoDatosGeneralesFragment(logger, this.id, grupoService, grupoEquipoService, palabraClaveService, rolProyectoService, vinculacionService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);

    this.datosGenerales.initialize();
  }

}
