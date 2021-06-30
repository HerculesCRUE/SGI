import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ConvocatoriaEntidadGestoraService } from './convocatoria-entidad-gestora.service';

describe('ConvocatoriaEntidadGestoraService', () => {
  let service: ConvocatoriaEntidadGestoraService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ],
      providers: [
      ],
    });
    service = TestBed.inject(ConvocatoriaEntidadGestoraService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
