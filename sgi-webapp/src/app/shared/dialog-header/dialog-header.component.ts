import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'sgi-dialog-header',
  templateUrl: './dialog-header.component.html',
  styleUrls: ['./dialog-header.component.scss']
})
export class DialogHeaderComponent {
  @Input()
  title: string;

  @Output()
  closeClick: EventEmitter<void> = new EventEmitter();

}
