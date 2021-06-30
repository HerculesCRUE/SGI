import { AfterViewInit, Component, Input, ViewChild } from '@angular/core';
import { RouterLinkActive } from '@angular/router';
import { IFragment } from '@core/services/action-service';

@Component({
  selector: 'sgi-action-fragment-menu-item',
  templateUrl: './action-fragment-menu-item.component.html',
  styleUrls: ['./action-fragment-menu-item.component.scss']
})
export class ActionFragmentMenuItemComponent implements AfterViewInit {
  @Input() fragment: IFragment;
  @Input() route: string | string[];
  @Input() title: string;

  @ViewChild(RouterLinkActive, { static: true }) private activeLink: RouterLinkActive;
  private initialized = false;

  get isActive(): boolean {
    if (this.initialized) {
      return this.activeLink.isActive;
    }
    return false;
  }

  constructor() { }

  ngAfterViewInit(): void {
    this.initialized = true;
  }

}
