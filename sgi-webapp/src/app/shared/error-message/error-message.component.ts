import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-error-message',
  templateUrl: './error-message.component.html',
  styleUrls: ['./error-message.component.scss']
})
export class ErrorMessageComponent {

  @Input()
  get message(): string {
    return this._message;
  }
  set message(value: string) {
    this._message = value;
  }
  // tslint:disable-next-line: variable-name
  private _message = '';

  constructor() { }
}
