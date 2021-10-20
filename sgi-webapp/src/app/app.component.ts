import { Component, Inject, OnInit } from '@angular/core';
import { Meta } from '@angular/platform-browser';
import { ConfigService } from '@core/services/config.service';
import { TIME_ZONE } from '@core/time-zone';

@Component({
  selector: 'sgi-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {

  constructor(
    private metaService: Meta,
    private configService: ConfigService,
    @Inject(TIME_ZONE) timeZone: any
  ) { }

  ngOnInit(): void {
    this.metaService.addTags([
      { name: 'app.version', content: this.configService.getConfig()['app.version'] },
      { name: 'build.time', content: this.configService.getConfig()['build.time'] }
    ]);
  }
}
