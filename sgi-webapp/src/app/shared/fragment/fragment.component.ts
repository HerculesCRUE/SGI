import { AfterViewInit, Component, Input, OnDestroy, Optional } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormFragmentComponent, FragmentComponent as BaseFragmentComponent } from '@core/component/fragment.component';
import { SgiProblem } from '@core/errors/sgi-error';
import { IFragment } from '@core/services/action-service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-fragment',
  templateUrl: './fragment.component.html',
  styleUrls: ['./fragment.component.scss']
})
export class FragmentComponent implements AfterViewInit, OnDestroy {

  @Input()
  title: string;

  problems: SgiProblem[] = [];

  private subscription: Subscription;

  constructor(@Optional() private routerOutlet: RouterOutlet) { }

  ngAfterViewInit(): void {
    let fragment: IFragment = null;
    // TODO: Hacky injection of current fragment. Remove when exists an alternative
    if (this.routerOutlet) {
      if (this.routerOutlet.component instanceof BaseFragmentComponent) {
        fragment = this.routerOutlet.component.fragment;
      }
      else if (this.routerOutlet.component instanceof FormFragmentComponent) {
        fragment = this.routerOutlet.component.fragment;
      }
      else {
        fragment = null;
      }
    }
    else {
      fragment = null;
    }
    if (fragment) {
      this.subscription = fragment.problems$.subscribe((value) => this.problems = value);
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }
}
