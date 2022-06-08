import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { ProduccionCientificaService } from '@core/services/prc/produccion-cientifica/produccion-cientifica.service';
import { SgiAuthService } from '@sgi/framework/auth';
import { Observable } from 'rxjs';
import { PRODUCCION_CIENTIFICA_ROUTE_PARAMS } from './produccion-cientifica-route-params';

@Injectable()
export class ProduccionCientificaInvGuard implements CanActivate {
  constructor(
    private authService: SgiAuthService,
    private service: ProduccionCientificaService,
  ) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const produccionCientificaId = Number(route.paramMap.get(PRODUCCION_CIENTIFICA_ROUTE_PARAMS.ID));
    return this.authService.hasAuthority('PRC-VAL-INV-ER') && this.service.isAccesibleByInvestigador(produccionCientificaId);
  }
}
