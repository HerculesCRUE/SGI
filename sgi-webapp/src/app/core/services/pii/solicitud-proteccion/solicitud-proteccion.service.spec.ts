import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SolicitudProteccionService } from './solicitud-proteccion.service';

describe('SolicitudProteccionService', () => {
  let service: SolicitudProteccionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudProteccionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
