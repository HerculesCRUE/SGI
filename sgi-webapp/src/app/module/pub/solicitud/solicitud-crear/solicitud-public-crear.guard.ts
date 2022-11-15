import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Module } from '@core/module';
import { Observable } from 'rxjs';
import { PUB_ROUTE_NAMES } from '../../pub-route-names';

export const CONVOCATORIA_PUBLIC_ID_KEY = 'idConvocatoria';

@Injectable()
export class SolicitudPublicCrearGuard implements CanActivate {

  constructor(private router: Router) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    const idConvocatoria = this.router.getCurrentNavigation()?.extras?.state?.[CONVOCATORIA_PUBLIC_ID_KEY];
    if (idConvocatoria) {
      return true;
    }
    else {
      if (!this.router.navigated) {
        return this.router.createUrlTree(['/', Module.INV.path, PUB_ROUTE_NAMES.SOLICITUDES]);
      }
      return false;
    }
  }

}