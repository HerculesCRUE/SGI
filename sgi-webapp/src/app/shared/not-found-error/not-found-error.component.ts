import { Component, Input } from '@angular/core';

@Component({
  selector: 'sgi-not-found-error',
  templateUrl: './not-found-error.component.html',
  styleUrls: ['./not-found-error.component.scss']
})
export class NotFoundErrorComponent {
  @Input() message: string;

  constructor() { }

}
