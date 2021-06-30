import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SectorAplicacionService } from './sector-aplicacion.service';

describe('SectorAplicacionService', () => {
  let service: SectorAplicacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SectorAplicacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
