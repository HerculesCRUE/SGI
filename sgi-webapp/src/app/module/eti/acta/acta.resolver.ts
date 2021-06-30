import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IActa } from '@core/models/eti/acta';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ActaService } from '@core/services/eti/acta.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { NGXLogger } from 'ngx-logger';
import { Observable } from 'rxjs';

const MSG_NOT_FOUND = marker('eti.acta.editar.no-encontrado');

@Injectable()
export class ActaResolver extends SgiResolverResolver<IActa> {

  constructor(logger: NGXLogger, router: Router, snackBar: SnackBarService, private service: ActaService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IActa> {
    return this.service.findById(Number(route.paramMap.get('id')));
  }
}
