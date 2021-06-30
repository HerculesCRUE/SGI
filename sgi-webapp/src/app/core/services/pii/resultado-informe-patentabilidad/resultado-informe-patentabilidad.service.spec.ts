import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ResultadoInformePatentabilidadService } from './resultado-informe-patentabilidad.service';

describe('ResultadoInformePatentabilidadService', () => {
  let service: ResultadoInformePatentabilidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ResultadoInformePatentabilidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
