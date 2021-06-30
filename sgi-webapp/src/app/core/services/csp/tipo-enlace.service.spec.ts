
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoEnlaceService } from './tipo-enlace.service';

describe('TipoEnlaceService', () => {
  let service: TipoEnlaceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoEnlaceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

