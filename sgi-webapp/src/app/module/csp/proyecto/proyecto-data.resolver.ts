import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Estado } from '@core/models/csp/estado-proyecto';
import { IProyecto } from '@core/models/csp/proyecto';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProyectoService } from '@core/services/csp/proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { PROYECTO_ROUTE_PARAMS } from './proyecto-route-params';
import { IProyectoData } from './proyecto.action.service';

const MSG_NOT_FOUND = marker('csp.proyecto.editar.no-encontrado');

export const PROYECTO_DATA_KEY = 'proyectoData';

@Injectable()
export class ProyectoDataResolver extends SgiResolverResolver<IProyectoData> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: ProyectoService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProyectoData> {
    return this.service.findById(Number(route.paramMap.get(PROYECTO_ROUTE_PARAMS.ID))).pipe(
      map(proyecto => {
        return {
          proyecto,
          readonly: this.isReadonly(proyecto)
        };
      })
    );
  }

  private isReadonly(proyecto: IProyecto): boolean {
    if (!proyecto.activo) {
      return true;
    }
    if (proyecto.estado.estado === Estado.RENUNCIADO || proyecto.estado.estado === Estado.RESCINDIDO) {
      return true;
    }
    return false;
  }
}
