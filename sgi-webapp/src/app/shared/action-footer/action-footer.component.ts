import { Component, Input } from '@angular/core';
import { FooterComponent } from '@core/component/footer.component';

@Component({
  selector: 'sgi-action-footer',
  templateUrl: './action-footer.component.html',
  styleUrls: ['./action-footer.component.scss'],
  providers: [{
    provide: FooterComponent,
    useExisting: ActionFooterComponent
  }]
})
export class ActionFooterComponent extends FooterComponent {

  @Input() texto: string;

}
