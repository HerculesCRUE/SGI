import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IMemoria } from '@core/models/eti/memoria';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { MemoriaService } from '@core/services/eti/memoria.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_NOT_FOUND = marker('error.load');

@Injectable()
export class MemoriaResolver extends SgiResolverResolver<IMemoria> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: MemoriaService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IMemoria> {
    const peticion = this.service.findById(Number(route.paramMap.get('id')));
    if (this.authService.hasAuthorityForAnyUO('ETI-MEM-INV-ER')) {
      return this.service.isResponsableOrCreador(Number(route.paramMap.get('id'))).pipe(
        switchMap(response => {
          if (response) {
            return peticion;
          } else {
            return throwError('NOT_FOUND');
          }
        }));
    } else {
      return peticion;
    }
  }
}
