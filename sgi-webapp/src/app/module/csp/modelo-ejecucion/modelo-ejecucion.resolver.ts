import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IModeloEjecucion } from '@core/models/csp/tipos-configuracion';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ModeloEjecucionService } from '@core/services/csp/modelo-ejecucion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('error.load');

@Injectable()
export class ModeloEjecucionResolver extends SgiResolverResolver<IModeloEjecucion> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: ModeloEjecucionService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IModeloEjecucion> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
