import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { TipoDocumentoPublicService } from './tipo-documento-public.service';

describe('TipoDocumentoPublicService', () => {
  let service: TipoDocumentoPublicService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        LoggerTestingModule,
        SgiAuthModule
      ],
      providers: [
        SgiAuthService
      ],
    });
    service = TestBed.inject(TipoDocumentoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
