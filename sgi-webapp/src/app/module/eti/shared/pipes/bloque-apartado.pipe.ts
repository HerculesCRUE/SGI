import { Pipe, PipeTransform } from '@angular/core';
import { IApartado } from '@core/models/eti/apartado';

export function getApartadoNombre(apartado?: IApartado): string {
  let ordenNumber = '';
  if (apartado.padre) {
    const apartados = getSubapartados(apartado.padre, []);
    apartados.reverse().forEach(aptdo => {
      ordenNumber += '.' + aptdo.orden;
    });
  }
  if (apartado.bloque.orden === 0) {
    return apartado?.padre ? apartado?.padre.nombre : (apartado?.nombre);
  } else {
    return apartado?.padre ?
      (apartado?.bloque?.orden + ordenNumber + ' ' + apartado?.padre.nombre) : (apartado?.bloque?.orden + ordenNumber + '.' + apartado?.orden + ' ' + apartado?.nombre);
  }
}

export function getSubApartadoNombre(apartado?: IApartado): string {
  let ordenNumber = '';
  if (apartado.padre) {
    const apartados = getSubapartados(apartado.padre, []);
    apartados.reverse().forEach(aptdo => {
      ordenNumber += '.' + aptdo.orden;
    });
    return apartado?.bloque?.orden + ordenNumber + '.' + apartado?.orden + ' ' + apartado?.nombre;
  } else {
    return null;
  }

}

function getSubapartados(apartado: IApartado, apartados: IApartado[]): IApartado[] {
  apartados.push(apartado);
  if (apartado?.padre) {
    return getSubapartados(apartado.padre, apartados);
  }
  return apartados;
}

@Pipe({
  name: 'bloqueApartado'
})
export class BloqueApartadoPipe implements PipeTransform {

  transform(apartado: IApartado): string {
    if (apartado) {
      return this.getApartadosNombre(apartado);
    }
  }

  private getApartadosNombre(apartado?: IApartado): string {
    let ordenNumber = '';
    if (apartado.bloque.orden === 0) {
      return apartado?.padre ? apartado?.padre.nombre : (apartado?.nombre);
    } else if (apartado.padre) {
      const apartados = getSubapartados(apartado.padre, []);
      apartados.reverse().forEach(aptdo => {
        ordenNumber += '.' + aptdo.orden;
      });
      return apartado?.bloque?.orden + ordenNumber + '.' + apartado?.orden + ' ' + apartado?.nombre;
    } else {
      return apartado?.bloque?.orden + '.' + apartado?.orden + ' ' + apartado?.nombre;
    }
  }

}
