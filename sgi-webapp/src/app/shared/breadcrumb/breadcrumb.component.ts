import { Component, OnDestroy, OnInit } from '@angular/core';
import { BreadcrumbData, LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  data: BreadcrumbData[];

  constructor(
    private layoutService: LayoutService
  ) { }

  ngOnInit(): void {
    this.subscriptions.push(this.layoutService.breadcrumData$.subscribe((data) => this.data = data));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

}
