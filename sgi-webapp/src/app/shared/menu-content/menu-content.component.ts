import { AfterViewInit, Component, ContentChild, Directive, OnDestroy, Optional } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AbstractMenuContentComponent } from '@core/component/abstract-menu-content.component';
import { SgiProblem } from '@core/errors/sgi-error';
import { Subscription } from 'rxjs';

@Directive({
  // tslint:disable-next-line: directive-selector
  selector: 'sgi-menu-content-footer'
})
// tslint:disable-next-line: directive-class-suffix
export class MenuContentFooter { }

@Component({
  selector: 'sgi-menu-content',
  templateUrl: './menu-content.component.html',
  styleUrls: ['./menu-content.component.scss']
})
export class MenuContentComponent implements AfterViewInit, OnDestroy {
  @ContentChild(MenuContentFooter) content!: MenuContentFooter;

  problems: SgiProblem[] = [];

  private subscription: Subscription;

  constructor(@Optional() private routerOutlet: RouterOutlet) { }

  ngAfterViewInit(): void {
    let component: AbstractMenuContentComponent = null;
    // TODO: Hacky injection of current component. Remove when exists an alternative
    if (this.routerOutlet) {
      if (this.routerOutlet.component instanceof AbstractMenuContentComponent) {
        component = this.routerOutlet.component;
      }
      else {
        component = null;
      }
    }
    else {
      component = null;
    }
    if (component) {
      this.subscription = component.problems$.subscribe((value) => this.problems = value);
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

}
