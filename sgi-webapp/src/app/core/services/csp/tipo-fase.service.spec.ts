import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoFaseService } from './tipo-fase.service';

describe('TipoFaseService', () => {
  let service: TipoFaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoFaseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

