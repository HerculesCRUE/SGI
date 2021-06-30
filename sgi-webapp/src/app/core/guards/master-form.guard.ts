import { Injectable } from '@angular/core';
import { CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { ActionComponent } from '@core/component/action.component';

@Injectable({
  providedIn: 'root'
})
export class ActionGuard implements CanDeactivate<ActionComponent> {
  constructor() { }

  canDeactivate(
    component: ActionComponent,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return component.allowNavigation();
  }
}
