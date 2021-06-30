import { IActionService, IFormFragment, IFragment, FormFragment } from '@core/services/action-service';
import { FormGroup } from '@angular/forms';
import { OnInit, Directive } from '@angular/core';

export interface SgiOnRouteChange {
  onRouteChange(): void;
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class FragmentComponent implements SgiOnRouteChange, OnInit {
  // tslint:disable-next-line: variable-name
  private _service: IActionService;
  public readonly GROUP_NAME: string;
  public readonly fragment: IFragment;


  constructor(name: string, actionService: IActionService) {
    this.GROUP_NAME = name;
    this._service = actionService;
    this.fragment = actionService.getFragment(name);
  }

  onRouteChange(): boolean {
    this._service.performChecks(true);
    return true;
  }

  ngOnInit(): void {
    this.fragment.initialize();
  }
}

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class FormFragmentComponent<T> implements SgiOnRouteChange, OnInit {
  // tslint:disable-next-line: variable-name
  private _service: IActionService;
  public readonly fragment: IFormFragment<T>;


  constructor(name: string, actionService: IActionService) {
    this._service = actionService;
    this.fragment = actionService.getFragment(name) as FormFragment<T>;
  }

  onRouteChange(): boolean {
    this._service.performChecks(true);
    return true;
  }

  ngOnInit(): void {
    this.fragment.initialize();
  }

  get formGroup(): FormGroup {
    return this.fragment.getFormGroup();
  }
}
