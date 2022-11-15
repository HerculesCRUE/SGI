import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConfiguracionSolicitudPublicService } from './configuracion-solicitud-public.service';

describe('ConfiguracionSolicitudPublicService', () => {
  let service: ConfiguracionSolicitudPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConfiguracionSolicitudPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
