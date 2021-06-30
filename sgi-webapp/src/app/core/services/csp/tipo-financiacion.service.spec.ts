import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoFinanciacionService } from './tipo-financiacion.service';

describe('TipoFinanciacionService', () => {
  let service: TipoFinanciacionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoFinanciacionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
