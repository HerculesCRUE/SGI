import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

import { TipoDocumentoService } from './tipo-documento.service';

describe('TipoDocumentoService', () => {
  let service: TipoDocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(TipoDocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
