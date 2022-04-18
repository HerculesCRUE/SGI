import { Directive, OnDestroy, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { DialogFormComponent } from './dialog-form.component';

@Directive()
// tslint:disable-next-line: directive-class-suffix
export abstract class DialogActionComponent<R> extends DialogFormComponent<R> implements OnInit, OnDestroy {

  constructor(
    matDialogRef: MatDialogRef<any, R>,
    edit: boolean
  ) {
    super(matDialogRef, edit);
  }
}
