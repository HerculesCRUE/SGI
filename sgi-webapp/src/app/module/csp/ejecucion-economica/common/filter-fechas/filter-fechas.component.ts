import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { DateTime } from 'luxon';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-ejecucion-economica-filter-fechas',
  templateUrl: './filter-fechas.component.html',
  styleUrls: ['./filter-fechas.component.scss']
})
export class FilterFechasComponent implements OnInit, OnDestroy {

  @Input()
  set formGroupFechas(value: FormGroup) {
    this._formGroupFechas = value;
  }
  get formGroupFechas(): FormGroup {
    return this._formGroupFechas;
  }
  // tslint:disable-next-line: variable-name
  private _formGroupFechas: FormGroup;

  private subscriptions: Subscription[] = [];

  constructor() { }

  ngOnInit(): void {
    if (!!this._formGroupFechas) {
      this.subscriptions.push(
        this.formGroupFechas.controls.devengoDesde.valueChanges.subscribe(
          (value) => {
            if (!!value && !!!this.formGroupFechas.controls.devengoHasta.value) {
              this.formGroupFechas.controls.devengoHasta.setValue(DateTime.now());
            }
          }
        )
      );
      this.subscriptions.push(
        this.formGroupFechas.controls.contabilizacionDesde.valueChanges.subscribe(
          (value) => {
            if (!!value && !!!this.formGroupFechas.controls.contabilizacionHasta.value) {
              this.formGroupFechas.controls.contabilizacionHasta.setValue(DateTime.now());
            }
          }
        )
      );
      this.subscriptions.push(
        this.formGroupFechas.controls.pagoDesde.valueChanges.subscribe(
          (value) => {
            if (!!value && !!!this.formGroupFechas.controls.pagoHasta.value) {
              this.formGroupFechas.controls.pagoHasta.setValue(DateTime.now());
            }
          }
        )
      );
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }
}
