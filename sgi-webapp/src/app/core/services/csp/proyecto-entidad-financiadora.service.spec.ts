import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { ProyectoEntidadFinanciadoraService } from './proyecto-entidad-financiadora.service';

describe('ProyectoEntidadFinanciadoraService', () => {
  let service: ProyectoEntidadFinanciadoraService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoEntidadFinanciadoraService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
