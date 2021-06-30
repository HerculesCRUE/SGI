import { TestBed } from '@angular/core/testing';

import { DocumentoService } from './documento.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';

describe('DocumentoService', () => {
  let service: DocumentoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(DocumentoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
