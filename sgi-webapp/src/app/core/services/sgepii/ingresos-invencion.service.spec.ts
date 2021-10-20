import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { IngresosInvencionService } from './ingresos-invencion.service';

describe('IngresosInvencionService', () => {
  let service: IngresosInvencionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
      ]
    });
    service = TestBed.inject(IngresosInvencionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
