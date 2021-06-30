import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoHitoService } from './tipo-hito.service';

describe('TipoHitoService', () => {
  let service: TipoHitoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoHitoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
