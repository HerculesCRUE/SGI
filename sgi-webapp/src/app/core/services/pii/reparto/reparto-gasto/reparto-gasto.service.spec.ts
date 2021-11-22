import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { RepartoGastoService } from './reparto-gasto.service';

describe('RepartoGastoService', () => {
  let service: RepartoGastoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(RepartoGastoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
