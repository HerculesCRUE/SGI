import { Component, HostBinding, Input, OnDestroy } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-menu-subitem-external',
  templateUrl: './menu-subitem-external.component.html',
  styleUrls: ['./menu-subitem-external.component.scss']
})
export class MenuSubItemExternalComponent implements OnDestroy {
  @Input() route: string | string[];
  @Input() title: string;

  private subcription: Subscription;

  private opened: boolean;

  @HostBinding()
  get hidden() {
    return !this.opened;
  }

  constructor(private layout: LayoutService) {
    this.subcription = this.layout.menuOpened$.subscribe((val) => this.opened = val);
  }

  ngOnDestroy(): void {
    this.subcription.unsubscribe();
  }
}
