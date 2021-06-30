import { TestBed } from '@angular/core/testing';

import { ProyectoSocioPeriodoPagoService } from './proyecto-socio-periodo-pago.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('ProyectoSocioPeriodoPagoService', () => {
  let service: ProyectoSocioPeriodoPagoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(ProyectoSocioPeriodoPagoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
