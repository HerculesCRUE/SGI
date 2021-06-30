import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { FuenteFinanciacionService } from './fuente-financiacion.service';

describe('FuenteFinanciacionService', () => {
  let service: FuenteFinanciacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(FuenteFinanciacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
