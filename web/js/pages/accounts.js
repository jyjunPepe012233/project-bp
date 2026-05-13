const accountsPage = (() => {
  const DEV_PERM_LABEL  = { ADMIN: 'Admin', PUBLISHER: 'Publisher' };
  const GAME_PERM_LABEL = { ADMIN: 'Admin', PRIMARY_WRITE: 'Primary Write', MAINTAIN: 'Maintain' };

  let _selectedAccount = null;

  function renderList(container) {
    setPageTitle('계정 목록');
    container.innerHTML = `
      <div class="card">
        <div class="card-title">계정 목록</div>
        <div id="accounts-alert" hidden></div>
        <div id="accounts-body">
          <p style="color:var(--text-sub)"><span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span></p>
        </div>
      </div>
    `;
    loadList();
  }

  async function loadList() {
    try {
      const [accountsRes, meRes, devsRes, gamesRes] = await Promise.all([
        api.getAccountList(),
        api.getMyAccount(),
        api.getDeveloperList(),
        api.getGameList(),
      ]);

      const accounts = accountsRes.data ?? [];
      const me       = meRes.data;
      const devMap   = Object.fromEntries((devsRes.data ?? []).map(d => [d.id, d.name]));
      const gameMap  = Object.fromEntries((gamesRes.data ?? []).map(g => [g.id, g.title]));

      // 모든 계정의 권한을 병렬 조회
      const permsResults = await Promise.all(
        accounts.map(a =>
          (a.id === me?.id ? api.getMyPermissions() : api.getAccountPermissions(a.id))
            .then(r => ({ accountId: a.id, perms: r.data }))
            .catch(() => ({ accountId: a.id, perms: null }))
        )
      );
      const permsMap = Object.fromEntries(permsResults.map(r => [r.accountId, r.perms]));

      const body = document.getElementById('accounts-body');
      if (!body) return;

      if (accounts.length === 0) {
        body.innerHTML = `<p style="color:var(--text-sub);font-size:13px">등록된 계정이 없습니다.</p>`;
        return;
      }

      body.innerHTML = accounts.map(a => {
        const isMe = a.id === me?.id;
        const perms = permsMap[a.id];
        return `
          <div class="account-row${isMe ? ' account-row-me' : ''}">
            <div class="account-row-header">
              <span class="account-name">${escapeHtml(a.name)}</span>
              ${isMe ? '<span class="account-badge-me">나</span>' : (permissions.canManageAccounts() ? `<button class="btn-table-action account-edit-btn" data-account-id="${a.id}" style="margin-left:auto">수정</button>` : '')}
            </div>
            ${renderPermissions(perms, devMap, gameMap)}
          </div>
        `;
      }).join('');

      body.querySelectorAll('.account-edit-btn').forEach(btn => {
        btn.addEventListener('click', () => {
          const id = Number(btn.dataset.accountId);
          _selectedAccount = accounts.find(a => a.id === id) ?? null;
          location.hash = '/accounts/edit';
        });
      });
    } catch (err) {
      const alertEl = document.getElementById('accounts-alert');
      if (alertEl) {
        alertEl.className = 'alert alert-error';
        alertEl.textContent = err.message || '목록을 불러오지 못했습니다.';
        alertEl.hidden = false;
      }
      const body = document.getElementById('accounts-body');
      if (body) body.innerHTML = '';
    }
  }

  function renderPermissions(perms, devMap, gameMap) {
    if (!perms) return '';

    const devRows = (perms.developerPermissions ?? []).map(p => `
      <div class="perm-row">
        <span class="perm-subject">${escapeHtml(devMap[p.developerId] ?? `개발사 #${p.developerId}`)}</span>
        <span class="perm-type-label">개발사</span>
        <div class="perm-tags">
          ${p.permissions.map(t => `<span class="perm-tag perm-tag-dev">${DEV_PERM_LABEL[t] ?? t}</span>`).join('')}
        </div>
      </div>
    `).join('');

    const gameRows = (perms.gamePermissions ?? []).map(p => `
      <div class="perm-row">
        <span class="perm-subject">${escapeHtml(gameMap[p.gameId] ?? `게임 #${p.gameId}`)}</span>
        <span class="perm-type-label">게임</span>
        <div class="perm-tags">
          ${p.permissions.map(t => `<span class="perm-tag perm-tag-game">${GAME_PERM_LABEL[t] ?? t}</span>`).join('')}
        </div>
      </div>
    `).join('');

    const hasPerms = devRows || gameRows;
    return `
      <div class="perm-section">
        ${hasPerms
          ? (devRows + gameRows)
          : '<span style="font-size:12px;color:var(--text-sub)">권한 없음</span>'}
      </div>
    `;
  }

  const DEV_PERMS  = ['ADMIN', 'PUBLISHER'];
  const GAME_PERMS = ['ADMIN', 'PRIMARY_WRITE', 'MAINTAIN'];
  const GAME_PERM_SHORT = { ADMIN: 'Admin', PRIMARY_WRITE: 'Primary Write', MAINTAIN: 'Maintain' };
  const DEV_PERM_SHORT  = { ADMIN: 'Admin', PUBLISHER: 'Publisher' };

  function renderNew(container) {
    setPageTitle('계정 생성');
    container.innerHTML = `
      <div class="card">
        <div class="card-title">계정 생성</div>
        <div id="acc-new-alert" hidden></div>
        <form id="acc-new-form">
          <fieldset class="fieldset">
            <legend class="fieldset-legend">기본 정보</legend>
            <div class="form-group">
              <label class="form-label" for="acc-name">계정명 <span style="color:var(--text-sub);font-weight:400">— 서비스 전체에서 고유해야 합니다</span></label>
              <input id="acc-name" class="form-input" type="text" autocomplete="off" placeholder="yejungames-publisher01" />
            </div>
            <div class="form-group">
              <label class="form-label" for="acc-pw">비밀번호</label>
              <input id="acc-pw" class="form-input" type="password" autocomplete="new-password" placeholder="Kp7mX2nQ" />
            </div>
          </fieldset>

          <fieldset class="fieldset">
            <legend class="fieldset-legend">개발사 권한</legend>
            <div class="perm-desc-table">
              <div class="perm-desc-row"><span class="perm-desc-name">Admin</span><span class="perm-desc-text">개발사 이름 수정 / 게임 등록·수정 / 계정 생성·수정 / 개발사·게임 권한 관리 / 패치 생성·노트 수정</span></div>
              <div class="perm-desc-row"><span class="perm-desc-name">Publisher</span><span class="perm-desc-text">게임 등록·수정 / 패치 생성·노트 수정</span></div>
            </div>
            <div id="acc-dev-perms">
              <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
            </div>
          </fieldset>

          <fieldset class="fieldset">
            <legend class="fieldset-legend">게임 권한</legend>
            <div class="perm-desc-table">
              <div class="perm-desc-row"><span class="perm-desc-name">Admin</span><span class="perm-desc-text">게임 정보 수정 / 게임 단위 계정 생성·수정 / 게임 권한 관리 / 패치 생성·노트 수정</span></div>
              <div class="perm-desc-row"><span class="perm-desc-name">Primary Write</span><span class="perm-desc-text">게임 정보 수정</span></div>
              <div class="perm-desc-row"><span class="perm-desc-name">Maintain</span><span class="perm-desc-text">패치 생성·노트 수정</span></div>
            </div>
            <div id="acc-game-perms">
              <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
            </div>
          </fieldset>

          <button id="acc-new-btn" class="btn btn-primary" type="submit" style="width:auto;padding:9px 24px">생성</button>
        </form>
      </div>
    `;

    loadPermissionTargets();

    document.getElementById('acc-new-form').addEventListener('submit', async (e) => {
      e.preventDefault();

      const name     = document.getElementById('acc-name').value.trim();
      const password = document.getElementById('acc-pw').value;

      const vErr = validation.check(
        validation.required(name, '계정명을 입력해주세요.'),
        validation.pattern(name, 'ACCOUNT_NAME'),
        validation.required(password, '비밀번호를 입력해주세요.'),
        validation.pattern(password, 'PASSWORD'),
      );
      if (vErr) { setNewAlert('error', vErr); return; }

      const developerAccessPermissions = collectPermissions('dev');
      const gameAccessPermissions      = collectPermissions('game');

      setNewLoading(true);
      setNewAlert('');

      try {
        const res = await api.createAccount(name, password, developerAccessPermissions, gameAccessPermissions);
        setNewAlert('success', `계정 "${res.data.name}"이(가) 생성됐습니다.`);
        document.getElementById('acc-new-form').reset();
      } catch (err) {
        setNewAlert('error', err.message || '생성에 실패했습니다.');
      } finally {
        setNewLoading(false);
      }
    });
  }

  async function loadPermissionTargets() {
    try {
      const [devsRes, gamesRes] = await Promise.all([api.getDeveloperList(), api.getGameList()]);
      const devs  = devsRes.data ?? [];
      const games = gamesRes.data ?? [];
      const devMap = Object.fromEntries(devs.map(d => [d.id, d.name]));

      const devContainer  = document.getElementById('acc-dev-perms');
      const gameContainer = document.getElementById('acc-game-perms');
      if (!devContainer || !gameContainer) return;

      if (devs.length === 0) {
        devContainer.innerHTML = `<p style="font-size:13px;color:var(--text-sub)">등록된 개발사가 없습니다.</p>`;
      } else {
        devContainer.innerHTML = devs.map(d => `
          <div class="perm-assign-row">
            <span class="perm-assign-subject">${escapeHtml(d.name)}</span>
            <div class="perm-assign-checks">
              ${DEV_PERMS.map(p => `
                <label class="perm-check-item">
                  <input type="checkbox" data-type="dev" data-id="${d.id}" data-perm="${p}" />
                  <span>${DEV_PERM_SHORT[p]}</span>
                </label>
              `).join('')}
            </div>
          </div>
        `).join('');
      }

      if (games.length === 0) {
        gameContainer.innerHTML = `<p style="font-size:13px;color:var(--text-sub)">등록된 게임이 없습니다.</p>`;
      } else {
        // 개발사별 그룹
        const groups = {};
        games.forEach(g => {
          const key = devMap[g.developerId] ?? `개발사 #${g.developerId}`;
          (groups[key] ??= []).push(g);
        });

        gameContainer.innerHTML = Object.entries(groups).map(([devName, devGames]) => `
          <div class="perm-assign-group">
            <div class="perm-assign-group-label">${escapeHtml(devName)}</div>
            ${devGames.map(g => `
              <div class="perm-assign-row">
                <span class="perm-assign-subject">${escapeHtml(g.title)}</span>
                <div class="perm-assign-checks">
                  ${GAME_PERMS.map(p => `
                    <label class="perm-check-item">
                      <input type="checkbox" data-type="game" data-id="${g.id}" data-perm="${p}" />
                      <span>${GAME_PERM_SHORT[p]}</span>
                    </label>
                  `).join('')}
                </div>
              </div>
            `).join('')}
          </div>
        `).join('');
      }
    } catch (err) {
      const devContainer = document.getElementById('acc-dev-perms');
      if (devContainer) devContainer.innerHTML = `<p style="font-size:13px;color:var(--error)">${err.message || '불러오지 못했습니다.'}</p>`;
    }
  }

  function collectPermissions(type) {
    const map = {};
    document.querySelectorAll(`input[data-type="${type}"]:checked`).forEach(el => {
      const id   = Number(el.dataset.id);
      const perm = el.dataset.perm;
      (map[id] ??= []).push(perm);
    });
    const idKey  = type === 'dev' ? 'developerId' : 'gameId';
    return Object.entries(map).map(([id, permissions]) => ({ [idKey]: Number(id), permissions }));
  }

  function setNewAlert(type, msg) {
    const el = document.getElementById('acc-new-alert');
    if (!el) return;
    if (!type) { el.hidden = true; el.textContent = ''; return; }
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.hidden = false;
  }

  function setNewLoading(on) {
    const btn = document.getElementById('acc-new-btn');
    if (!btn) return;
    btn.disabled = on;
    btn.innerHTML = on ? '<span class="spinner"></span>' : '생성';
  }

  // ── 계정 수정 ──────────────────────────────────

  function renderEdit(container) {
    setPageTitle('계정 수정');

    const account = _selectedAccount;

    if (!account) {
      container.innerHTML = `
        <div class="card">
          <div class="card-title">계정 수정</div>
          <p style="color:var(--text-sub);font-size:13px">수정할 계정을 <a href="#/accounts">계정 목록</a>에서 선택해주세요.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = `
      <div class="card" style="margin-bottom:20px">
        <div class="card-title">기본 정보 수정 — ${escapeHtml(account.name)}</div>
        <div id="acc-edit-info-alert" hidden></div>
        <form id="acc-edit-info-form">
          <div class="form-group">
            <label class="form-label" for="acc-edit-name">계정명 <span style="color:var(--text-sub);font-weight:400">— 서비스 전체에서 고유해야 합니다</span></label>
            <input id="acc-edit-name" class="form-input" type="text" value="${escapeHtml(account.name)}" />
          </div>
          <button id="acc-edit-info-btn" class="btn btn-primary" type="submit" style="width:auto;padding:9px 24px">계정명 저장</button>
        </form>
      </div>

      <div class="card" style="margin-bottom:20px">
        <div class="card-title">비밀번호 변경</div>
        <div id="acc-edit-pw-alert" hidden></div>
        <form id="acc-edit-pw-form">
          <div class="form-group">
            <label class="form-label" for="acc-edit-pw">새 비밀번호</label>
            <input id="acc-edit-pw" class="form-input" type="password" autocomplete="new-password" placeholder="변경할 비밀번호 입력" />
          </div>
          <button id="acc-edit-pw-btn" class="btn btn-primary" type="submit" style="width:auto;padding:9px 24px">비밀번호 변경</button>
        </form>
      </div>

      <div class="card">
        <div class="card-title">권한 수정</div>
        <p style="font-size:12px;color:var(--text-sub);margin-bottom:16px">각 항목의 저장 버튼을 누르면 해당 개발사/게임의 권한이 선택한 값으로 덮어씌워집니다.</p>
        <div id="acc-edit-perm-alert" hidden></div>

        <fieldset class="fieldset">
          <legend class="fieldset-legend">개발사 권한</legend>
          <div id="acc-edit-dev-perms">
            <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
          </div>
        </fieldset>

        <fieldset class="fieldset">
          <legend class="fieldset-legend">게임 권한</legend>
          <div id="acc-edit-game-perms">
            <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
          </div>
        </fieldset>
      </div>
    `;

    loadEditPermTargets(account.id);

    // 계정명 저장
    document.getElementById('acc-edit-info-form').addEventListener('submit', async (e) => {
      e.preventDefault();
      const name = document.getElementById('acc-edit-name').value.trim();
      const nameErr = validation.check(
        validation.required(name, '계정명을 입력해주세요.'),
        validation.pattern(name, 'ACCOUNT_NAME'),
      );
      if (nameErr) { setEditAlert('acc-edit-info-alert', 'error', nameErr); return; }

      const btn = document.getElementById('acc-edit-info-btn');
      btn.disabled = true;
      btn.innerHTML = '<span class="spinner"></span>';
      setEditAlert('acc-edit-info-alert', '');

      try {
        const res = await api.updateAccount(account.id, name);
        _selectedAccount = { ...account, name: res.data.name };
        setEditAlert('acc-edit-info-alert', 'success', '계정명이 변경됐습니다.');
      } catch (err) {
        setEditAlert('acc-edit-info-alert', 'error', err.message || '변경에 실패했습니다.');
      } finally {
        btn.disabled = false;
        btn.textContent = '계정명 저장';
      }
    });

    // 비밀번호 변경
    document.getElementById('acc-edit-pw-form').addEventListener('submit', async (e) => {
      e.preventDefault();
      const password = document.getElementById('acc-edit-pw').value;
      const pwErr = validation.check(
        validation.required(password, '비밀번호를 입력해주세요.'),
        validation.pattern(password, 'PASSWORD'),
      );
      if (pwErr) { setEditAlert('acc-edit-pw-alert', 'error', pwErr); return; }

      const btn = document.getElementById('acc-edit-pw-btn');
      btn.disabled = true;
      btn.innerHTML = '<span class="spinner"></span>';
      setEditAlert('acc-edit-pw-alert', '');

      try {
        await api.updateAccountPassword(account.id, password);
        setEditAlert('acc-edit-pw-alert', 'success', '비밀번호가 변경됐습니다.');
        document.getElementById('acc-edit-pw').value = '';
      } catch (err) {
        setEditAlert('acc-edit-pw-alert', 'error', err.message || '변경에 실패했습니다.');
      } finally {
        btn.disabled = false;
        btn.textContent = '비밀번호 변경';
      }
    });
  }

  async function loadEditPermTargets(accountId) {
    try {
      const [devsRes, gamesRes, permsRes] = await Promise.all([
        api.getDeveloperList(),
        api.getGameList(),
        api.getAccountPermissions(accountId),
      ]);
      const devs  = devsRes.data ?? [];
      const games = gamesRes.data ?? [];
      const devMap = Object.fromEntries(devs.map(d => [d.id, d.name]));
      const currentPerms = permsRes.data ?? {};

      // 현재 권한을 빠르게 조회할 수 있는 Set 생성
      const devPermSet  = new Set();
      (currentPerms.developerPermissions ?? []).forEach(p =>
        p.permissions.forEach(t => devPermSet.add(`${p.developerId}:${t}`))
      );
      const gamePermSet = new Set();
      (currentPerms.gamePermissions ?? []).forEach(p =>
        p.permissions.forEach(t => gamePermSet.add(`${p.gameId}:${t}`))
      );

      const devContainer  = document.getElementById('acc-edit-dev-perms');
      const gameContainer = document.getElementById('acc-edit-game-perms');
      if (!devContainer || !gameContainer) return;

      if (devs.length === 0) {
        devContainer.innerHTML = `<p style="font-size:13px;color:var(--text-sub)">등록된 개발사가 없습니다.</p>`;
      } else {
        devContainer.innerHTML = devs.map(d => `
          <div class="perm-assign-row">
            <span class="perm-assign-subject">${escapeHtml(d.name)}</span>
            <div class="perm-assign-checks">
              ${DEV_PERMS.map(p => `
                <label class="perm-check-item">
                  <input type="checkbox" data-scope="edit-dev" data-id="${d.id}" data-perm="${p}" ${devPermSet.has(`${d.id}:${p}`) ? 'checked' : ''} />
                  <span>${DEV_PERM_SHORT[p]}</span>
                </label>
              `).join('')}
            </div>
            <button class="btn-table-action perm-save-btn" data-scope="edit-dev" data-target-id="${d.id}">저장</button>
          </div>
        `).join('');

        devContainer.querySelectorAll('.perm-save-btn[data-scope="edit-dev"]').forEach(btn => {
          btn.addEventListener('click', () => savePermission(btn, accountId, 'dev'));
        });
      }

      if (games.length === 0) {
        gameContainer.innerHTML = `<p style="font-size:13px;color:var(--text-sub)">등록된 게임이 없습니다.</p>`;
      } else {
        const groups = {};
        games.forEach(g => {
          const key = devMap[g.developerId] ?? `개발사 #${g.developerId}`;
          (groups[key] ??= []).push(g);
        });

        gameContainer.innerHTML = Object.entries(groups).map(([devName, devGames]) => `
          <div class="perm-assign-group">
            <div class="perm-assign-group-label">${escapeHtml(devName)}</div>
            ${devGames.map(g => `
              <div class="perm-assign-row">
                <span class="perm-assign-subject">${escapeHtml(g.title)}</span>
                <div class="perm-assign-checks">
                  ${GAME_PERMS.map(p => `
                    <label class="perm-check-item">
                      <input type="checkbox" data-scope="edit-game" data-id="${g.id}" data-perm="${p}" ${gamePermSet.has(`${g.id}:${p}`) ? 'checked' : ''} />
                      <span>${GAME_PERM_SHORT[p]}</span>
                    </label>
                  `).join('')}
                </div>
                <button class="btn-table-action perm-save-btn" data-scope="edit-game" data-target-id="${g.id}">저장</button>
              </div>
            `).join('')}
          </div>
        `).join('');

        gameContainer.querySelectorAll('.perm-save-btn[data-scope="edit-game"]').forEach(btn => {
          btn.addEventListener('click', () => savePermission(btn, accountId, 'game'));
        });
      }
    } catch (err) {
      const c = document.getElementById('acc-edit-dev-perms');
      if (c) c.innerHTML = `<p style="font-size:13px;color:var(--error)">${escapeHtml(err.message || '불러오지 못했습니다.')}</p>`;
    }
  }

  async function savePermission(btn, accountId, type) {
    const targetId = Number(btn.dataset.targetId);
    const scope    = type === 'dev' ? 'edit-dev' : 'edit-game';

    // 해당 대상의 체크된 권한 수집
    const permissions = [];
    document.querySelectorAll(`input[data-scope="${scope}"][data-id="${targetId}"]:checked`).forEach(el => {
      permissions.push(el.dataset.perm);
    });

    btn.disabled = true;
    const origText = btn.textContent;
    btn.innerHTML = '<span class="spinner"></span>';

    try {
      if (type === 'dev') {
        await api.updateDeveloperPermission(accountId, targetId, permissions);
      } else {
        await api.updateGamePermission(accountId, targetId, permissions);
      }
      btn.textContent = '완료';
      setTimeout(() => { btn.textContent = origText; }, 1500);
    } catch (err) {
      setEditAlert('acc-edit-perm-alert', 'error', err.message || '권한 저장에 실패했습니다.');
      btn.textContent = origText;
    } finally {
      btn.disabled = false;
    }
  }

  function setEditAlert(elId, type, msg) {
    const el = document.getElementById(elId);
    if (!el) return;
    if (!type) { el.hidden = true; el.textContent = ''; return; }
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.hidden = false;
  }

  function escapeHtml(str) {
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  return { renderList, renderNew, renderEdit };
})();
