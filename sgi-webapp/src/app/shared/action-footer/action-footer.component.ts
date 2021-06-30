import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActionStatus, IActionService } from '@core/services/action-service';
import { Subject, Subscription } from 'rxjs';

@Component({
  selector: 'sgi-action-footer',
  templateUrl: './action-footer.component.html',
  styleUrls: ['./action-footer.component.scss']
})
export class ActionFooterComponent implements OnInit, OnDestroy {
  @Input() texto: string;
  @Input() actionService: IActionService;
  readonly event$ = new Subject<boolean>();

  status: ActionStatus;

  private subscriptions: Subscription[] = [];

  constructor() { }

  ngOnInit(): void {
    this.subscriptions.push(this.actionService.status$.subscribe((status) => {
      this.status = status;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  save() {
    this.event$.next(true);
  }

  cancel() {
    this.event$.next(false);
  }
}
