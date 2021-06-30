
import { TestBed } from '@angular/core/testing';

import { SolicitudService } from './solicitud.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('SolicitudService', () => {
  let service: SolicitudService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
