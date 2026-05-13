const store = (() => {
  const KEY_ACCESS  = 'bp_access_token';
  const KEY_REFRESH = 'bp_refresh_token';
  const KEY_ACCOUNT_NAME = 'bp_account_name';
  const KEY_ACCOUNT_ID   = 'bp_account_id';

  return {
    getAccessToken()  { return localStorage.getItem(KEY_ACCESS); },
    getRefreshToken() { return localStorage.getItem(KEY_REFRESH); },

    setTokens(accessToken, refreshToken) {
      localStorage.setItem(KEY_ACCESS, accessToken);
      localStorage.setItem(KEY_REFRESH, refreshToken);
    },

    clearTokens() {
      localStorage.removeItem(KEY_ACCESS);
      localStorage.removeItem(KEY_REFRESH);
      localStorage.removeItem(KEY_ACCOUNT_NAME);
      localStorage.removeItem(KEY_ACCOUNT_ID);
    },

    getAccountName() { return localStorage.getItem(KEY_ACCOUNT_NAME); },
    setAccountName(name) { localStorage.setItem(KEY_ACCOUNT_NAME, name); },

    getAccountId() { const v = localStorage.getItem(KEY_ACCOUNT_ID); return v ? Number(v) : null; },
    setAccountId(id) { localStorage.setItem(KEY_ACCOUNT_ID, String(id)); },

    isLoggedIn() { return !!localStorage.getItem(KEY_ACCESS); },
  };
})();
