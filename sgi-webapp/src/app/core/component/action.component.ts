import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { IActionService } from '@core/services/action-service';
import { Observable, of, Subscription } from 'rxjs';
import { DialogService } from '@core/services/dialog.service';
import { map } from 'rxjs/operators';
import { OnDestroy, OnInit, ViewChild, Directive } from '@angular/core';
import { ActionFooterComponent } from '@shared/action-footer/action-footer.component';
import { ActivatedRoute, Router } from '@angular/router';
import { DIALOG_BUTTON_STYLE } from '@block/dialog/dialog.component';

const MSG_FORM_UNSAVED = marker('msg.unsaved');
const MSG_FORM_UNSAVED_CANCEL = marker('btn.cancel');
const MSG_FORM_UNSAVED_CONTINUE = marker('btn.continue');

export interface SgiAllowNavigation {
  allowNavigation(): Observable<boolean>;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class ActionComponent implements SgiAllowNavigation, OnInit, OnDestroy {
  @ViewChild(ActionFooterComponent, { static: true }) footer: ActionFooterComponent;
  // tslint:disable-next-line: variable-name
  private _service: IActionService;
  protected router: Router;
  protected activatedRoute: ActivatedRoute;
  protected subscriptions: Subscription[] = [];


  constructor(router: Router, activatedRoute: ActivatedRoute, actionService: IActionService, private dialogService: DialogService) {
    this.router = router;
    this.activatedRoute = activatedRoute;
    this._service = actionService;
  }

  ngOnInit(): void {
    if (this.footer) {
      this.subscriptions.push(this.footer.event$.subscribe((value: boolean) => {
        if (value) {
          this.saveOrUpdate();
        }
        else {
          this.cancel();
        }
      }));
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  allowNavigation(): Observable<boolean> {
    if (this._service.hasChanges()) {
      return this.dialogService.showConfirmation(
        MSG_FORM_UNSAVED,
        {},
        MSG_FORM_UNSAVED_CONTINUE,
        MSG_FORM_UNSAVED_CANCEL,
        DIALOG_BUTTON_STYLE.BTN_STYLE_WARN,
        DIALOG_BUTTON_STYLE.BTN_STYLE_ACCENT
      ).pipe(map((val) => val));
    }
    return of(true);
  }

  protected cancel(): void {
    this.router.navigate(['../'], { relativeTo: this.activatedRoute });
  }

  abstract saveOrUpdate(): void;


}
