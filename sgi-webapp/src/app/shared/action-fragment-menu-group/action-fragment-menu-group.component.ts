import {
  AfterContentChecked,
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ContentChildren,
  Input,
  OnDestroy,
  QueryList,
  ViewChild
} from '@angular/core';
import { MatExpansionPanel } from '@angular/material/expansion';
import { Event, NavigationEnd, Router } from '@angular/router';
import { ActionFragmentMenuItemComponent } from '@shared/action-fragment-menu-item/action-fragment-menu-item.component';
import { BehaviorSubject, Subscription } from 'rxjs';

interface Status {
  changes: boolean;
  errors: boolean;
}

@Component({
  selector: 'sgi-action-fragment-menu-group',
  templateUrl: './action-fragment-menu-group.component.html',
  styleUrls: ['./action-fragment-menu-group.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ActionFragmentMenuGroupComponent implements AfterViewInit, AfterContentChecked, OnDestroy {
  @Input() title: string;

  @ContentChildren(ActionFragmentMenuItemComponent, { descendants: true }) private menuItems!: QueryList<ActionFragmentMenuItemComponent>;
  @ViewChild(MatExpansionPanel, { static: true }) private panel: MatExpansionPanel;

  status$: BehaviorSubject<Status> = new BehaviorSubject<Status>({ changes: false, errors: false });

  private activated = false;
  isChildActive = false;

  private subscriptions: Subscription[] = [];

  constructor(router: Router, private readonly cdr: ChangeDetectorRef) {
    this.subscriptions.push(router.events.subscribe((s: Event) => {
      if (s instanceof NavigationEnd) {
        this.cdr.markForCheck();
      }
    }));
  }

  // Workaround for angular component issue https://github.com/angular/components/issues/13870
  disableAnimation = true;
  ngAfterViewInit(): void {
    this.menuItems.forEach(item => {
      if (item.fragment) {
        this.subscriptions.push(item.fragment.status$.subscribe(() => {
          this.mergeStatus();
        }));
      }
    });
    setTimeout(() => this.disableAnimation = false);
  }

  ngAfterContentChecked(): void {
    this.isChildActive = this.isActive();
    if (this.isChildActive && !this.activated) {
      this.activated = true;
      this.panel.open();
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private mergeStatus(): void {
    const current = this.status$.value;
    current.changes = this.hasChanges();
    current.errors = this.hasErrors();
    this.status$.next(current);
  }

  private hasChanges(): boolean {
    return this.menuItems.some(items => items.fragment?.hasChanges());
  }

  private hasErrors(): boolean {
    return this.menuItems.some(items => items.fragment?.hasErrors());
  }

  private isActive(): boolean {
    return this.menuItems.some(items => items.isActive);
  }
}
