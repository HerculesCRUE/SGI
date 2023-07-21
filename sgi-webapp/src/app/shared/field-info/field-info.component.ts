import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { Component, Input } from '@angular/core';
import { ThemePalette } from '@angular/material/core';

@Component({
  selector: 'sgi-field-info',
  templateUrl: './field-info.component.html',
  styleUrls: ['./field-info.component.scss']
})
export class FieldInfoComponent {

  @Input()
  get message(): string {
    return this._message;
  }
  set message(value: string) {
    this._message = value;
  }
  // tslint:disable-next-line: variable-name
  private _message = '';

  @Input()
  get visible(): boolean {
    return this._visible;
  }
  set visible(value: boolean) {
    this._visible = coerceBooleanProperty(value);
  }
  // tslint:disable-next-line: variable-name
  private _visible = false;

  @Input()
  get color(): ThemePalette {
    return this._color;
  }
  set color(value: ThemePalette) {
    this._color = value;
  }
  // tslint:disable-next-line: variable-name
  private _color: ThemePalette = 'primary';

  @Input()
  get icon(): string {
    return this._icon;
  }
  set icon(value: string) {
    this._icon = value;
  }
  // tslint:disable-next-line: variable-name
  private _icon = 'info';

  constructor() { }
}
