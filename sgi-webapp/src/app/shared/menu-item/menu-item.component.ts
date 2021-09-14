import { AfterViewInit, Component, Input, OnDestroy, ViewChild } from '@angular/core';
import { RouterLinkActive } from '@angular/router';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.scss']
})
export class MenuItemComponent implements AfterViewInit, OnDestroy {
  @Input() route: string | string[];
  @Input() title: string;
  @Input() icon: string;

  @ViewChild(RouterLinkActive, { static: true }) private activeLink: RouterLinkActive;
  private initialized = false;
  private subcription: Subscription;

  get isActive(): boolean {
    if (this.initialized) {
      return this.activeLink.isActive;
    }
    return false;
  }
  opened: boolean;

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
