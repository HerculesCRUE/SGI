import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IAreaTematica } from '@core/models/csp/area-tematica';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { AreaTematicaService } from '@core/services/csp/area-tematica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('error.load');

@Injectable()
export class AreaTematicaResolver extends SgiResolverResolver<IAreaTematica> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: AreaTematicaService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IAreaTematica> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
