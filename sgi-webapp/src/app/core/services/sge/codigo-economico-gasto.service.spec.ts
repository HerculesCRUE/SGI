import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { CodigoEconomicoGastoService } from './codigo-economico-gasto.service';

describe('CodigoEconomicoGastoService', () => {
  let service: CodigoEconomicoGastoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(CodigoEconomicoGastoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
