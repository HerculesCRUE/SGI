import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { ActionService } from '@core/services/action-service';
import { AreaTematicaService } from '@core/services/csp/area-tematica.service';
import { NGXLogger } from 'ngx-logger';
import { AreaTematicaArbolFragment } from './area-tematica-formulario/area-tematica-arbol/area-tematica-arbol.fragment';
import { AreaTematicaDatosGeneralesFragment } from './area-tematica-formulario/area-tematica-datos-generales/area-tematica-datos-generales.fragment';



@Injectable()
export class AreaTematicaActionService extends ActionService {
  public readonly FRAGMENT = {
    DATOS_GENERALES: 'datos-generales',
    AREAS_ARBOL: 'arbol-areas'
  };

  private area: IAreaTematica;
  private datosGenerales: AreaTematicaDatosGeneralesFragment;
  private areaTematicaArbol: AreaTematicaArbolFragment;

  constructor(
    private readonly logger: NGXLogger,
    route: ActivatedRoute,
    areaTematicaService: AreaTematicaService
  ) {
    super();
    this.area = {} as IAreaTematica;
    if (route.snapshot.data.area) {
      this.area = route.snapshot.data.area;
      this.enableEdit();
    }

    this.datosGenerales = new AreaTematicaDatosGeneralesFragment(logger, this.area?.id,
      areaTematicaService);

    this.areaTematicaArbol = new AreaTematicaArbolFragment(logger, this.area?.id,
      areaTematicaService);

    this.addFragment(this.FRAGMENT.DATOS_GENERALES, this.datosGenerales);
    this.addFragment(this.FRAGMENT.AREAS_ARBOL, this.areaTematicaArbol);
  }

  getArea(): IAreaTematica {
    return this.area;
  }

}
