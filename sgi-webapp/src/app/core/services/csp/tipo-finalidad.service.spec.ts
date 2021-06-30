import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoFinalidadService } from './tipo-finalidad.service';

describe('TipoFinalidadService', () => {
  let service: TipoFinalidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoFinalidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
