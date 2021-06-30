import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  GuardsCheckEnd,
  NavigationCancel,
  NavigationEnd,
  NavigationStart,
  ResolveEnd,
  Route,
  Router,
  RouterEvent,
  UrlSegment
} from '@angular/router';
import { BehaviorSubject } from 'rxjs';

export interface Navigation {
  segments: UrlSegment[];
  routeConfig: Route;
}

@Injectable({
  providedIn: 'root'
})
export class NavigationService {

  navigation$ = new BehaviorSubject<Navigation[]>([]);
  lastNavigation: string;
  currentNavigation: string;

  private navigationStack: Navigation[] = [];

  constructor(private router: Router) {
    this.router.events.subscribe((event: RouterEvent) => this.processEvent(event));
  }

  private processEvent(event: RouterEvent): void {
    if (event instanceof NavigationStart) {
      this.navigationStack = [];
    }
    if (event instanceof GuardsCheckEnd) {
      const guardsCheckEnd: GuardsCheckEnd = event;
      if (guardsCheckEnd.shouldActivate) {
        this.navigationStack = this.getNavigation(guardsCheckEnd.state.root);
      }
    }
    if (event instanceof NavigationCancel) {
      this.navigationStack = [];
    }
    if (event instanceof ResolveEnd) {
      const nav = this.router.getCurrentNavigation();
      if (nav.extras.state) {
        nav.extras.state.from = this.currentNavigation;
      }
      else {
        nav.extras.state = { from: this.currentNavigation };
      }
    }
    if (event instanceof NavigationEnd) {
      if (!this.lastNavigation) {
        this.currentNavigation = this.getLastNavigationUrl(this.navigationStack);
        this.lastNavigation = this.currentNavigation;
      }
      else {
        this.lastNavigation = this.currentNavigation;
        this.currentNavigation = this.getLastNavigationUrl(this.navigationStack);
      }
      this.navigation$.next(this.navigationStack);
    }
  }


  private getNavigation(route: ActivatedRouteSnapshot): Navigation[] {
    const navigations: Navigation[] = [];
    navigations.push({
      routeConfig: route.routeConfig ? route.routeConfig : undefined,
      segments: route.url ? route.url : []
    });
    route.children.forEach((child) => {
      navigations.push(...this.getNavigation(child));
    });
    return navigations;
  }

  private getLastNavigationUrl(navigationStack: Navigation[]): string {
    const urlStack: string[] = [];
    navigationStack.forEach((navigation) => {
      if (navigation.segments && navigation.segments.length > 0) {
        urlStack.push(...navigation.segments.map((s) => s.path));
      }
    });
    return '/' + urlStack.join('/');
  }
}
