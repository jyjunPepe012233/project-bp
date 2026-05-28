const gamesPage = (() => {
  let _games = [];
  let _selectedGame = null;

  // ── 게임 목록 ──────────────────────────────────

  function renderList(container) {
    setPageTitle('게임 목록');
    container.innerHTML = `
      <div id="game-list-alert" hidden></div>
      <div id="game-list-body" class="game-card-list">
        <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
      </div>
    `;
    loadList();
  }

  async function loadList() {
    const body  = document.getElementById('game-list-body');
    const alert = document.getElementById('game-list-alert');
    if (!body) return;

    try {
      const [gamesRes, devsRes] = await Promise.all([
        api.getGameList(),
        api.getDeveloperList(),
      ]);
      _games = gamesRes.data ?? [];
      const devs = devsRes.data ?? [];
      const devMap = Object.fromEntries(devs.map(d => [d.id, d.name]));

      if (_games.length === 0) {
        body.innerHTML = `<p style="color:var(--text-sub);font-size:13px">등록된 게임이 없습니다.</p>`;
        return;
      }

      renderCards(body, devMap);
    } catch (err) {
      if (alert) {
        alert.className = 'alert alert-error';
        alert.textContent = err.message || '목록을 불러오지 못했습니다.';
        alert.hidden = false;
      }
      body.innerHTML = '';
    }
  }

  function renderCards(body, devMap) {
    body.innerHTML = `
      <div class="game-card-list">
        ${_games.map((g, i) => `
          <div class="game-card" data-idx="${i}">
            <div class="game-card-header">
              <div class="game-card-name-row">
                <span class="game-card-name">${escapeHtml(g.title)}</span>
                <span class="game-card-id-badge"># ${g.id}</span>
              </div>
              <div class="game-card-meta">
                <span class="game-card-meta-item">개발사 <strong>${escapeHtml(devMap[g.developerId] ?? '—')}</strong></span>
                <span class="game-card-meta-item">UUID <strong style="font-family:monospace;font-size:11px">${g.uuid}</strong></span>
              </div>
              ${g.description ? `<div class="game-card-desc">${escapeHtml(g.description)}</div>` : ''}
            </div>
            <div class="game-card-footer">
              <button class="game-card-edit-btn" data-idx="${i}">상세 / 수정</button>
              ${permissions.canDeleteGame(g.developerId) ? `<button class="btn-table-action game-card-delete-btn" data-idx="${i}">삭제</button>` : ''}
            </div>
          </div>
        `).join('')}
      </div>
    `;

    body.querySelectorAll('.game-card-edit-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = Number(btn.dataset.idx);
        _selectedGame = _games[idx];
        location.hash = '/games/edit';
      });
    });

    body.querySelectorAll('.game-card-delete-btn').forEach(btn => {
      btn.addEventListener('click', async () => {
        const idx = Number(btn.dataset.idx);
        const game = _games[idx];
        if (!game) return;

        if (!confirm(`게임을 삭제하시겠습니까?\n${game.title}\n\n관련 패치/카탈로그/권한도 함께 삭제됩니다.`)) return;

        const originalLabel = btn.textContent;
        btn.disabled = true;
        btn.textContent = '삭제 중…';

        try {
          await api.deleteGame(game.id);
          _games = _games.filter(g => g.id !== game.id);
          await loadList();
        } catch (err) {
          alert(err.message || '게임 삭제에 실패했습니다.');
          btn.disabled = false;
          btn.textContent = originalLabel;
        }
      });
    });
  }

  // ── 게임 상세 / 수정 ──────────────────────────

  function renderEdit(container) {
    setPageTitle('게임 상세');

    const game = _selectedGame;

    if (!game) {
      container.innerHTML = `
        <div class="card">
          <div class="card-title">게임 상세</div>
          <p style="color:var(--text-sub);font-size:13px">조회할 게임을 <a href="#/games">게임 목록</a>에서 선택해주세요.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = `
      <div class="card" style="margin-bottom:20px">
        <div class="card-title">게임 정보</div>
        <div id="game-detail-body">
          <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
        </div>
      </div>

      ${permissions.canWriteGame(game.developerId, game.id) ? `
      <div class="card" style="margin-bottom:20px">
        <div class="card-title">게임 수정</div>
        <div id="game-edit-alert" hidden></div>
        <form id="game-edit-form">
          <div class="form-group">
            <label class="form-label" for="game-edit-title">게임 제목</label>
            <input id="game-edit-title" class="form-input" type="text" value="${escapeHtml(game.title)}" />
          </div>
          <div class="form-group">
            <label class="form-label" for="game-edit-desc">설명 <span style="color:var(--text-sub);font-weight:400">(선택)</span></label>
            <input id="game-edit-desc" class="form-input" type="text" value="${escapeHtml(game.description ?? '')}" />
          </div>
          <button id="game-edit-btn" class="btn btn-primary" type="submit" style="width:auto;padding:9px 24px">저장</button>
        </form>
      </div>
      ` : ''}

      <div class="card" style="margin-bottom:20px">
        <div class="card-title">패치 목록</div>
        <div id="game-patches-body">
          <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
        </div>
      </div>

      <div class="card">
        <div class="card-title">관련 계정</div>
        <p style="font-size:12px;color:var(--text-sub);margin-bottom:16px">이 게임에 권한이 부여된 계정 목록입니다.</p>
        <div id="game-accounts-body">
          <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
        </div>
      </div>
    `;

    loadDetail(game);
    if (permissions.canWriteGame(game.developerId, game.id)) bindEditForm(game);
  }

  async function loadDetail(game) {
    try {
      const [devsRes, patchesRes, accountsRes] = await Promise.all([
        api.getDeveloperList(),
        api.getPatchList(game.id),
        api.getAccountList(),
      ]);

      const devs     = devsRes.data ?? [];
      const patches  = patchesRes.data ?? [];
      const accounts = accountsRes.data ?? [];

      const devMap = Object.fromEntries(devs.map(d => [d.id, d.name]));

      // 게임 정보
      const detailBody = document.getElementById('game-detail-body');
      if (detailBody) {
        detailBody.innerHTML = `
          <div class="dev-detail-info">
            <div class="dev-detail-row">
              <span class="dev-detail-label">ID</span>
              <span class="dev-detail-value"><code>${game.id}</code></span>
            </div>
            <div class="dev-detail-row">
              <span class="dev-detail-label">UUID</span>
              <span class="dev-detail-value"><code style="font-family:monospace;font-size:11px">${game.uuid}</code></span>
            </div>
            <div class="dev-detail-row">
              <span class="dev-detail-label">게임명</span>
              <span class="dev-detail-value" id="game-detail-title-display">${escapeHtml(game.title)}</span>
            </div>
            <div class="dev-detail-row">
              <span class="dev-detail-label">설명</span>
              <span class="dev-detail-value" id="game-detail-desc-display">${escapeHtml(game.description || '—')}</span>
            </div>
            <div class="dev-detail-row">
              <span class="dev-detail-label">개발사</span>
              <span class="dev-detail-value">${escapeHtml(devMap[game.developerId] ?? '—')} <code style="font-size:11px;color:var(--text-sub)">#${game.developerId ?? '—'}</code></span>
            </div>
          </div>
        `;
      }

      // 패치 목록
      const patchesBody = document.getElementById('game-patches-body');
      if (patchesBody) {
        if (patches.length === 0) {
          patchesBody.innerHTML = `<p style="color:var(--text-sub);font-size:13px">등록된 패치가 없습니다.</p>`;
        } else {
          patchesBody.innerHTML = `
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>버전</th>
                  <th>플랫폼</th>
                  <th>패치노트</th>
                </tr>
              </thead>
              <tbody>
                ${patches.map(p => `
                  <tr>
                    <td><code>${p.id}</code></td>
                    <td><span class="version-badge">${escapeHtml(p.version)}</span></td>
                    <td><span class="patch-card-platform">${escapeHtml(p.platform)}</span></td>
                    <td style="color:var(--text-sub);max-width:300px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">${escapeHtml(p.patchNote || '—')}</td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          `;
        }
      }

      // 관련 계정 (이 게임에 권한이 있는 계정들)
      const accountsBody = document.getElementById('game-accounts-body');
      if (accountsBody) {
        await loadGameAccounts(accountsBody, game.id, accounts);
      }
    } catch (err) {
      const detailBody = document.getElementById('game-detail-body');
      if (detailBody) {
        detailBody.innerHTML = `<p style="font-size:13px;color:var(--error)">${escapeHtml(err.message || '불러오지 못했습니다.')}</p>`;
      }
    }
  }

  async function loadGameAccounts(container, gameId, accounts) {
    try {
      const GAME_PERM_LABEL = { ADMIN: 'Admin', PUBLISHER: 'Publisher' };

      const permsResults = await Promise.all(
        accounts.map(a =>
          api.getAccountPermissions(a.id)
            .then(r => ({ account: a, perms: r.data }))
            .catch(() => ({ account: a, perms: null }))
        )
      );

      const gameAccounts = permsResults.filter(r => {
        if (!r.perms) return false;
        return (r.perms.gamePermissions ?? []).some(p => p.gameId === gameId);
      });

      if (gameAccounts.length === 0) {
        container.innerHTML = `<p style="color:var(--text-sub);font-size:13px">이 게임에 권한이 부여된 계정이 없습니다.</p>`;
        return;
      }

      container.innerHTML = `
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>계정명</th>
              <th>게임 권한</th>
            </tr>
          </thead>
          <tbody>
            ${gameAccounts.map(r => {
              const gamePerm = (r.perms.gamePermissions ?? []).find(p => p.gameId === gameId);
              const permTags = (gamePerm?.permissions ?? []).map(t =>
                `<span class="perm-tag perm-tag-game">${GAME_PERM_LABEL[t] ?? t}</span>`
              ).join('');
              return `
                <tr>
                  <td><code>${r.account.id}</code></td>
                  <td>${escapeHtml(r.account.name)}</td>
                  <td><div class="perm-tags">${permTags || '<span style="color:var(--text-sub);font-size:12px">—</span>'}</div></td>
                </tr>
              `;
            }).join('')}
          </tbody>
        </table>
      `;
    } catch (err) {
      container.innerHTML = `<p style="font-size:13px;color:var(--error)">${escapeHtml(err.message || '계정 목록을 불러오지 못했습니다.')}</p>`;
    }
  }

  function bindEditForm(game) {
    document.getElementById('game-edit-form').addEventListener('submit', async (e) => {
      e.preventDefault();
      const title       = document.getElementById('game-edit-title').value.trim();
      const description = document.getElementById('game-edit-desc').value.trim() || null;

      const vErr = validation.check(
        validation.required(title, '게임 제목을 입력해주세요.'),
        validation.maxLength(title, 200, '게임 제목은 200자 이하여야 합니다.'),
        validation.maxLength(description, 2000, '게임 설명은 2000자 이하여야 합니다.'),
      );
      if (vErr) { setEditAlert('error', vErr); return; }

      const btn = document.getElementById('game-edit-btn');
      btn.disabled = true;
      btn.innerHTML = '<span class="spinner"></span>';
      setEditAlert('');

      try {
        const res = await api.updateGame(game.id, title, description);
        _selectedGame = { ...game, title: res.data.title, description: res.data.description };

        const titleDisplay = document.getElementById('game-detail-title-display');
        if (titleDisplay) titleDisplay.textContent = res.data.title;
        const descDisplay = document.getElementById('game-detail-desc-display');
        if (descDisplay) descDisplay.textContent = res.data.description || '—';

        setEditAlert('success', '게임 정보가 수정됐습니다.');
      } catch (err) {
        setEditAlert('error', err.message || '수정에 실패했습니다.');
      } finally {
        btn.disabled = false;
        btn.textContent = '저장';
      }
    });
  }

  function setEditAlert(type, msg) {
    const el = document.getElementById('game-edit-alert');
    if (!el) return;
    if (!type) { el.hidden = true; el.textContent = ''; return; }
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.hidden = false;
  }

  // ── 게임 등록 ──────────────────────────────────

  function renderNew(container) {
    setPageTitle('게임 등록');
    container.innerHTML = `
      <div class="content-narrow"><div class="card">
        <div class="card-title">게임 등록</div>
        <div id="game-new-alert" hidden></div>
        <form id="game-new-form">
          <div class="form-group">
            <label class="form-label" for="game-developer">개발사</label>
            <select id="game-developer" class="form-input">
              <option value="" disabled selected>불러오는 중...</option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label" for="game-title">게임 제목</label>
            <input id="game-title" class="form-input" type="text" placeholder="게임 제목" />
          </div>
          <div class="form-group">
            <label class="form-label" for="game-desc">설명 <span style="color:var(--text-sub);font-weight:400">(선택)</span></label>
            <input id="game-desc" class="form-input" type="text" placeholder="한 줄 설명" />
          </div>
          <button id="game-new-btn" class="btn btn-primary" type="submit" style="width:auto;padding:9px 24px">등록</button>
        </form>
      </div></div>
    `;

    loadDeveloperOptions();

    document.getElementById('game-new-form').addEventListener('submit', async (e) => {
      e.preventDefault();

      const developerId = Number(document.getElementById('game-developer').value);
      const title       = document.getElementById('game-title').value.trim();
      const description = document.getElementById('game-desc').value.trim() || null;

      const vErr = validation.check(
        !developerId ? '개발사를 선택해주세요.' : null,
        validation.required(title, '게임 제목을 입력해주세요.'),
        validation.maxLength(title, 200, '게임 제목은 200자 이하여야 합니다.'),
        validation.maxLength(description, 2000, '게임 설명은 2000자 이하여야 합니다.'),
      );
      if (vErr) { setNewAlert('error', vErr); return; }

      setNewLoading(true);
      setNewAlert('');

      try {
        const res = await api.createGame(developerId, title, description);
        setNewAlert('success', `게임이 등록됐습니다. (ID: ${res.data.id})`);
        document.getElementById('game-new-form').reset();
        await loadDeveloperOptions();
      } catch (err) {
        setNewAlert('error', err.message || '등록에 실패했습니다.');
      } finally {
        setNewLoading(false);
      }
    });
  }

  async function loadDeveloperOptions() {
    const sel = document.getElementById('game-developer');
    if (!sel) return;
    try {
      const res = await api.getDeveloperList();
      const devs = res.data ?? [];
      if (devs.length === 0) {
        sel.innerHTML = `<option value="" disabled selected>등록된 개발사가 없습니다</option>`;
      } else {
        sel.innerHTML = `<option value="" disabled selected>개발사 선택</option>` +
          devs.map(d => `<option value="${d.id}">${escapeHtml(d.name)}</option>`).join('');
      }
    } catch {
      sel.innerHTML = `<option value="" disabled selected>불러오기 실패</option>`;
    }
  }

  function setNewAlert(type, msg) {
    const el = document.getElementById('game-new-alert');
    if (!el) return;
    if (!type) { el.hidden = true; el.textContent = ''; return; }
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.hidden = false;
  }

  function setNewLoading(on) {
    const btn = document.getElementById('game-new-btn');
    if (!btn) return;
    btn.disabled = on;
    btn.innerHTML = on ? '<span class="spinner"></span>' : '등록';
  }

  function escapeHtml(str) {
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  function getSelectedGame() { return _selectedGame; }

  return { renderList, renderNew, renderEdit, getSelectedGame };
})();
