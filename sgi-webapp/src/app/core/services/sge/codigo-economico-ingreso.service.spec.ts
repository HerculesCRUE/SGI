import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { CodigoEconomicoIngresoService } from './codigo-economico-ingreso.service';

describe('CodigoEconomicoIngresoService', () => {
  let service: CodigoEconomicoIngresoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(CodigoEconomicoIngresoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
