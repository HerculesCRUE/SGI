import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ConfiguracionSolicitudService } from './configuracion-solicitud.service';

describe('ConfiguracionSolicitudService', () => {
  let service: ConfiguracionSolicitudService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConfiguracionSolicitudService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
