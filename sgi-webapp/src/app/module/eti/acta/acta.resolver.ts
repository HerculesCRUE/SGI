import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IActa } from '@core/models/eti/acta';
import { Module } from '@core/module';
import { SgiResolverResolver } from '@core/resolver/sgi-resolver';
import { ActaService } from '@core/services/eti/acta.service';
import { SnackBarService } from '@core/services/snack-bar.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { NGXLogger } from 'ngx-logger';
import { Observable, throwError } from 'rxjs';
import { switchMap } from 'rxjs/operators';

const MSG_NOT_FOUND = marker('eti.acta.editar.no-encontrado');

@Injectable()
export class ActaResolver extends SgiResolverResolver<IActa> {

  constructor(
    logger: NGXLogger,
    router: Router,
    snackBar: SnackBarService,
    private service: ActaService,
    private authService: SgiAuthService) {
    super(logger, router, snackBar, MSG_NOT_FOUND);
  }

  protected resolveEntity(route: ActivatedRouteSnapshot): Observable<IActa> {
    const isInvestigador = route.data.module === Module.INV && this.hasViewAuthorityInv();

    const acta$ = this.service.findById(Number(route.paramMap.get('id')));
    if (isInvestigador) {
      return this.service.isMiembroActivoComite(Number(route.paramMap.get('id'))).pipe(
        switchMap(response => {
          if (response) {
            return acta$;
          } else {
            return throwError('NOT_FOUND');
          }
        })
      );
    } else {
      return acta$;
    }
  }

  private hasViewAuthorityInv(): boolean {
    return this.authService.hasAuthority('ETI-ACT-INV-ER');
  }
}
