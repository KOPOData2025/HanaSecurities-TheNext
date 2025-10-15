


interface PasskeyAuthProps {
  onSuccess?: () => void;
  onError?: (error: Error) => void;
}


const AUTH_SESSION_KEY = 'webauthn_authenticated';
const AUTH_EXPIRY_KEY = 'webauthn_auth_expiry';


const isAuthenticated = (): boolean => {
  const authenticated = sessionStorage.getItem(AUTH_SESSION_KEY);
  const expiry = sessionStorage.getItem(AUTH_EXPIRY_KEY);
  
  if (authenticated === 'true' && expiry) {
    const expiryTime = parseInt(expiry);
    if (Date.now() < expiryTime) {
      return true;
    }
  }
  
  return false;
};


const setAuthenticated = (duration: number = 3600000) => { 
  sessionStorage.setItem(AUTH_SESSION_KEY, 'true');
  sessionStorage.setItem(AUTH_EXPIRY_KEY, (Date.now() + duration).toString());
};


export const clearAuthentication = () => {
  sessionStorage.removeItem(AUTH_SESSION_KEY);
  sessionStorage.removeItem(AUTH_EXPIRY_KEY);
};


const base64ToArrayBuffer = (base64: string): ArrayBuffer => {
  const binaryString = window.atob(base64);
  const bytes = new Uint8Array(binaryString.length);
  for (let i = 0; i < binaryString.length; i++) {
    bytes[i] = binaryString.charCodeAt(i);
  }
  return bytes.buffer;
};

