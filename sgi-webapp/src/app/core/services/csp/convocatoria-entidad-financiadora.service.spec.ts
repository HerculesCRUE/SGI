import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ConvocatoriaEntidadFinanciadoraService } from './convocatoria-entidad-financiadora.service';

describe('ConvocatoriaEntidadFinanciadoraService', () => {
  let service: ConvocatoriaEntidadFinanciadoraService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ConvocatoriaEntidadFinanciadoraService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
