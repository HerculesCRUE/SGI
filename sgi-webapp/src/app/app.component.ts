import { Component } from '@angular/core';
import { ConfigService } from '@core/services/config.service';

@Component({
  selector: 'sgi-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {

  constructor(private configService: ConfigService) {
  }
}
