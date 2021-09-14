import { Injectable } from '@angular/core';
import {
  Router,
  ActivatedRouteSnapshot
} from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IInvencion } from '@core/models/pii/invencion';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { InvencionService } from '@core/services/pii/invencion/invencion.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { INVENCION_ROUTE_PARAMS } from './invencion-route-params';
import { IInvencionData } from './invencion.action.service';

const MSG_NOT_FOUND = marker('error.load');

export const INVENCION_DATA_KEY = 'invencionData';

@Injectable()
export class InvencionResolver extends SgiResolverResolver<IInvencionData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: InvencionService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IInvencionData> {

    const invencionId = Number(route.paramMap.get(INVENCION_ROUTE_PARAMS.ID));

    return this.service.findById(invencionId).pipe(
      map(invencion => {
        if (!invencion) {
          throwError('NOT_FOUND');
        }
        return {
          canEdit: this.authService.hasAuthority('PII-INV-E'),
          tipoPropiedad: invencion.tipoProteccion.tipoPropiedad,
          invencion
        };
      }));
  }
}
