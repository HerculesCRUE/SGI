import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { InformePatentabilidadService } from './informe-patentabilidad.service';

describe('InformePatentabilidadService', () => {
  let service: InformePatentabilidadService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(InformePatentabilidadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
