import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { GastosInvencionService } from './gastos-invencion.service';

describe('GastosInvencionService', () => {
  let service: GastosInvencionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
      ]
    });
    service = TestBed.inject(GastosInvencionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
