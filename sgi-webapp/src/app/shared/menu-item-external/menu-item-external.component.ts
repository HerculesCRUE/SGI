import { Component, Input, OnDestroy } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-menu-item-external',
  templateUrl: './menu-item-external.component.html',
  styleUrls: ['./menu-item-external.component.scss']
})
export class MenuItemExternalComponent implements OnDestroy {
  @Input() route: string | string[];
  @Input() title: string;
  @Input() icon: string;

  private subcription: Subscription;

  opened: boolean;

  constructor(private layout: LayoutService) {
    this.subcription = this.layout.menuOpened$.subscribe((val) => this.opened = val);
  }

  ngOnDestroy(): void {
    this.subcription.unsubscribe();
  }

}
