import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { ComponentType } from '@angular/cdk/portal';
import { PlatformLocation } from '@angular/common';
import { Directive, Input, OnInit, Optional, Self, TemplateRef } from '@angular/core';
import { NgControl } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { EntityKey, SelectServiceComponent } from '../select-service/select-service.component';

/** Base select component for selects of SGI entities. Allow loading options from a service */
@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class SelectServiceExtendedComponent<T extends EntityKey> extends SelectServiceComponent<T> {

  public readonly reloadable = true;

  get addable(): boolean {
    return this.isAddAuthorized();
  }

  protected addTarget: ComponentType<any> | TemplateRef<any> | string;

  // tslint:disable-next-line: variable-name
  protected readonly _baseUrl: string;

  @Input()
  get extended(): boolean {
    return this._extended;
  }
  set extended(value: boolean) {
    this._extended = coerceBooleanProperty(value);
  }
  // tslint:disable-next-line: variable-name
  private _extended = true;

  constructor(
    defaultErrorStateMatcher: ErrorStateMatcher,
    @Self() @Optional() ngControl: NgControl,
    platformLocation?: PlatformLocation,
    private dialog?: MatDialog
  ) {
    super(defaultErrorStateMatcher, ngControl);

    if (platformLocation) {
      this._baseUrl = `${platformLocation.protocol}//${platformLocation.hostname}`;
      if (platformLocation.port) {
        this._baseUrl += `:${platformLocation.port}`;
      }
    }
  }

  onContainerClick(event: MouseEvent): void {
    if (!event.defaultPrevented) {
      super.onContainerClick(event);
    }
  }

  public reload(event?: MouseEvent): void {
    if (this.reloadable) {
      if (!!event) {
        event.preventDefault();
      }
      this.loadData();
    }
  }

  add(event?: MouseEvent): void {
    if (this.addable) {
      event.preventDefault();
      if (!!this.addTarget && typeof this.addTarget === 'string') {
        window.open(this.addTarget, '_blank');
      } else if (!!this.addTarget && typeof this.addTarget !== 'string' && !!this.dialog) {
        const dialogRef = this.dialog.open(this.addTarget, {
          panelClass: 'sgi-dialog-container',
        });

        dialogRef.afterClosed().subscribe((value) => {
          if (!!value) {
            this.reload();
          }
        });
      }
    }
  }

  protected abstract isAddAuthorized(): boolean;
}
