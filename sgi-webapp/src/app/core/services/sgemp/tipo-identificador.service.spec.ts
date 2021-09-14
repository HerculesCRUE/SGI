import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { TipoIdentificadorService } from '../sgemp/tipo-identificador.service';

describe('TipoIdentificadorService', () => {
  let service: TipoIdentificadorService;

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
    service = TestBed.inject(TipoIdentificadorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
