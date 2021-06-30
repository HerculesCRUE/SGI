import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SgiAuthService } from '@sgi/framework/auth';
import { Module } from '@core/module';


@Injectable({
  providedIn: 'root'
})
export class AllowModuleGuard implements CanActivate {

  constructor(private authService: SgiAuthService, private router: Router) {

  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    const module: Module = Module.fromPath(route.routeConfig.path);
    if (this.authService.hasModuleAccess(module.code)) {
      return true;
    }
    return this.router.createUrlTree(['/']);
  }

}