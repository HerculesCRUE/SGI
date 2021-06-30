import { Injectable } from '@angular/core';
import { CanDeactivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { FragmentComponent } from '../component/fragment.component';

@Injectable({
  providedIn: 'root'
})
export class FragmentGuard implements CanDeactivate<FragmentComponent> {
  constructor() { }

  canDeactivate(
    component: FragmentComponent,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    component.onRouteChange();
    return true;
  }
}
