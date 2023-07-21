import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IRolProyecto } from '@core/models/csp/rol-proyecto';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { RolProyectoService } from '@core/services/csp/rol-proyecto/rol-proyecto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('error.load');

@Injectable()
export class RolEquipoResolver extends SgiResolverResolver<IRolProyecto> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: RolProyectoService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IRolProyecto> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
