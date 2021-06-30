import { Component, OnDestroy, OnInit } from '@angular/core';
import { BreadcrumbData, LayoutService, Title } from '@core/services/layout.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  data: BreadcrumbData[];
  title: Title;
  constructor(
    private layoutService: LayoutService
  ) { }

  ngOnInit(): void {
    this.subscriptions.push(this.layoutService.breadcrumData$.subscribe((data) => this.data = data));
    this.subscriptions.push(this.layoutService.title$.subscribe((title) => this.title = title));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

}
