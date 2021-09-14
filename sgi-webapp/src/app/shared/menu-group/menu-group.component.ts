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
import { LayoutService } from '@core/services/layout.service';
import { MenuSubItemComponent } from '@shared/menu-subitem/menu-subitem.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-menu-group',
  templateUrl: './menu-group.component.html',
  styleUrls: ['./menu-group.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MenuGroupComponent implements AfterViewInit, AfterContentChecked, OnDestroy {
  @Input() title: string;
  @Input() icon: string;

  @ContentChildren(MenuSubItemComponent, { descendants: true }) private menuItems!: QueryList<MenuSubItemComponent>;
  @ViewChild(MatExpansionPanel, { static: true }) private panel: MatExpansionPanel;

  private activated = false;
  isChildActive = false;
  opened = false;

  private subscriptions: Subscription[] = [];

  constructor(router: Router, private readonly cdr: ChangeDetectorRef, private layout: LayoutService) {
    this.subscriptions.push(router.events.subscribe((s: Event) => {
      if (s instanceof NavigationEnd) {
        this.cdr.markForCheck();
      }
    }));
  }

  // Workaround for angular component issue https://github.com/angular/components/issues/13870
  disableAnimation = true;
  ngAfterViewInit(): void {
    this.subscriptions.push(this.layout.menuOpened$.subscribe((val) => {
      this.opened = val;
      setTimeout(() => this.cdr.markForCheck());
    }));
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

  private isActive(): boolean {
    return this.menuItems.some(items => items.isActive);
  }
}
