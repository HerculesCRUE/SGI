import { Component, Input } from '@angular/core';
import { IComentario } from '@core/models/eti/comentario';

@Component({
  selector: 'sgi-comentarios-generales-formly',
  templateUrl: './comentarios-generales-formly.component.html',
  styleUrls: ['./comentarios-generales-formly.component.scss']
})
export class ComentariosGeneralesFormlyComponent {

  /**
   * Lista de comentarios generales
   */
  @Input()
  get comentarios(): IComentario[] {
    return this._comentarios;
  }
  set comentarios(value: IComentario[]) {
    this._comentarios = value;
  }
  // tslint:disable-next-line: variable-name
  private _comentarios: IComentario[] = [];

  /**
   * Flag para mostrar el bloque expandido o no. Por defecto true.
   */
  @Input()
  get expanded(): boolean {
    return this._expanded;
  }
  set expanded(value: boolean) {
    this._expanded = value;
  }
  // tslint:disable-next-line: variable-name
  private _expanded = true;

  get titulo(): string {
    return this.comentarios[0]?.apartado.nombre ?? '';
  }

  constructor() {
  }

}
