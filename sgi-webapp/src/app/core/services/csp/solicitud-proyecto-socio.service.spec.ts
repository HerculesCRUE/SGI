import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SolicitudProyectoSocioService } from './solicitud-proyecto-socio.service';

describe('SolicitudProyectoSocioService', () => {
  let service: SolicitudProyectoSocioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProyectoSocioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
