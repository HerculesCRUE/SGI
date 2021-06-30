import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { SolicitudHitoService } from './solicitud-hito.service';

describe('SolicitudHitoService', () => {
  let service: SolicitudHitoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(SolicitudHitoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
