const developersPage = (() => {
  let _devs = [];
  let _selectedDev = null;

  // ── 개발사 관리 (목록) ──────────────────────────

  function renderList(container) {
    setPageTitle('개발사 관리');
    container.innerHTML = `
      <div id="dev-list-alert" hidden></div>
      <div id="dev-list-body" class="dev-card-list">
        <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
      </div>
    `;
    loadList();
  }

  async function loadList() {
    const body  = document.getElementById('dev-list-body');
    const alert = document.getElementById('dev-list-alert');
    if (!body) return;

    try {
      const [devsRes, gamesRes, accountsRes] = await Promise.all([
        api.getDeveloperList(),
        api.getGameList(),
        api.getAccountList(),
      ]);
      _devs = devsRes.data ?? [];
      const games    = gamesRes.data ?? [];
      const accounts = accountsRes.data ?? [];

      // 개발사별 게임 수
      const gameCountMap = {};
      games.forEach(g => { gameCountMap[g.developerId] = (gameCountMap[g.developerId] || 0) + 1; });

      // 루트 계정 이름 매핑
      const accountMap = Object.fromEntries(accounts.map(a => [a.id, a.name]));

      if (_devs.length === 0) {
        body.innerHTML = `<p style="color:var(--text-sub);font-size:13px">등록된 개발사가 없습니다.</p>`;
        return;
      }

      renderCards(body, gameCountMap, accountMap);
    } catch (err) {
      if (alert) {
        alert.className = 'alert alert-error';
        alert.textContent = err.message || '목록을 불러오지 못했습니다.';
        alert.hidden = false;
      }
      body.innerHTML = '';
    }
  }

  function renderCards(body, gameCountMap, accountMap) {
    body.innerHTML = `
      <div class="dev-card-list">
        ${_devs.map((d, i) => `
          <div class="dev-card" data-idx="${i}">
            <div class="dev-card-header">
              <div class="dev-card-name-row">
                <span class="dev-card-name">${escapeHtml(d.name)}</span>
                <span class="dev-card-id-badge"># ${d.id}</span>
              </div>
              <div class="dev-card-meta">
                <span class="dev-card-meta-item">루트 계정 <strong>${escapeHtml(accountMap[d.rootAccountId] ?? '—')}</strong></span>
                <span class="dev-card-meta-item">게임 <strong>${gameCountMap[d.id] || 0}</strong>개</span>
              </div>
            </div>
            <div class="dev-card-footer">
              <button class="dev-card-edit-btn" data-idx="${i}">상세 / 수정</button>
              ${permissions.canDeleteDeveloper(d.id) ? `<button class="btn-table-action dev-card-delete-btn" data-idx="${i}">삭제</button>` : ''}
            </div>
          </div>
        `).join('')}
      </div>
    `;

    body.querySelectorAll('.dev-card-edit-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = Number(btn.dataset.idx);
        _selectedDev = _devs[idx];
        location.hash = '/developers/edit';
      });
    });

    body.querySelectorAll('.dev-card-delete-btn').forEach(btn => {
      btn.addEventListener('click', async () => {
        const idx = Number(btn.dataset.idx);
        const dev = _devs[idx];
        if (!dev) return;

        if (!confirm(`개발사를 삭제하시겠습니까?\n${dev.name}\n\n소속 게임/패치/관련 계정 데이터가 함께 삭제됩니다.`)) return;

        const originalLabel = btn.textContent;
        btn.disabled = true;
        btn.textContent = '삭제 중…';

        try {
          await api.deleteDeveloper(dev.id);
          _devs = _devs.filter(d => d.id !== dev.id);
          if (_selectedDev?.id === dev.id) _selectedDev = null;
          await loadList();
        } catch (err) {
          alert(err.message || '개발사 삭제에 실패했습니다.');
          btn.disabled = false;
          btn.textContent = originalLabel;
        }
      });
    });
  }

  // ── 개발사 상세 / 수정 ──────────────────────────

  function renderEdit(container) {
    setPageTitle('개발사 상세');

    const dev = _selectedDev;

    if (!dev) {
      container.innerHTML = `
        <div class="card">
          <div class="card-title">개발사 상세</div>
          <p style="color:var(--text-sub);font-size:13px">조회할 개발사를 <a href="#/developers">개발사 목록</a>에서 선택해주세요.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = `
      <div class="card" style="margin-bottom:20px">
        <div class="card-title">개발사 정보</div>
        <div id="dev-detail-body">
          <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
        </div>
      </div>

      ${permissions.canWriteDeveloper(dev.id) ? `
      <div class="card" style="margin-bottom:20px">
        <div class="card-title">이름 수정</div>
        <div id="dev-edit-name-alert" hidden></div>
        <form id="dev-edit-name-form">
          <div class="form-group">
            <label class="form-label" for="dev-edit-name">개발사명</label>
            <input id="dev-edit-name" class="form-input" type="text" value="${escapeHtml(dev.name)}" />
          </div>
          <button id="dev-edit-name-btn" class="btn btn-primary" type="submit" style="width:auto;padding:9px 24px">이름 저장</button>
        </form>
      </div>
      ` : ''}

      <div class="card" style="margin-bottom:20px">
        <div class="card-title">소속 게임</div>
        <div id="dev-games-body">
          <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
        </div>
      </div>

      <div class="card">
        <div class="card-title">소속 계정</div>
        <p style="font-size:12px;color:var(--text-sub);margin-bottom:16px">이 개발사에 권한이 부여된 계정 목록입니다.</p>
        <div id="dev-accounts-body">
          <span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span>
        </div>
      </div>
    `;

    loadDetail(dev);
    if (permissions.canWriteDeveloper(dev.id)) bindEditName(dev);
  }

  async function loadDetail(dev) {
    try {
      const [devsRes, gamesRes, accountsRes] = await Promise.all([
        api.getDeveloper(dev.id),
        api.getGameList(),
        api.getAccountList(),
      ]);

      const developer = devsRes.data;
      const allGames  = gamesRes.data ?? [];
      const accounts  = accountsRes.data ?? [];

      const accountMap = Object.fromEntries(accounts.map(a => [a.id, a.name]));
      const devGames   = allGames.filter(g => g.developerId === dev.id);

      // 개발사 정보
      const detailBody = document.getElementById('dev-detail-body');
      if (detailBody) {
        detailBody.innerHTML = `
          <div class="dev-detail-info">
            <div class="dev-detail-row">
              <span class="dev-detail-label">ID</span>
              <span class="dev-detail-value"><code>${developer.id}</code></span>
            </div>
            <div class="dev-detail-row">
              <span class="dev-detail-label">개발사명</span>
              <span class="dev-detail-value" id="dev-detail-name-display">${escapeHtml(developer.name)}</span>
            </div>
            <div class="dev-detail-row">
              <span class="dev-detail-label">루트 계정</span>
              <span class="dev-detail-value">${escapeHtml(accountMap[developer.rootAccountId] ?? '—')} <code style="font-size:11px;color:var(--text-sub)">#${developer.rootAccountId ?? '—'}</code></span>
            </div>
          </div>
        `;
      }

      // 소속 게임
      const gamesBody = document.getElementById('dev-games-body');
      if (gamesBody) {
        if (devGames.length === 0) {
          gamesBody.innerHTML = `<p style="color:var(--text-sub);font-size:13px">등록된 게임이 없습니다.</p>`;
        } else {
          gamesBody.innerHTML = `
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>게임명</th>
                  <th>설명</th>
                </tr>
              </thead>
              <tbody>
                ${devGames.map(g => `
                  <tr>
                    <td><code>${g.id}</code></td>
                    <td>${escapeHtml(g.title)}</td>
                    <td style="color:var(--text-sub)">${escapeHtml(g.description || '—')}</td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          `;
        }
      }

      // 소속 계정 (이 개발사에 권한이 있는 계정들)
      const accountsBody = document.getElementById('dev-accounts-body');
      if (accountsBody) {
        await loadDevAccounts(accountsBody, dev.id, accounts);
      }
    } catch (err) {
      const detailBody = document.getElementById('dev-detail-body');
      if (detailBody) {
        detailBody.innerHTML = `<p style="font-size:13px;color:var(--error)">${escapeHtml(err.message || '불러오지 못했습니다.')}</p>`;
      }
    }
  }

  async function loadDevAccounts(container, developerId, accounts) {
    try {
      const DEV_PERM_LABEL  = { ADMIN: 'Admin', PUBLISHER: 'Publisher' };

      // 각 계정의 권한을 병렬 조회
      const permsResults = await Promise.all(
        accounts.map(a =>
          api.getAccountPermissions(a.id)
            .then(r => ({ account: a, perms: r.data }))
            .catch(() => ({ account: a, perms: null }))
        )
      );

      // 이 개발사에 권한이 있는 계정만 필터
      const devAccounts = permsResults.filter(r => {
        if (!r.perms) return false;
        return (r.perms.developerPermissions ?? []).some(p => p.developerId === developerId);
      });

      if (devAccounts.length === 0) {
        container.innerHTML = `<p style="color:var(--text-sub);font-size:13px">이 개발사에 권한이 부여된 계정이 없습니다.</p>`;
        return;
      }

      container.innerHTML = `
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>계정명</th>
              <th>개발사 권한</th>
            </tr>
          </thead>
          <tbody>
            ${devAccounts.map(r => {
              const devPerm = (r.perms.developerPermissions ?? []).find(p => p.developerId === developerId);
              const permTags = (devPerm?.permissions ?? []).map(t =>
                `<span class="perm-tag perm-tag-dev">${DEV_PERM_LABEL[t] ?? t}</span>`
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

  function bindEditName(dev) {
    document.getElementById('dev-edit-name-form').addEventListener('submit', async (e) => {
      e.preventDefault();
      const name = document.getElementById('dev-edit-name').value.trim();
      const vErr = validation.check(
        validation.required(name, '개발사명을 입력해주세요.'),
        validation.pattern(name, 'DISPLAY_NAME'),
      );
      if (vErr) { setEditAlert('error', vErr); return; }

      const btn = document.getElementById('dev-edit-name-btn');
      btn.disabled = true;
      btn.innerHTML = '<span class="spinner"></span>';
      setEditAlert('');

      try {
        const res = await api.updateDeveloper(dev.id, name);
        _selectedDev = { ...dev, name: res.data.name };

        // 상세 정보 영역 이름도 갱신
        const nameDisplay = document.getElementById('dev-detail-name-display');
        if (nameDisplay) nameDisplay.textContent = res.data.name;

        setEditAlert('success', '개발사명이 변경됐습니다.');
      } catch (err) {
        setEditAlert('error', err.message || '변경에 실패했습니다.');
      } finally {
        btn.disabled = false;
        btn.textContent = '이름 저장';
      }
    });
  }

  function setEditAlert(type, msg) {
    const el = document.getElementById('dev-edit-name-alert');
    if (!el) return;
    if (!type) { el.hidden = true; el.textContent = ''; return; }
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.hidden = false;
  }

  // ── 개발자 등록 (퍼블릭) ─────────────────────────

  function renderNew(container) {
    container.innerHTML = `
      <div class="public-page">
        <div class="public-brand">BP Admin Console</div>
        <div class="form-card">
          <h2>개발자 등록</h2>
          <p class="form-card-desc">
            처음 사용하는 경우 개발자 조직과 루트 계정을 등록합니다.<br>
            이미 계정이 있다면 <a href="#/">로그인</a>하세요.
          </p>
          <form id="dev-form">
            <div id="dev-alert" hidden></div>
            <fieldset class="fieldset">
              <legend class="fieldset-legend">개발사 정보</legend>
              <div class="form-group">
                <label class="form-label" for="dev-name">개발사명</label>
                <input id="dev-name" class="form-input" type="text" autofocus placeholder="예) 예준게임즈" />
              </div>
            </fieldset>
            <fieldset class="fieldset">
              <legend class="fieldset-legend">루트 계정</legend>
              <div class="form-group">
                <label class="form-label" for="dev-account-name">계정명</label>
                <input id="dev-account-name" class="form-input" type="text" autocomplete="username" placeholder="yesgames0512" />
              </div>
              <div class="form-group">
                <label class="form-label" for="dev-account-pw">비밀번호</label>
                <input id="dev-account-pw" class="form-input" type="password" autocomplete="new-password" placeholder="VGr5Iq9x" />
              </div>
            </fieldset>
            <button id="dev-btn" class="btn btn-primary" type="submit">등록</button>
          </form>
        </div>
      </div>
    `;

    document.getElementById('dev-form').addEventListener('submit', async (e) => {
      e.preventDefault();
      const developerName       = document.getElementById('dev-name').value.trim();
      const rootAccountName     = document.getElementById('dev-account-name').value.trim();
      const rootAccountPassword = document.getElementById('dev-account-pw').value;

      const vErr = validation.check(
        validation.required(developerName, '개발사명을 입력해주세요.'),
        validation.pattern(developerName, 'DISPLAY_NAME'),
        validation.required(rootAccountName, '루트 계정명을 입력해주세요.'),
        validation.pattern(rootAccountName, 'ACCOUNT_NAME'),
        validation.required(rootAccountPassword, '비밀번호를 입력해주세요.'),
        validation.pattern(rootAccountPassword, 'PASSWORD'),
      );
      if (vErr) { setRegAlert('error', vErr); return; }
      setRegLoading(true);
      setRegAlert('');
      try {
        await api.createDeveloper(developerName, rootAccountName, rootAccountPassword);
        setRegAlert('success', '등록이 완료됐습니다. 로그인 페이지로 이동합니다.');
        setTimeout(() => { location.hash = '/'; }, 1800);
      } catch (err) {
        setRegAlert('error', err.message || '등록에 실패했습니다.');
      } finally {
        setRegLoading(false);
      }
    });
  }

  function setRegAlert(type, msg) {
    const el = document.getElementById('dev-alert');
    if (!el) return;
    if (!type) { el.hidden = true; el.textContent = ''; return; }
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.hidden = false;
  }

  function setRegLoading(on) {
    const btn = document.getElementById('dev-btn');
    if (!btn) return;
    btn.disabled = on;
    btn.innerHTML = on ? '<span class="spinner"></span>' : '등록';
  }

  function escapeHtml(str) {
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  return { renderList, renderNew, renderEdit };
})();
