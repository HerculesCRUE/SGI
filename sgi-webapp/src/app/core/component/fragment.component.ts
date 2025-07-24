import { Directive, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormFragment, IActionService, IFormFragment, IFragment } from '@core/services/action-service';
import { TranslateService } from '@ngx-translate/core';
import { Subject, Subscription } from 'rxjs';

export interface SgiOnRouteChange {
  onRouteChange(): void;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class FragmentComponent implements SgiOnRouteChange, OnInit, OnDestroy {
  // tslint:disable-next-line: variable-name
  private _service: IActionService;
  public readonly GROUP_NAME: string;
  public readonly fragment: IFragment;
  private _subscriptions: Subscription[] = [];
  protected readonly _destroy$ = new Subject();

  constructor(name: string, actionService: IActionService, translateService?: TranslateService) {
    this.GROUP_NAME = name;
    this._service = actionService;
    this.fragment = actionService.getFragment(name);

    if (translateService) {
      this._subscriptions.push(translateService.onLangChange.subscribe(() => {
        this.setupI18N();
      }));
    }
  }

  onRouteChange(): boolean {
    this._service.performChecks(true);
    return true;
  }

  ngOnInit(): void {
    this.fragment.initialize();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this._subscriptions.forEach(subscription => subscription.unsubscribe());
    this._destroy$.next();
  }

  protected abstract setupI18N(): void;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class FormFragmentComponent<T> implements SgiOnRouteChange, OnInit, OnDestroy {
  // tslint:disable-next-line: variable-name
  private _service: IActionService;
  public readonly fragment: IFormFragment<T>;
  private _subscriptions: Subscription[] = [];

  constructor(name: string, actionService: IActionService, translateService: TranslateService) {
    this._service = actionService;
    this.fragment = actionService.getFragment(name) as FormFragment<T>;

    if (translateService) {
      this._subscriptions.push(translateService.onLangChange.subscribe(() => {
        this.setupI18N();
      }));
    }
  }

  onRouteChange(): boolean {
    this._service.performChecks(true);
    return true;
  }

  ngOnInit(): void {
    this.fragment.initialize();
    this.setupI18N();
  }

  ngOnDestroy(): void {
    this._subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  get formGroup(): FormGroup {
    return this.fragment.getFormGroup();
  }

  protected abstract setupI18N(): void;
}
