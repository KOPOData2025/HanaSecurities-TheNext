const API_BASE_URL = '/api';


function isElectron(): boolean {
  return navigator.userAgent.toLowerCase().includes('electron');
}


function getRpId(serverRpId?: string): string {
  if (isElectron()) {
    console.log('[WebAuthn] Electron 환경 감지: RP ID를 localhost로 설정');
    return 'localhost';
  }
  return serverRpId || window.location.hostname;
}


function base64UrlEncode(buffer: ArrayBuffer): string {
  const bytes = new Uint8Array(buffer);
  let binary = '';
  for (let i = 0; i < bytes.byteLength; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return btoa(binary)
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '');
}

function base64UrlDecode(base64url: string): ArrayBuffer {
  const base64 = base64url
    .replace(/-/g, '+')
    .replace(/_/g, '/');
  const padding = '='.repeat((4 - base64.length % 4) % 4);
  const binary = atob(base64 + padding);
  const bytes = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    bytes[i] = binary.charCodeAt(i);
  }
  return bytes.buffer;
}

function stringToArrayBuffer(str: string): ArrayBuffer {
  const encoder = new TextEncoder();
  return encoder.encode(str);
}





export interface RegisterStartRequest {
  userName: string;
  mobileNo: string;
  gender: string;
  birth: string;
  email: string;
  address: string;
  secondaryPassword: string;
}

export interface RegisterStartResponse {
  challenge: string;
  rpId: string;
  rpName: string;
  userId: number;
  userName: string;
  timeout: number;
}

export interface RegisterFinishRequest {
  userId: number;
  credentialId: string;
  publicKey: string;
  attestationObject: string;
  clientDataJSON: string;
}

export async function registerStart(request: RegisterStartRequest): Promise<RegisterStartResponse> {
  const response = await fetch(`${API_BASE_URL}/register/start`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || '회원가입 시작 실패');
  }

  return response.json();
}

export async function registerWithFingerprint(request: RegisterStartRequest): Promise<string> {
  
  const startResponse = await registerStart(request);

  
  if (isElectron()) {
    console.log('[WebAuthn] Electron 환경에서 WebAuthn 실행');
    console.log('[WebAuthn] RP ID:', getRpId(startResponse.rpId));

    
    try {
      const isAvailable = await window.PublicKeyCredential?.isUserVerifyingPlatformAuthenticatorAvailable();
      console.log('[WebAuthn] Platform Authenticator 사용 가능:', isAvailable);
      if (!isAvailable) {
        console.warn('[WebAuthn] Platform Authenticator를 사용할 수 없습니다. 외부 인증기를 시도합니다.');
      }
    } catch (e) {
      console.warn('[WebAuthn] Platform Authenticator 확인 실패:', e);
    }
  }

  
  const credential = await navigator.credentials.create({
    publicKey: {
      challenge: base64UrlDecode(startResponse.challenge),
      rp: {
        id: getRpId(startResponse.rpId),  
        name: startResponse.rpName,
      },
      user: {
        id: stringToArrayBuffer(startResponse.userId.toString()),
        name: startResponse.userId.toString(),
        displayName: startResponse.userName,
      },
      pubKeyCredParams: [
        { alg: -7, type: 'public-key' },   
        { alg: -257, type: 'public-key' }, 
      ],
      authenticatorSelection: isElectron() ? {
        
        userVerification: 'discouraged',  
        residentKey: 'discouraged',       
        
      } : {
        
        authenticatorAttachment: 'platform',
        userVerification: 'required',
      },
      timeout: startResponse.timeout,
      attestation: 'none',  
    },
  }) as PublicKeyCredential;

  if (!credential) {
    throw new Error('지문 등록이 취소되었습니다.');
  }

  
  const response = credential.response as AuthenticatorAttestationResponse;
  const credentialId = base64UrlEncode(credential.rawId);
  const attestationObject = base64UrlEncode(response.attestationObject);
  const clientDataJSON = base64UrlEncode(response.clientDataJSON);

  
  const publicKey = attestationObject; 

  
  const finishRequest: RegisterFinishRequest = {
    userId: startResponse.userId,
    credentialId,
    publicKey,
    attestationObject,
    clientDataJSON,
  };

  const finishResponse = await fetch(`${API_BASE_URL}/register/finish`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(finishRequest),
  });

  if (!finishResponse.ok) {
    const error = await finishResponse.text();
    throw new Error(error || '회원가입 완료 실패');
  }

  return finishResponse.text();
}





