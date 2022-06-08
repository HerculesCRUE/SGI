import { DecimalPipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { IRango, TipoTemporalidad } from '@core/models/prc/rango';

@Pipe({
  name: 'rango'
})
export class RangoPipe implements PipeTransform {

  constructor(private numberPipe: DecimalPipe) {
  }

  transform(value: IRango): string {
    switch (value?.tipoTemporalidad) {
      case TipoTemporalidad.INICIAL:
        return `<= ${this.formatNumber(value.hasta)}`;
      case TipoTemporalidad.INTERMEDIO:
        return `${this.formatNumber(value.desde ?? 0)} - ${this.formatNumber(value.hasta)}`;
      case TipoTemporalidad.FINAL:
        return `>=  ${this.formatNumber(value.desde ?? 0)}`;
      default:
        return '';
    }
  }

  private formatNumber(value: number): string {
    return this.numberPipe.transform(value, '1.0-0');
  }

}
