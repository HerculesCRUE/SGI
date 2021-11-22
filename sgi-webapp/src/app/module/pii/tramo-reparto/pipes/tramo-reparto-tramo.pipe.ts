import { DecimalPipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { ITramoReparto, Tipo } from '@core/models/pii/tramo-reparto';

@Pipe({
  name: 'tramoRepartoTramo'
})
export class TramoRepartoTramoPipe implements PipeTransform {

  constructor(private numberPipe: DecimalPipe) {
  }

  transform(value: ITramoReparto): string {
    switch (value?.tipo) {
      case Tipo.INICIAL:
        return `<= ${this.formatNumber(value.hasta)}`;
      case Tipo.INTERMEDIO:
        return `${this.formatNumber(value.desde)} - ${this.formatNumber(value.hasta)}`;
      case Tipo.FINAL:
        return `>=  ${this.formatNumber(value.desde)}`;
      default:
        return '';
    }
  }

  private formatNumber(value: number): string {
    return this.numberPipe.transform(value, '1.0-0');
  }

}
