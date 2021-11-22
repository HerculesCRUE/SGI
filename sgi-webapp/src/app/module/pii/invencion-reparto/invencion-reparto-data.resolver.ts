import { Injectable } from '@angular/core';
import {
  Router,
  ActivatedRouteSnapshot
} from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { RepartoService } from '@core/services/pii/reparto/reparto.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { INVENCION_REPARTO_ROUTE_PARAMS } from './invencion-reparto-route-params';
import { IInvencionRepartoData } from './invencion-reparto.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const INVENCION_REPARTO_DATA_KEY = 'invencionRepartoData';

@Injectable({
  providedIn: 'root'
})
export class InvencionRepartoDataResolver extends SgiResolverResolver<IInvencionRepartoData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: RepartoService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IInvencionRepartoData> {
    const repartoId = Number(route.paramMap.get(INVENCION_REPARTO_ROUTE_PARAMS.ID));

    return this.service.findById(repartoId).pipe(
      map(reparto => {
        if (!reparto) {
          throwError('NOT_FOUND');
        }
        return {
          canEdit: this.authService.hasAuthority('PII-INV-E'),
          reparto
        };
      }));
  }
}
