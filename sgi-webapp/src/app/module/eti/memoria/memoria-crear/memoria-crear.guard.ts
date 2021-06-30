import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Module } from '@core/module';
import { MEMORIAS_ROUTE } from '../memoria-route-names';


@Injectable()
export class MemoriaCrearGuard implements CanActivate {

  constructor(private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    const idPeticionEvalaucion = this.router.getCurrentNavigation()?.extras?.state?.idPeticionEvaluacion;
    if (idPeticionEvalaucion) {
      return true;
    }
    else {
      if (!this.router.navigated) {
        return this.router.createUrlTree(['/', Module.INV.path, MEMORIAS_ROUTE]);
      }
      return false;
    }
  }

}