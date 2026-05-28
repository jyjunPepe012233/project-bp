const API_BASE = 'http://localhost:8080';

const api = (() => {
  let refreshPromise = null;

  function forceLogout() {
    store.clearTokens();
    location.hash = '/';
    router.resolve();
  }

  async function refreshTokens() {
    const refreshToken = store.getRefreshToken();
    if (!refreshToken) throw new Error('No refresh token');

    const res = await fetch(`${API_BASE}/auth/reissue`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });

    const data = await res.json().catch(() => null);
    if (!res.ok) throw new Error(data?.message || 'Token refresh failed');

    store.setTokens(data.data.accessToken, data.data.refreshToken);
    return data.data.accessToken;
  }

  async function handleTokenRefresh() {
    if (!refreshPromise) {
      refreshPromise = refreshTokens().finally(() => { refreshPromise = null; });
    }
    return refreshPromise;
  }

  async function request(method, path, body, auth = false) {
    const headers = { 'Content-Type': 'application/json' };
    if (auth) {
      const token = store.getAccessToken();
      if (token) headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(`${API_BASE}${path}`, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });

    if (res.status === 401 && auth) {
      let newToken;
      try {
        newToken = await handleTokenRefresh();
      } catch {
        forceLogout();
        throw new Error('Session expired. Please log in again.');
      }

      const retryHeaders = { ...headers, 'Authorization': `Bearer ${newToken}` };
      const retryRes = await fetch(`${API_BASE}${path}`, {
        method,
        headers: retryHeaders,
        body: body !== undefined ? JSON.stringify(body) : undefined,
      });
      const retryData = await retryRes.json().catch(() => null);
      if (!retryRes.ok) {
        const msg = retryData?.message || retryData?.error || `HTTP ${retryRes.status}`;
        throw new Error(msg);
      }
      return retryData;
    }

    const data = await res.json().catch(() => null);

    if (!res.ok) {
      const msg = data?.message || data?.error || `HTTP ${res.status}`;
      throw new Error(msg);
    }

    return data;
  }

  async function uploadFile(url, fieldName, file) {
    function buildForm() {
      const form = new FormData();
      form.append(fieldName, file);
      return form;
    }

    const headers = {};
    const token = store.getAccessToken();
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(url, { method: 'POST', headers, body: buildForm() });

    if (res.status === 401) {
      let newToken;
      try {
        newToken = await handleTokenRefresh();
      } catch {
        forceLogout();
        throw new Error('Session expired. Please log in again.');
      }

      const retryRes = await fetch(url, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${newToken}` },
        body: buildForm(),
      });
      const retryData = await retryRes.json().catch(() => null);
      if (!retryRes.ok) throw new Error(retryData?.message || retryData?.error || `HTTP ${retryRes.status}`);
      return retryData;
    }

    const data = await res.json().catch(() => null);
    if (!res.ok) throw new Error(data?.message || data?.error || `HTTP ${res.status}`);
    return data;
  }

  return {
    // Auth
    login(name, password) {
      return request('POST', '/auth/login', { name, password });
    },

    reissue(refreshToken) {
      return request('POST', '/auth/reissue', { refreshToken });
    },

    // Developer
    createDeveloper(developerName, rootAccountName, rootAccountPassword) {
      return request('POST', '/developers', { developerName, rootAccountName, rootAccountPassword });
    },

    getDeveloperList() {
      return request('GET', '/developers', undefined, true);
    },

    getDeveloper(developerId) {
      return request('GET', `/developers/${developerId}`, undefined, true);
    },

    updateDeveloper(developerId, name) {
      return request('PATCH', `/developers/${developerId}`, { name }, true);
    },

    // Game
    getGameList() {
      return request('GET', '/games', undefined, true);
    },

    createGame(developerId, title, description) {
      return request('POST', '/games', { developerId, title, description }, true);
    },

    updateGame(gameId, title, description) {
      return request('PATCH', `/games/${gameId}`, { title, description }, true);
    },

    deleteGame(gameId) {
      return request('DELETE', `/games/${gameId}`, undefined, true);
    },

    // Patch
    getPatchList(gameId) {
      return request('GET', `/games/${gameId}/patches`, undefined, true);
    },

    createPatch(gameId, version, platform, patchNote) {
      return request('POST', `/games/${gameId}/patches`, { version, platform, patchNote }, true);
    },

    updatePatchNote(patchId, patchNote) {
      return request('PATCH', `/patches/${patchId}`, { patchNote }, true);
    },

    deletePatch(patchId) {
      return request('DELETE', `/patches/${patchId}`, undefined, true);
    },

    // Account
    createAccount(name, password, developerAccessPermissions, gameAccessPermissions) {
      return request('POST', '/accounts', { name, password, developerAccessPermissions, gameAccessPermissions }, true);
    },

    getMyAccount() {
      return request('GET', '/accounts/me', undefined, true);
    },

    getAccountList() {
      return request('GET', '/accounts', undefined, true);
    },

    getMyPermissions() {
      return request('GET', '/accounts/me/permissions', undefined, true);
    },

    getAccountPermissions(accountId) {
      return request('GET', `/accounts/${accountId}/permissions`, undefined, true);
    },

    updateAccount(accountId, name) {
      return request('PATCH', `/accounts/${accountId}`, { name }, true);
    },

    updateAccountPassword(accountId, password) {
      return request('PATCH', `/accounts/${accountId}/password`, { password }, true);
    },

    updateDeveloperPermission(accountId, developerId, permissions) {
      return request('PUT', `/developers/${developerId}/permissions/${accountId}`, permissions, true);
    },

    updateGamePermission(accountId, gameId, permissions) {
      return request('PUT', `/games/${gameId}/permissions/${accountId}`, permissions, true);
    },

    // Game - Bundle List (all platforms)
    getGameBundleList(gameId) {
      return request('GET', `/games/${gameId}/bundles`, undefined, true);
    },

    // Patch - Catalog Upload Status
    checkCatalogUploaded(patchId) {
      return request('GET', `/patches/${patchId}/catalog/uploaded`, undefined, true);
    },

    checkCatalogHashUploaded(patchId) {
      return request('GET', `/patches/${patchId}/catalog-hash/uploaded`, undefined, true);
    },

    // Patch - Catalog Upload (multipart/form-data)
    async uploadCatalog(patchId, catalogFile) {
      return uploadFile(`${API_BASE}/patches/${patchId}/catalog`, 'catalog', catalogFile);
    },

    // Patch - Catalog Hash Upload (multipart/form-data)
    async uploadCatalogHash(patchId, catalogHashFile) {
      return uploadFile(`${API_BASE}/patches/${patchId}/catalog-hash`, 'catalogHash', catalogHashFile);
    },

    // Patch - Bundle File List
    getBundleFileList(patchId) {
      return request('GET', `/patches/${patchId}/bundles`, undefined, true);
    },

    // Bundle Upload (multipart/form-data, streaming)
    async uploadBundle(gameId, platform, bundleFile) {
      return uploadFile(`${API_BASE}/games/${gameId}/bundles?platform=${encodeURIComponent(platform)}`, 'bundle', bundleFile);
    },

    deleteBundle(gameId, platform, filename) {
      const query = `platform=${encodeURIComponent(platform)}&filename=${encodeURIComponent(filename)}`;
      return request('DELETE', `/games/${gameId}/bundles?${query}`, undefined, true);
    },

    // Patch - Delete Catalog
    deleteCatalog(patchId) {
      return request('DELETE', `/patches/${patchId}/catalog`, undefined, true);
    },

    // Patch - Delete Catalog Hash
    deleteCatalogHash(patchId) {
      return request('DELETE', `/patches/${patchId}/catalog-hash`, undefined, true);
    },
  };
})();
