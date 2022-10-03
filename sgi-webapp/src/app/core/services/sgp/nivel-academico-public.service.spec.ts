import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { NivelAcademicoPublicService } from './nivel-academico-public.service';

describe('NivelAcademicoPublicService', () => {
  let service: NivelAcademicoPublicService;

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
    service = TestBed.inject(NivelAcademicoPublicService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
