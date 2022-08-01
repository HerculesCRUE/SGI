import { Injectable } from '@angular/core';
import {
  Router,
  ActivatedRouteSnapshot
} from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IProduccionCientifica } from '@core/models/prc/produccion-cientifica';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ProduccionCientificaService } from '@core/services/prc/produccion-cientifica/produccion-cientifica.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { map } from 'rxjs/operators';
import { PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from './produccion-cientifica-route-params';

const MSG_NOT_FOUND = marker('error.load');

export interface IProduccionCientificaData {
  canEdit: boolean;
  produccionCientifica: IProduccionCientifica;
}

@Injectable()
export class ProduccionCientificaResolver extends SgiResolverResolver<IProduccionCientificaData> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ProduccionCientificaService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IProduccionCientificaData> {

    const produccionCientificaId = Number(route.paramMap.get(PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID));

    return this.service.findById(produccionCientificaId).pipe(
      map(produccionCientifica => {
        if (!produccionCientifica) {
          throwError('NOT_FOUND');
        }
        return {
          canEdit: this.authService.hasAuthority('PII-INV-E'),
          produccionCientifica
        };
      }));
  }
}
