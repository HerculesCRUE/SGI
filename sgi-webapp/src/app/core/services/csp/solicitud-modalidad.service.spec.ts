
import { TestBed } from '@angular/core/testing';

import { SolicitudModalidadService } from './solicitud-modalidad.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('SolicitudModalidadService', () => {
  let service: SolicitudModalidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudModalidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
