import { AfterViewInit, Component, HostBinding, Input, OnDestroy, ViewChild } from '@angular/core';
import { RouterLinkActive } from '@angular/router';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-menu-subitem',
  templateUrl: './menu-subitem.component.html',
  styleUrls: ['./menu-subitem.component.scss']
})
export class MenuSubItemComponent implements AfterViewInit, OnDestroy {
  @Input() route: string | string[];
  @Input() title: string;

  @ViewChild(RouterLinkActive, { static: true }) private activeLink: RouterLinkActive;
  private initialized = false;
  private subcription: Subscription;

  get isActive(): boolean {
    if (this.initialized) {
      return this.activeLink.isActive;
    }
    return false;
  }
  private opened: boolean;

  @HostBinding()
  get hidden() {
    return !this.opened;
  }

  constructor(private layout: LayoutService) {
    this.subcription = this.layout.menuOpened$.subscribe((val) => this.opened = val);
  }

  ngAfterViewInit(): void {
    this.initialized = true;
  }

  ngOnDestroy(): void {
    this.subcription.unsubscribe();
  }
}
