import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CodigoEconomicoGastoPublicService } from './codigo-economico-gasto-public.service';

describe('CodigoEconomicoGastoPublicService', () => {
  let service: CodigoEconomicoGastoPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(CodigoEconomicoGastoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
