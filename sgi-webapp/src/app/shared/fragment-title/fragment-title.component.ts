import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { LayoutService, Title } from '@core/services/layout.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-fragment-title',
  templateUrl: './fragment-title.component.html',
  styleUrls: ['./fragment-title.component.scss']
})
export class FragmentTitleComponent implements OnInit, OnDestroy {
  @Input()
  title: string;

  routeTitle: Title;

  private subscriptions: Subscription[] = [];

  constructor(private layoutService: LayoutService) { }

  ngOnInit(): void {
    this.subscriptions.push(this.layoutService.title$.subscribe((title) => this.routeTitle = title));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
