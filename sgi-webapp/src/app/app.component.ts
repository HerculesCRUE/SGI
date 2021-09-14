import { Component, Inject } from '@angular/core';
import { ConfigService } from '@core/services/config.service';
import { TIME_ZONE } from '@core/time-zone';

@Component({
  selector: 'sgi-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {

  constructor(
    private configService: ConfigService,
    @Inject(TIME_ZONE) timeZone: any
  ) { }
}
