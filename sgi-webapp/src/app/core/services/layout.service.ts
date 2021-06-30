import { Injectable } from '@angular/core';
import { Data } from '@angular/router';
import { Module } from '@core/module';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject } from 'rxjs';
import { Navigation, NavigationService } from './navigation.service';

export interface Title {
  key: string;
  params: { [key: string]: any };
}
export interface BreadcrumbData {
  title: Title;
  path: string;
}

@Injectable({
  providedIn: 'root'
})
export class LayoutService {

  activeModule$ = new BehaviorSubject<Module>(undefined);
  menuOpened$ = new BehaviorSubject<boolean>(true);
  menuAutoclose$ = new BehaviorSubject<boolean>(false);
  breadcrumData$ = new BehaviorSubject<BreadcrumbData[]>([]);
  title$ = new BehaviorSubject<Title>(undefined);

  constructor(private navigationService: NavigationService,
    private readonly translate: TranslateService) {
    this.navigationService.navigation$.subscribe((navigationStack) => {
      if (navigationStack.length > 0) {
        this.parseNavigationStack(navigationStack);
      }
    });
  }

  private parseNavigationStack(navigationStack: Navigation[]): void {
    // Build breadcrumb data
    const breadcrumbData = this.getBreadcrumbData(navigationStack);

    // Publis new active module
    this.activeModule$.next(this.getActiveModule(navigationStack));

    // Publish new breadcrumb data
    this.breadcrumData$.next(breadcrumbData);

    // Publis new title
    this.title$.next(this.getTitle(navigationStack));

    if (breadcrumbData.length > 1) {
      this.menuAutoclose$.next(true);
      this.closeMenu();
    }
    else {
      this.menuAutoclose$.next(false);
      this.openMenu();
    }
  }

  private getBreadcrumbData(navigationStack: Navigation[]): BreadcrumbData[] {
    const data: BreadcrumbData[] = [];
    const urlStack: string[] = [];

    // The last navigation is discarded every time
    let endPositionsToDiscard = 1;
    // Check reverse navigation to discard nested empty end paths or paths without a title defined
    let p = navigationStack.length - 1;
    while (p > 0 && (navigationStack[p].segments.length < 1 || !navigationStack[p]?.routeConfig?.data?.title)) {
      endPositionsToDiscard++;
      p--;
    }

    const endPosition = navigationStack.length - endPositionsToDiscard;
    for (let i = 0; i < endPosition; i++) {
      const navigation = navigationStack[i];
      if (navigation.segments && navigation.segments.length > 0) {
        urlStack.push(...navigation.segments.map((s) => s.path));
        data.push({
          // If no title defined for navigation, find the nearest right title
          title: this.getNearestTitle(navigationStack.slice(i)),
          path: '/' + urlStack.join('/')
        });
      }
    }
    return data;
  }

  private getNearestTitle(navigations: Navigation[]): Title {
    const nav = navigations.find((n) => n.routeConfig?.data?.title);
    if (nav?.routeConfig?.data?.title) {
      return this.toTitle(nav.routeConfig.data);
    }
    return undefined;
  }

  private getActiveModule(navigationStack: Navigation[]): Module {
    const path = navigationStack.find((nav) => nav.segments.length > 0)?.routeConfig?.path;
    return Module.fromPath(path);
  }

  private getTitle(navigationStack: Navigation[]): Title {
    if (navigationStack.length === 0) {
      return undefined;
    }
    let endPositionsToDiscard = 0;
    // Check reverse navigation to discard nested paths without a title defined
    let p = navigationStack.length - 1;
    while (p > 0 && (!navigationStack[p]?.routeConfig?.data?.title)) {
      endPositionsToDiscard++;
      p--;
    }
    const nav = navigationStack[navigationStack.length - endPositionsToDiscard - 1];
    if (nav?.routeConfig?.data?.title) {
      return this.toTitle(nav.routeConfig.data);
    }
    return undefined;
  }

  private toTitle(data: Data): Title {
    let key: string;
    let params: { [key: string]: any };
    key = data.title;
    if (data.titleParams) {
      params = this.resolvedParams(data.titleParams);
    } else {
      params = {};
    }
    return { key, params };
  }

  resolvedParams(titleParams: any): any {
    if (titleParams.entity) {
      this.translate.get(
        titleParams.entity,
        { count: titleParams.count }
      ).subscribe((value) => titleParams.entity = value);

    }

    return titleParams;
  }

  openMenu(): void {
    this.menuOpened$.next(true);
  }

  closeMenu(): void {
    this.menuOpened$.next(false);
  }

  toggleMenu(): void {
    this.menuOpened$.next(!this.menuOpened$.value);
  }

}
