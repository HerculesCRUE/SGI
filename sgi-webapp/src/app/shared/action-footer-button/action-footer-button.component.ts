import { Component, Input, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';

@Component({
  selector: 'sgi-action-footer-button',
  templateUrl: './action-footer-button.component.html',
  styleUrls: ['./action-footer-button.component.scss']
})
export class ActionFooterButtonComponent implements OnDestroy {
  @Input() icon = 'save';
  @Input() text: string;
  @Input() color = 'accent';
  @Input() disabled = false;
  @Input() action = 'save';
  @Input() type: 'default' | 'cancel' = 'default';

  readonly action$ = new Subject<string>();
  readonly destroyed = new Subject<void>();

  constructor() { }

  ngOnDestroy(): void {
    this.destroyed.next();
    this.destroyed.complete();
  }

  doClick($event: any) {
    this.action$.next(this.action);
  }
}
