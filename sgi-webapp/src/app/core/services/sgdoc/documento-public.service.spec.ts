import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { DocumentoPublicService } from './documento-public.service';

describe('DocumentoPublicService', () => {
  let service: DocumentoPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        BrowserAnimationsModule,
        LoggerTestingModule
      ]
    });
    service = TestBed.inject(DocumentoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
