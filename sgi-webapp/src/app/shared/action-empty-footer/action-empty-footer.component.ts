import { Component } from '@angular/core';
import { FooterComponent } from '@core/component/footer.component';

@Component({
  selector: 'sgi-action-empty-footer',
  templateUrl: './action-empty-footer.component.html',
  styleUrls: ['./action-empty-footer.component.scss'],
  providers: [{
    provide: FooterComponent,
    useExisting: ActionEmptyFooterComponent
  }]
})
export class ActionEmptyFooterComponent extends FooterComponent { }