const arrayBufferToBase64 = (buffer: ArrayBuffer): string => {
  const bytes = new Uint8Array(buffer);
  let binary = '';
  for (let i = 0; i < bytes.byteLength; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return window.btoa(binary);
};


const STORAGE_KEY = 'webauthn_credentials';


const isAndroid = (): boolean => {
  const userAgent = navigator.userAgent.toLowerCase();
  return userAgent.includes('android');
};


const isIOS = (): boolean => {
  const userAgent = navigator.userAgent.toLowerCase();
  return userAgent.includes('iphone') || userAgent.includes('ipad');
};


const isElectron = (): boolean => {
  return navigator.userAgent.toLowerCase().includes('electron');
};


const getRpId = (): string => {
  if (isElectron()) {
    console.log('[WebAuthn] Electron 환경 감지: RP ID를 localhost로 설정');
    return 'localhost';
  }
  return window.location.hostname;
};


export const registerPasskey = async ({ onSuccess, onError }: PasskeyAuthProps = {}) => {
  try {
    
    if (!window.PublicKeyCredential) {
      throw new Error('WebAuthn is not supported in this browser');
    }

    
    const challenge = new Uint8Array(32);
    window.crypto.getRandomValues(challenge);

    
    const userId = new Uint8Array(16);
    window.crypto.getRandomValues(userId);

    
    const authenticatorSelection: AuthenticatorSelectionCriteria = isAndroid() ? {
      
      userVerification: 'preferred', 
      residentKey: 'preferred',      
      requireResidentKey: false,     
    } : {
      
      authenticatorAttachment: 'platform',
      userVerification: 'required',
      residentKey: 'required',
      requireResidentKey: true,
    };

    
    const createCredentialOptions: PublicKeyCredentialCreationOptions = {
      challenge: challenge,
      rp: {
        name: '하나증권',
        
        id: getRpId(),
      },
      user: {
        id: userId,
        name: 'user@example.com',
        displayName: '사용자',
      },
      pubKeyCredParams: [
        { alg: -7, type: 'public-key' },   
        { alg: -257, type: 'public-key' }, 
      ],
      authenticatorSelection: authenticatorSelection,
      timeout: 60000,
      attestation: 'none', 
    };

    console.log('Requesting credential creation on', isAndroid() ? 'Android' : 'Desktop/iOS');
    
    
    const credential = await navigator.credentials.create({
      publicKey: createCredentialOptions,
    }) as PublicKeyCredential;

    if (!credential) {
      throw new Error('Failed to create credential');
    }

    console.log('Credential created successfully:', credential);

    
    const storedCredentials = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
    storedCredentials.push({
      id: arrayBufferToBase64(credential.rawId),
      type: credential.type,
    });
    localStorage.setItem(STORAGE_KEY, JSON.stringify(storedCredentials));

    
    setAuthenticated();
    
    console.log('Passkey registered successfully');
    onSuccess?.();
  } catch (error) {
    console.error('Passkey registration error:', error);
    onError?.(error as Error);
  }
};


export const authenticateWithPasskey = async ({ onSuccess, onError }: PasskeyAuthProps = {}) => {
  try {
    
    
    
    
    
    

    
    if (!window.PublicKeyCredential) {
      
      if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
        console.log('Development mode: Using mock authentication');
        const result = confirm('지문 인증을 시뮬레이션합니다.\n계속하시겠습니까?');
        if (result) {
          console.log('Mock authentication successful');
          setAuthenticated();
          onSuccess?.();
        } else {
          throw new Error('사용자가 인증을 취소했습니다');
        }
        return;
      }
      throw new Error('WebAuthn is not supported in this browser');
    }

    
    const challenge = new Uint8Array(32);
    window.crypto.getRandomValues(challenge);

    
    const storedCredentials = JSON.parse(localStorage.getItem(STORAGE_KEY) || '[]');
    
    
    if (storedCredentials.length === 0) {
      console.log('No passkey found, registering new one...');
      await registerPasskey({
        onSuccess: async () => {
          
          onSuccess?.();
        },
        onError
      });
      return;
    }

    
    const allowCredentials = storedCredentials.map((cred: any) => ({
      id: base64ToArrayBuffer(cred.id),
      type: 'public-key' as PublicKeyCredentialType,
      
      transports: isAndroid() ? 
        ['internal', 'hybrid'] as AuthenticatorTransport[] : 
        ['internal'] as AuthenticatorTransport[],
    }));

    
    const getCredentialOptions: PublicKeyCredentialRequestOptions = {
      challenge: challenge,
      allowCredentials: allowCredentials.length > 0 ? allowCredentials : undefined,
      userVerification: isAndroid() ? 'preferred' : 'required', 
      timeout: 60000,
      rpId: getRpId(),  
    };

    console.log('Requesting authentication on', isAndroid() ? 'Android' : 'Desktop/iOS');

    
    const assertion = await navigator.credentials.get({
      publicKey: getCredentialOptions,
    }) as PublicKeyCredential;

    if (!assertion) {
      throw new Error('Authentication failed');
    }

    console.log('Authentication successful:', assertion);

    
    
    if (assertion.response) {
      
      
      console.log('Passkey authentication successful');
      onSuccess?.();
    } else {
      throw new Error('Invalid authentication response');
    }
  } catch (error) {
    console.error('Passkey authentication error:', error);
    
    
    if (error instanceof Error) {
      if (error.name === 'NotAllowedError') {
        if (isAndroid()) {
          onError?.(new Error('지문 인증이 취소되었습니다. 다시 시도해주세요.'));
        } else {
          onError?.(new Error('인증이 취소되었거나 시간이 초과되었습니다'));
        }
      } else if (error.name === 'NotSupportedError') {
        onError?.(new Error('이 브라우저는 패스키를 지원하지 않습니다'));
      } else if (error.name === 'InvalidStateError') {
        onError?.(new Error('이미 등록된 패스키가 있습니다'));
      } else if (error.name === 'SecurityError') {
        if (isAndroid()) {
          onError?.(new Error('보안 설정을 확인해주세요. HTTPS 연결이 필요합니다.'));
        } else {
          onError?.(error);
        }
      } else {
        onError?.(error);
      }
    } else {
      onError?.(new Error('알 수 없는 오류가 발생했습니다'));
    }
  }
};


export const isPasskeyAvailable = async (): Promise<boolean> => {
  try {
    if (!window.PublicKeyCredential) {
      return false;
    }
    
    
    const available = await PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable();
    console.log('Platform authenticator available:', available, 'on', isAndroid() ? 'Android' : 'Desktop/iOS');
    return available;
  } catch {
    return false;
  }
};


export const clearPasskeys = () => {
  localStorage.removeItem(STORAGE_KEY);
  clearAuthentication();
  console.log('All stored passkeys and authentication state cleared');
};


export const logout = () => {
  clearAuthentication();
  console.log('User logged out');
};

export default { 
  authenticateWithPasskey, 
  registerPasskey, 
  isPasskeyAvailable,
  clearPasskeys,
  logout,
  clearAuthentication
};