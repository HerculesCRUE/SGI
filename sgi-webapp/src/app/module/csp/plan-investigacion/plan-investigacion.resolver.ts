import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IPrograma } from '@core/models/csp/programa';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProgramaService } from '@core/services/csp/programa.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('error.load');

@Injectable()
export class PlanInvestigacionResolver extends SgiResolverResolver<IPrograma> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProgramaService
  ) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IPrograma> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
