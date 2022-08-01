import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { DialogCommonComponent } from '@core/component/dialog-common.component';
import { Module } from '@core/module';

@Component({
  templateUrl: './selector-modulo.component.html',
  styleUrls: ['./selector-modulo.component.scss']
})
export class SelectorModuloComponent extends DialogCommonComponent {

  get modulos() {
    return Module.values;
  }

  constructor(
    dialogRef: MatDialogRef<SelectorModuloComponent>,
    private router: Router
  ) {
    super(dialogRef);
  }

  openModulo(modulo: string) {
    this.router.navigateByUrl(modulo).then(
      () => {
        this.close();
      }
    );
  }
}
