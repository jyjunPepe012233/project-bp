const permissions = (() => {
  let _accountId = null;
  let _devPerms = [];
  let _gamePerms = [];
  let _developers = [];

  async function load() {
    const [permsRes, devsRes] = await Promise.all([
      api.getMyPermissions(),
      api.getDeveloperList(),
    ]);
    _devPerms = permsRes.data?.developerPermissions ?? [];
    _gamePerms = permsRes.data?.gamePermissions ?? [];
    _developers = devsRes.data ?? [];
    _accountId = store.getAccountId();
  }

  function clear() {
    _accountId = null;
    _devPerms = [];
    _gamePerms = [];
    _developers = [];
  }

  function _isRoot(developerId) {
    if (developerId == null) return _developers.some(d => d.rootAccountId === _accountId);
    return _developers.some(d => d.id === developerId && d.rootAccountId === _accountId);
  }

  function _hasDevPerm(developerId, ...types) {
    if (developerId == null) return _devPerms.some(p => p.permissions.some(t => types.includes(t)));
    return _devPerms.some(p => p.developerId === developerId && p.permissions.some(t => types.includes(t)));
  }

  function _hasGamePerm(gameId, ...types) {
    if (gameId == null) return _gamePerms.some(p => p.permissions.some(t => types.includes(t)));
    return _gamePerms.some(p => p.gameId === gameId && p.permissions.some(t => types.includes(t)));
  }

  // -- Sidebar visibility --

  function canViewDevelopers() {
    return _isRoot() || _hasDevPerm(null, 'ADMIN', 'PUBLISHER');
  }

  function canViewGames() {
    return _isRoot() || _hasDevPerm(null, 'ADMIN', 'PUBLISHER') || _hasGamePerm(null, 'ADMIN', 'PRIMARY_WRITE', 'MAINTAIN');
  }

  function canCreateGame() {
    return _isRoot() || _hasDevPerm(null, 'ADMIN', 'PUBLISHER');
  }

  function canViewPatches() { return canViewGames(); }

  function canCreatePatch() {
    return _isRoot() || _hasDevPerm(null, 'ADMIN', 'PUBLISHER') || _hasGamePerm(null, 'ADMIN', 'MAINTAIN');
  }

  function canUploadCatalog() { return canCreatePatch(); }
  function canViewBundles() { return canViewGames(); }
  function canUploadBundle() { return canCreatePatch(); }

  function canManageAccounts() {
    return _isRoot() || _hasDevPerm(null, 'ADMIN') || _hasGamePerm(null, 'ADMIN');
  }

  // -- Per-resource checks --

  function canWriteDeveloper(developerId) {
    return _isRoot(developerId) || _hasDevPerm(developerId, 'ADMIN');
  }

  function canDeleteDeveloper(developerId) {
    return _isRoot(developerId);
  }

  function canWriteGame(developerId, gameId) {
    return _isRoot(developerId) || _hasDevPerm(developerId, 'ADMIN', 'PUBLISHER') || _hasGamePerm(gameId, 'ADMIN', 'PRIMARY_WRITE');
  }

  function canDeleteGame(developerId) {
    return _isRoot(developerId) || _hasDevPerm(developerId, 'ADMIN');
  }

  function canWritePatch(developerId, gameId) {
    return _isRoot(developerId) || _hasDevPerm(developerId, 'ADMIN', 'PUBLISHER') || _hasGamePerm(gameId, 'ADMIN', 'MAINTAIN');
  }

  return {
    load, clear,
    canViewDevelopers, canViewGames, canCreateGame,
    canViewPatches, canCreatePatch, canUploadCatalog,
    canViewBundles, canUploadBundle, canManageAccounts,
    canWriteDeveloper, canDeleteDeveloper, canWriteGame, canDeleteGame, canWritePatch,
  };
})();
