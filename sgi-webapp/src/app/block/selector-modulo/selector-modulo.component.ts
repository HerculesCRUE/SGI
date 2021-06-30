import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { Module } from '@core/module';

const MSG_MODULO_CSP = marker('title.modulo.csp');
const MSG_MODULO_ETI = marker('title.modulo.eti');
const MSG_MODULO_INV = marker('title.modulo.inv');

interface SelectorModulo {
  module: Module;
  nombre: string;
}

@Component({
  templateUrl: './selector-modulo.component.html',
  styleUrls: ['./selector-modulo.component.scss']
})
export class SelectorModuloComponent {
  modulos: SelectorModulo[];

  constructor(
    public dialogRef: MatDialogRef<SelectorModuloComponent>,
    private router: Router
  ) {
    this.modulos = [
      {
        module: Module.CSP,
        nombre: MSG_MODULO_CSP
      },
      {
        module: Module.ETI,
        nombre: MSG_MODULO_ETI
      },
      {
        module: Module.INV,
        nombre: MSG_MODULO_INV
      }
    ];
  }

  closeModal(): void {
    this.dialogRef.close();
  }

  openModulo(modulo: string) {
    this.router.navigateByUrl(modulo).then(
      () => {
        this.closeModal();
      }
    );
  }
}
