import { HttpClient, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { SgiAuthHttpInterceptor } from './auth-http-interceptor';
import { SgiAuthService } from './auth.service';

const fakeEndpoint = 'http://localhost:8080/fake';

describe(`AuthHttpInterceptor`, () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;
  let authServiceMock: jasmine.SpyObj<SgiAuthService>;

  beforeEach(waitForAsync(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getToken', 'isAuthenticated', 'isProtectedRequest']);
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [
        {
          provide: HTTP_INTERCEPTORS,
          useClass: SgiAuthHttpInterceptor,
          multi: true,
        },
        {
          provide: SgiAuthService,
          useValue: authServiceSpy
        }
      ],
    }).compileComponents();

    authServiceMock = TestBed.inject(SgiAuthService) as any;
    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  }));

  afterEach(() => {
    // then: verify that there are not pending http calls
    httpMock.verify();
  });

  it('should add an Authorization header for protected resource with right value', () => {
    // AuthService responses
    authServiceMock.isAuthenticated.and.returnValue(true);
    authServiceMock.getToken.and.returnValue(of('TOKEN'));
    authServiceMock.isProtectedRequest.and.returnValue(true);

    httpClient.get(fakeEndpoint).subscribe(response => {
      expect(response).toBeTruthy();
    });

    // then: the right backend API is called
    const req = httpMock.expectOne(`${fakeEndpoint}`, 'GET to API');

    expect(req.request.headers.has('Authorization')).toEqual(true);
    expect(req.request.headers.get('Authorization')).toBe('Bearer TOKEN');

    // check for protected request should be called 1 time
    expect(authServiceMock.isProtectedRequest).toHaveBeenCalledTimes(1);

    // “fire” the request with the mocked result
    req.flush({});
  });

  it('should NOT add an Authorization header for a no protected resource', () => {
    // AuthService responses
    authServiceMock.isAuthenticated.and.returnValue(true);
    authServiceMock.getToken.and.returnValue(of('TOKEN'));
    authServiceMock.isProtectedRequest.and.returnValue(false);

    httpClient.get(fakeEndpoint).subscribe(response => {
      expect(response).toBeTruthy();
    });

    // then: the right backend API is called
    const req = httpMock.expectOne(`${fakeEndpoint}`, 'GET to API');

    // header should not be added
    expect(req.request.headers.has('Authorization')).toEqual(false);

    // check for protected request should be called 1 time
    expect(authServiceMock.isProtectedRequest).toHaveBeenCalledTimes(1);

    // “fire” the request with the mocked result
    req.flush({});
  });

});
