import { AfterViewInit, ContentChildren, Directive, Input, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActionStatus, IActionService } from '@core/services/action-service';
import { ActionFooterButtonComponent } from '@shared/action-footer-button/action-footer-button.component';
import { merge, Subject, Subscription } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class FooterComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChildren(ActionFooterButtonComponent) private viewButtons!: QueryList<ActionFooterButtonComponent>;
  @ContentChildren(ActionFooterButtonComponent) private contentButtons!: QueryList<ActionFooterButtonComponent>;

  @Input() actionService: IActionService;

  readonly event$ = new Subject<any>();

  status: ActionStatus = { changes: false, complete: false, edit: false, errors: false, problems: false };
  protected subscriptions: Subscription[] = [];

  private buttonSubscriptions: Subscription[] = [];

  constructor() { }

  ngOnInit(): void {
    this.subscriptions.push(this.actionService.status$.subscribe((status) => {
      this.status = status;
    }));
  }

  ngAfterViewInit(): void {
    this.subscribeToButtons();
    this.subscriptions.push(
      merge(this.viewButtons.changes, this.contentButtons.changes).subscribe(
        () => this.subscribeToButtons()
      )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.buttonSubscriptions.forEach(subscription => subscription.unsubscribe());
  }

  private subscribeToButtons(): void {
    const buttonSuscriptions: Subscription[] = [];
    this.viewButtons.toArray().concat(this.contentButtons.toArray()).forEach(button => {

      buttonSuscriptions.push(
        button.action$.pipe(
          takeUntil(button.destroyed)
        ).subscribe(
          (action) => this.event$.next(action)
        )
      );

    });
    this.buttonSubscriptions.forEach(subscription => subscription.unsubscribe());
    this.buttonSubscriptions = buttonSuscriptions;
  }
}

