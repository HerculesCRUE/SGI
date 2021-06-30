import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Module } from '@core/module';
import { INV_ROUTE_NAMES } from 'src/app/module/inv/inv-route-names';

export const CONVOCATORIA_ID_KEY = 'idConvocatoria';

@Injectable()
export class SolicitudCrearGuard implements CanActivate {

  constructor(private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    const idConvocatoria = this.router.getCurrentNavigation()?.extras?.state?.[CONVOCATORIA_ID_KEY];
    if (idConvocatoria) {
      return true;
    }
    else {
      if (!this.router.navigated) {
        return this.router.createUrlTree(['/', Module.INV.path, INV_ROUTE_NAMES.SOLICITUDES]);
      }
      return false;
    }
  }

}