export interface LoginStartRequest {
  mobileNo: string;
}

export interface LoginStartResponse {
  challenge: string;
  allowCredentials: Array<{
    id: string;
    type: string;
    transports: string[];
  }>;
  timeout: number;
}

export interface LoginFinishRequest {
  mobileNo: string;
  credentialId: string;
  authenticatorData: string;
  clientDataJSON: string;
  signature: string;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  accessToken: string;
  userId: number;
  userName: string;
  expiresIn: number;
}

export async function loginStart(request: LoginStartRequest): Promise<LoginStartResponse> {
  const response = await fetch(`${API_BASE_URL}/login/start`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || '로그인 시작 실패');
  }

  return response.json();
}

export async function loginWithFingerprint(mobileNo: string): Promise<LoginResponse> {
  
  const startResponse = await loginStart({ mobileNo });

  
  if (isElectron()) {
    console.log('[WebAuthn] Electron 환경에서 로그인 시도');
    console.log('[WebAuthn] RP ID:', getRpId());

    
    try {
      const isAvailable = await window.PublicKeyCredential?.isUserVerifyingPlatformAuthenticatorAvailable();
      console.log('[WebAuthn] Platform Authenticator 사용 가능:', isAvailable);
      if (!isAvailable) {
        console.warn('[WebAuthn] Platform Authenticator를 사용할 수 없습니다. 외부 인증기를 시도합니다.');
      }
    } catch (e) {
      console.warn('[WebAuthn] Platform Authenticator 확인 실패:', e);
    }
  }

  
  console.log('[WebAuthn] credentials.get() 호출 시작...');

  const credentialOptions = {
    challenge: base64UrlDecode(startResponse.challenge),
    rpId: getRpId(),
    allowCredentials: startResponse.allowCredentials.map((cred) => ({
      id: base64UrlDecode(cred.id),
      type: cred.type as PublicKeyCredentialType,
      transports: cred.transports as AuthenticatorTransport[],
    })),
    userVerification: isElectron() ? 'discouraged' : 'required',
    timeout: 60000,  
  };

  console.log('[WebAuthn] Credential Options:', {
    rpId: credentialOptions.rpId,
    allowCredentialsCount: credentialOptions.allowCredentials.length,
    userVerification: credentialOptions.userVerification,
    timeout: credentialOptions.timeout,
  });

  try {
    const assertion = await navigator.credentials.get({
      publicKey: credentialOptions,
    }) as PublicKeyCredential;

    console.log('[WebAuthn] credentials.get() 성공!', assertion);

    if (!assertion) {
      throw new Error('지문 인증이 취소되었습니다.');
    }

    
    const response = assertion.response as AuthenticatorAssertionResponse;
    const credentialId = base64UrlEncode(assertion.rawId);
    const authenticatorData = base64UrlEncode(response.authenticatorData);
    const clientDataJSON = base64UrlEncode(response.clientDataJSON);
    const signature = base64UrlEncode(response.signature);

    
    const finishRequest: LoginFinishRequest = {
      mobileNo,
      credentialId,
      authenticatorData,
      clientDataJSON,
      signature,
    };

    const finishResponse = await fetch(`${API_BASE_URL}/login/finish`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(finishRequest),
    });

    if (!finishResponse.ok) {
      const error = await finishResponse.text();
      throw new Error(error || '로그인 완료 실패');
    }

    return finishResponse.json();
  } catch (error) {
    console.error('[WebAuthn] credentials.get() 실패:', error);
    throw error;
  }
}





export interface VerifySecondaryPasswordRequest {
  userId: number;
  secondaryPassword: string;
}

export interface VerifySecondaryPasswordResponse {
  success: boolean;
  message: string;
}

/**
 * 2차 비밀번호 검증
 * @param userId 사용자 ID
 * @param secondaryPassword 2차 비밀번호 (4자리)
 * @returns 검증 결과
 */
export async function verifySecondaryPassword(
  userId: number,
  secondaryPassword: string
): Promise<VerifySecondaryPasswordResponse> {
  const request: VerifySecondaryPasswordRequest = {
    userId,
    secondaryPassword,
  };

  const response = await fetch(`${API_BASE_URL}/verify-secondary-password`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error || '2차 비밀번호 검증 실패');
  }

  return response.json();
}
