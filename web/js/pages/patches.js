const patchesPage = (() => {
  const PLATFORM_LABEL = { ANDROID: 'Android', IOS: 'iOS', STANDALONE_OSX: 'StandaloneOSX' };

  let _allPatches = [];
  let _games      = [];
  let _devMap     = {};
  let _selectedPatch = null;
  let _uploadPreselect = null;

  // ── 패치 목록 ────────────────────────────────────

  function renderList(container) {
    setPageTitle('패치 목록');
    container.innerHTML = `
      <div class="patch-filter-bar">
        <div class="patch-filter-group">
          <label class="patch-filter-label">개발사</label>
          <select id="filter-dev" class="form-input patch-filter-select">
            <option value="">전체</option>
          </select>
        </div>
        <div class="patch-filter-group">
          <label class="patch-filter-label">게임</label>
          <select id="filter-game" class="form-input patch-filter-select">
            <option value="">전체</option>
          </select>
        </div>
      </div>
      <div id="patch-list-alert" hidden></div>
      <div id="patch-list-body">
        <p class="patch-empty-msg"><span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span></p>
      </div>
    `;
    loadAll();
  }

  async function loadAll() {
    const devSel  = document.getElementById('filter-dev');
    const gameSel = document.getElementById('filter-game');
    const body    = document.getElementById('patch-list-body');
    if (!devSel || !gameSel || !body) return;

    try {
      const [gamesRes, devsRes] = await Promise.all([api.getGameList(), api.getDeveloperList()]);
      _games  = gamesRes.data ?? [];
      _devMap = Object.fromEntries((devsRes.data ?? []).map(d => [d.id, d.name]));

      const patchResults = await Promise.all(
        _games.map(g =>
          api.getPatchList(g.id)
            .then(r => (r.data ?? []).map(p => ({
              ...p,
              gameId: g.id, gameTitle: g.title, gameUuid: g.uuid,
              devId: g.developerId, devName: _devMap[g.developerId] ?? `개발사 #${g.developerId}`,
            })))
            .catch(() => [])
        )
      );
      _allPatches = patchResults.flat();
      _allPatches.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

      const devs = devsRes.data ?? [];
      devSel.innerHTML = `<option value="">전체</option>` +
        devs.map(d => `<option value="${d.id}">${escapeHtml(d.name)}</option>`).join('');

      function updateGameOptions(devId) {
        const filtered = devId ? _games.filter(g => g.developerId === devId) : _games;
        gameSel.innerHTML = `<option value="">전체</option>` +
          filtered.map(g => `<option value="${g.id}">${escapeHtml(g.title)}</option>`).join('');
        gameSel.value = '';
      }

      updateGameOptions(null);
      devSel.addEventListener('change', () => {
        updateGameOptions(Number(devSel.value) || null);
        applyFilter();
      });
      gameSel.addEventListener('change', applyFilter);
      showMissingCatalogWarning();
      applyFilter();
    } catch (err) {
      body.innerHTML = `<p class="patch-empty-msg" style="color:var(--error)">${escapeHtml(err.message || '불러오지 못했습니다.')}</p>`;
    }
  }

  function applyFilter() {
    const body    = document.getElementById('patch-list-body');
    const devSel  = document.getElementById('filter-dev');
    const gameSel = document.getElementById('filter-game');
    if (!body) return;

    const devId  = Number(devSel?.value)  || null;
    const gameId = Number(gameSel?.value) || null;

    const filtered = _allPatches.filter(p =>
      (!devId  || p.devId  === devId) &&
      (!gameId || p.gameId === gameId)
    );
    renderPatches(filtered, body);
  }

  function showMissingCatalogWarning() {
    const alertEl = document.getElementById('patch-list-alert');
    if (!alertEl) return;

    const missing = _allPatches.filter(p => !p.catalogUploaded || !p.catalogHashUploaded);
    if (missing.length === 0) {
      alertEl.hidden = true;
      return;
    }

    const lines = missing.map(p => {
      const plat = PLATFORM_LABEL[p.platform] ?? p.platform;
      const parts = [];
      if (!p.catalogUploaded) parts.push('카탈로그');
      if (!p.catalogHashUploaded) parts.push('카탈로그 해시');
      return `<span>${escapeHtml(p.gameTitle)} — ${plat} ${escapeHtml(p.version)}: <strong>${parts.join(', ')}</strong> 미업로드</span>`;
    }).join('<br>');

    alertEl.className = 'alert alert-error';
    alertEl.innerHTML = `<div style="margin-bottom:4px;font-weight:700">카탈로그가 업로드되지 않은 패치가 ${missing.length}개 있습니다.</div>${lines}`;
    alertEl.hidden = false;
  }

  function renderPatches(patches, body) {
    if (patches.length === 0) {
      body.innerHTML = `<p class="patch-empty-msg">패치가 없습니다.</p>`;
      return;
    }

    const seenKeys = new Set();
    body.innerHTML = `
      <div class="patch-card-list">
        ${patches.map((p, i) => {
          const key      = `${p.gameId}:${p.platform}`;
          const isLatest = !seenKeys.has(key);
          if (isLatest) seenKeys.add(key);
          return `
            <div class="patch-card${isLatest ? ' patch-card-latest' : ''}" data-idx="${i}">
              <div class="patch-card-header">
                <span class="version-badge">${escapeHtml(p.version)}</span>
                ${isLatest ? `<span class="patch-latest-badge">${PLATFORM_LABEL[p.platform] ?? p.platform} LATEST</span>` : ''}
                <span class="patch-card-platform">${PLATFORM_LABEL[p.platform] ?? p.platform}</span>
                <span class="patch-card-game">${escapeHtml(p.gameTitle)}</span>
                <span class="patch-card-date">${formatDate(p.createdAt)}</span>
                ${permissions.canWritePatch(p.devId, p.gameId) ? `<button class="btn-table-action patch-edit-btn" data-idx="${i}" style="margin-left:auto">수정</button>` : ''}
              </div>
              <div class="patch-card-note">${p.patchNote ? escapeHtml(p.patchNote) : '<span class="patch-note-empty">패치 노트 없음</span>'}</div>
              ${renderCatalogBar(p)}
            </div>
          `;
        }).join('')}
      </div>
    `;

    body.querySelectorAll('.patch-edit-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        _selectedPatch = patches[Number(btn.dataset.idx)];
        location.hash = '/patches/edit';
      });
    });

    body.querySelectorAll('.catalog-copy-btn:not(.catalog-download-btn)').forEach(btn => {
      btn.addEventListener('click', () => {
        const path = btn.dataset.path;
        navigator.clipboard.writeText(path).then(() => {
          const orig = btn.textContent;
          btn.textContent = '복사됨';
          btn.disabled = true;
          setTimeout(() => { btn.textContent = orig; btn.disabled = false; }, 1500);
        });
      });
    });

    body.querySelectorAll('.catalog-download-btn').forEach(btn => {
      btn.addEventListener('click', () => downloadFile(btn.dataset.url, btn.dataset.filename, btn));
    });

    body.querySelectorAll('.catalog-upload-btn').forEach(btn => {
      btn.addEventListener('click', () => {
        _uploadPreselect = {
          gameId: Number(btn.dataset.gameId),
          patchId: Number(btn.dataset.patchId),
          target: btn.dataset.target || null,
        };
        location.hash = '/patches/upload';
      });
    });

    body.querySelectorAll('.catalog-delete-btn').forEach(btn => {
      btn.addEventListener('click', async () => {
        const gameId  = Number(btn.dataset.gameId);
        const patchId = Number(btn.dataset.patchId);
        const target  = btn.dataset.target;
        const label   = target === 'catalog' ? '카탈로그' : '카탈로그 해시';

        if (!confirm(`${label} 파일을 삭제하시겠습니까?`)) return;

        btn.disabled = true;
        btn.textContent = '삭제 중…';

        try {
          if (target === 'catalog') {
            await api.deleteCatalog(patchId);
          } else {
            await api.deleteCatalogHash(patchId);
          }
          const idx = _allPatches.findIndex(x => x.id === patchId && x.gameId === gameId);
          if (idx !== -1) {
            if (target === 'catalog') _allPatches[idx].catalogUploaded = false;
            else _allPatches[idx].catalogHashUploaded = false;
          }
          applyFilter();
        } catch (err) {
          alert(err.message || '삭제에 실패했습니다.');
          btn.disabled = false;
          btn.textContent = '삭제';
        }
      });
    });

  }

  function renderCatalogBar(p) {
    const apiBase = `${location.protocol}//${location.hostname}:8080`;
    const hasCatalog = !!p.catalogUploaded;
    const hasHash = !!p.catalogHashUploaded;
    const canWrite = permissions.canWritePatch(p.devId, p.gameId);

    const platformDir = PLATFORM_LABEL[p.platform] ?? p.platform;
    const catalogDownloadPath = hasCatalog
      ? `${apiBase}/bundles/${p.gameUuid}/${platformDir}/${p.version}/catalog.json`
      : '';
    const hashDownloadPath = hasHash
      ? `${apiBase}/bundles/${p.gameUuid}/${platformDir}/${p.version}/catalog.hash`
      : '';

    const catalogCell = hasCatalog
      ? `<div class="catalog-cell catalog-cell-ok">
           <span class="catalog-cell-label">CATALOG</span>
           <span class="catalog-cell-filename">catalog.json</span>
           <span class="catalog-url-box"><span class="catalog-method-badge">GET</span><span class="catalog-path-text">${escapeHtml(catalogDownloadPath)}</span></span>
           <button class="catalog-copy-btn catalog-download-btn" data-url="${escapeHtml(catalogDownloadPath)}" data-filename="catalog.json">다운로드</button>
           <button class="catalog-copy-btn" data-path="${escapeHtml(catalogDownloadPath)}">경로 복사</button>
           ${canWrite ? `<button class="btn-table-action catalog-delete-btn" data-game-id="${p.gameId}" data-patch-id="${p.id}" data-target="catalog">삭제</button>` : ''}
         </div>`
      : `<div class="catalog-cell catalog-cell-empty catalog-cell-warn">
           <span class="catalog-cell-label">CATALOG</span>
           <span class="catalog-cell-none catalog-cell-warn-text">미업로드</span>
           ${canWrite ? `<button class="btn-table-action catalog-upload-btn" data-game-id="${p.gameId}" data-patch-id="${p.id}" data-target="catalog">업로드</button>` : ''}
         </div>`;

    const hashCell = hasHash
      ? `<div class="catalog-cell catalog-cell-ok">
           <span class="catalog-cell-label">CATALOG HASH</span>
           <span class="catalog-cell-filename">catalog.hash</span>
           <span class="catalog-url-box"><span class="catalog-method-badge">GET</span><span class="catalog-path-text">${escapeHtml(hashDownloadPath)}</span></span>
           <button class="catalog-copy-btn catalog-download-btn" data-url="${escapeHtml(hashDownloadPath)}" data-filename="catalog.hash">다운로드</button>
           <button class="catalog-copy-btn" data-path="${escapeHtml(hashDownloadPath)}">경로 복사</button>
           ${canWrite ? `<button class="btn-table-action catalog-delete-btn" data-game-id="${p.gameId}" data-patch-id="${p.id}" data-target="hash">삭제</button>` : ''}
         </div>`
      : `<div class="catalog-cell catalog-cell-empty catalog-cell-warn">
           <span class="catalog-cell-label">CATALOG HASH</span>
           <span class="catalog-cell-none catalog-cell-warn-text">미업로드</span>
           ${canWrite ? `<button class="btn-table-action catalog-upload-btn" data-game-id="${p.gameId}" data-patch-id="${p.id}" data-target="hash">업로드</button>` : ''}
         </div>`;

    const bundleSection = `<div class="catalog-cell catalog-cell-empty" style="border-top:1px solid var(--border)">
      <span class="catalog-cell-label">BUNDLE</span>
      <a href="#/bundles" class="btn-table-action" style="text-decoration:none">번들 관리 →</a>
    </div>`;

    return `<div class="catalog-footer">
      <div class="catalog-grid">${catalogCell}${hashCell}</div>
      ${bundleSection}
    </div>`;
  }

  // ── 패치 수정 ────────────────────────────────────

  function renderEdit(container) {
    setPageTitle('패치 수정');

    const p = _selectedPatch;

    if (!p) {
      container.innerHTML = `
        <div class="content-narrow">
          <div class="card">
            <div class="card-title">패치 수정</div>
            <p style="color:var(--text-sub);font-size:13px">수정할 패치를 <a href="#/patches">패치 목록</a>에서 선택해주세요.</p>
          </div>
        </div>
      `;
      return;
    }

    container.innerHTML = `
      <div class="content-narrow">
        <div class="card">
          <div class="card-title">패치 수정</div>
          <div class="patch-edit-meta-row">
            <div class="patch-edit-meta-item">
              <span class="patch-edit-meta-label">게임</span>
              <span class="patch-edit-meta-value">${escapeHtml(p.gameTitle)}</span>
            </div>
            <div class="patch-edit-meta-item">
              <span class="patch-edit-meta-label">플랫폼</span>
              <span class="patch-edit-meta-value">${PLATFORM_LABEL[p.platform] ?? p.platform}</span>
            </div>
            <div class="patch-edit-meta-item">
              <span class="patch-edit-meta-label">버전</span>
              <span class="patch-edit-meta-value">${escapeHtml(p.version)}</span>
            </div>
            <div class="patch-edit-meta-item">
              <span class="patch-edit-meta-label">생성일</span>
              <span class="patch-edit-meta-value">${formatDate(p.createdAt)}</span>
            </div>
          </div>
          <div id="patch-edit-alert" hidden></div>
          <form id="patch-edit-form">
            <div class="form-group">
              <label class="form-label" for="patch-edit-note">패치 노트</label>
              <textarea id="patch-edit-note" class="form-input form-textarea form-textarea-lg" placeholder="변경 사항을 입력하세요">${p.patchNote ? escapeHtml(p.patchNote) : ''}</textarea>
            </div>
            <button id="patch-edit-btn" class="btn btn-primary" type="submit" style="width:auto;padding:9px 24px">저장</button>
          </form>
        </div>
      </div>
    `;

    document.getElementById('patch-edit-form').addEventListener('submit', async (e) => {
      e.preventDefault();
      const patchNote = document.getElementById('patch-edit-note').value.trim() || null;

      const vErr = validation.maxLength(patchNote, 4096, '패치 노트는 4096자 이하여야 합니다.');
      if (vErr) { setEditAlert('error', vErr); return; }

      setEditLoading(true);
      setEditAlert('');

      try {
        await api.updatePatchNote(p.id, patchNote);
        _selectedPatch = { ...p, patchNote };
        // _allPatches 캐시도 갱신
        const idx = _allPatches.findIndex(x => x.id === p.id && x.gameId === p.gameId);
        if (idx !== -1) _allPatches[idx] = { ..._allPatches[idx], patchNote };
        setEditAlert('success', '패치 노트가 저장됐습니다.');
      } catch (err) {
        setEditAlert('error', err.message || '저장에 실패했습니다.');
      } finally {
        setEditLoading(false);
      }
    });
  }

  function setEditAlert(type, msg) {
    const el = document.getElementById('patch-edit-alert');
    if (!el) return;
    if (!type) { el.hidden = true; el.textContent = ''; return; }
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.hidden = false;
  }

  function setEditLoading(on) {
    const btn = document.getElementById('patch-edit-btn');
    if (!btn) return;
    btn.disabled = on;
    btn.innerHTML = on ? '<span class="spinner"></span>' : '저장';
  }

  // ── 패치 생성 ────────────────────────────────────

  function renderNew(container) {
    setPageTitle('패치 생성');
    container.innerHTML = `
      <div class="content-narrow">
      <div class="card">
        <div class="card-title">패치 생성</div>
        <div id="patch-alert" hidden></div>
        <form id="patch-form">
          <div class="form-group">
            <label class="form-label" for="patch-game">게임</label>
            <select id="patch-game" class="form-input">
              <option value="" disabled selected>불러오는 중...</option>
            </select>
          </div>
          <div class="form-row">
            <div class="form-group" style="flex:1">
              <label class="form-label">플랫폼</label>
              <div class="platform-check-group">
                <label class="platform-check-item">
                  <input type="checkbox" class="platform-check" value="ANDROID" />
                  <span>Android</span>
                </label>
                <label class="platform-check-item">
                  <input type="checkbox" class="platform-check" value="IOS" />
                  <span>iOS</span>
                </label>
                <label class="platform-check-item">
                  <input type="checkbox" class="platform-check" value="STANDALONE_OSX" />
                  <span>StandaloneOSX</span>
                </label>
              </div>
            </div>
            <div class="form-group" style="flex:1">
              <label class="form-label" for="patch-version">버전</label>
              <input id="patch-version" class="form-input" type="text" placeholder="예) 1.0.0" />
            </div>
          </div>
          <div class="form-group">
            <label class="form-label" for="patch-note">패치 노트 <span style="color:var(--text-sub);font-weight:400">(선택)</span></label>
            <textarea id="patch-note" class="form-input form-textarea form-textarea-lg" placeholder="변경 사항을 입력하세요"></textarea>
          </div>
          <button id="patch-btn" class="btn btn-primary" type="submit" style="width:auto;padding:9px 24px">생성</button>
        </form>
      </div>
      </div>
    `;

    loadGameOptions();

    document.getElementById('patch-form').addEventListener('submit', async (e) => {
      e.preventDefault();

      const gameId    = Number(document.getElementById('patch-game').value);
      const platforms = [...document.querySelectorAll('.platform-check:checked')].map(el => el.value);
      const version   = document.getElementById('patch-version').value.trim();
      const patchNote = document.getElementById('patch-note').value.trim() || null;

      const vErr = validation.check(
        !gameId ? '게임을 선택해주세요.' : null,
        !platforms.length ? '플랫폼을 하나 이상 선택해주세요.' : null,
        validation.required(version, '버전을 입력해주세요.'),
        validation.pattern(version, 'VERSION'),
        validation.maxLength(patchNote, 4096, '패치 노트는 4096자 이하여야 합니다.'),
      );
      if (vErr) { setAlert('error', vErr); return; }

      setLoading(true);
      setAlert('');

      const results = await Promise.allSettled(
        platforms.map(p => api.createPatch(gameId, version, p, patchNote))
      );

      const succeeded = results.filter(r => r.status === 'fulfilled').map(r => r.value.data);
      const failed    = results.filter(r => r.status === 'rejected');

      if (succeeded.length > 0 && failed.length === 0) {
        const labels = succeeded.map(d => `${PLATFORM_LABEL[d.platform] ?? d.platform} ${d.version}`).join(', ');
        setAlert('success', `패치가 생성됐습니다. (${labels})`);
        document.getElementById('patch-version').value = '';
        document.getElementById('patch-note').value = '';
        document.querySelectorAll('.platform-check').forEach(el => { el.checked = false; });

        const linkArea = document.getElementById('patch-alert');
        if (linkArea) {
          const links = succeeded.map(d => {
            const platLabel = PLATFORM_LABEL[d.platform] ?? d.platform;
            return `<a href="#/patches/upload" class="patch-created-link" data-game-id="${gameId}" data-patch-id="${d.id}">${platLabel} ${d.version} 카탈로그 업로드 →</a>`;
          }).join(' ');
          linkArea.innerHTML += `<div style="margin-top:8px">${links}</div>`;
          linkArea.querySelectorAll('.patch-created-link').forEach(a => {
            a.addEventListener('click', (e) => {
              e.preventDefault();
              _uploadPreselect = {
                gameId: Number(a.dataset.gameId),
                patchId: Number(a.dataset.patchId),
                target: 'catalog',
              };
              location.hash = '/patches/upload';
            });
          });
        }
      } else if (succeeded.length > 0 && failed.length > 0) {
        const okLabels = succeeded.map(d => PLATFORM_LABEL[d.platform] ?? d.platform).join(', ');
        setAlert('error', `일부 플랫폼만 성공했습니다. 성공: ${okLabels} / 실패: ${failed.map(f => f.reason?.message ?? '').join(', ')}`);
      } else {
        setAlert('error', failed[0]?.reason?.message || '생성에 실패했습니다.');
      }

      setLoading(false);
    });
  }

  async function loadGameOptions() {
    const sel = document.getElementById('patch-game');
    if (!sel) return;
    try {
      const [gamesRes, devsRes] = await Promise.all([api.getGameList(), api.getDeveloperList()]);
      const games  = gamesRes.data ?? [];
      const devMap = Object.fromEntries((devsRes.data ?? []).map(d => [d.id, d.name]));

      if (games.length === 0) {
        sel.innerHTML = `<option value="" disabled selected>등록된 게임이 없습니다</option>`;
        return;
      }

      const groups = {};
      games.forEach(g => {
        const key = devMap[g.developerId] ?? `개발사 #${g.developerId}`;
        (groups[key] ??= []).push(g);
      });

      sel.innerHTML = `<option value="" disabled selected>게임 선택</option>` +
        Object.entries(groups).map(([devName, devGames]) =>
          `<optgroup label="${escapeHtml(devName)}">` +
          devGames.map(g => `<option value="${g.id}">${escapeHtml(g.title)}</option>`).join('') +
          `</optgroup>`
        ).join('');
    } catch {
      sel.innerHTML = `<option value="" disabled selected>불러오기 실패</option>`;
    }
  }

  function setAlert(type, msg) {
    const el = document.getElementById('patch-alert');
    if (!el) return;
    if (!type) { el.hidden = true; el.textContent = ''; return; }
    el.className = `alert alert-${type}`;
    el.textContent = msg;
    el.hidden = false;
  }

  function setLoading(on) {
    const btn = document.getElementById('patch-btn');
    if (!btn) return;
    btn.disabled = on;
    btn.innerHTML = on ? '<span class="spinner"></span>' : '생성';
  }

  function formatDate(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    const pad = n => String(n).padStart(2, '0');
    return `${d.getFullYear()}.${pad(d.getMonth()+1)}.${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  function escapeHtml(str) {
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  }

  async function downloadFile(url, filename, btn) {
    const orig = btn.textContent;
    btn.disabled = true;
    btn.textContent = '다운로드 중…';
    try {
      const headers = {};
      const token = store.getAccessToken();
      if (token) headers['Authorization'] = `Bearer ${token}`;
      const res = await fetch(url, { headers });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const blob = await res.blob();
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = filename || 'download';
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(a.href);
    } catch (err) {
      alert(err.message || '다운로드에 실패했습니다.');
    } finally {
      btn.textContent = orig;
      btn.disabled = false;
    }
  }

  // ── 카탈로그 업로드 ────────────────────────────

  function renderUpload(container) {
    setPageTitle('카탈로그 업로드');
    container.innerHTML = `
      <div class="content-narrow">
      <div class="card">
        <div class="card-title">카탈로그 업로드</div>
        <p style="font-size:12px;color:var(--text-sub);margin-bottom:16px">패치에 카탈로그 파일과 카탈로그 해시 파일을 각각 업로드합니다.</p>

        <div class="form-group">
          <label class="form-label">게임</label>
          <select id="catalog-game" class="form-input">
            <option value="" disabled selected>불러오는 중...</option>
          </select>
        </div>
        <div class="form-group" style="margin-bottom:24px">
          <label class="form-label">패치</label>
          <select id="catalog-patch" class="form-input" disabled>
            <option value="" disabled selected>게임을 먼저 선택하세요</option>
          </select>
        </div>

        <div class="upload-split">
          <div class="upload-panel" id="panel-catalog">
            <div class="upload-panel-header">
              <span class="upload-panel-title">카탈로그 파일</span>
              <span class="upload-panel-status" id="status-catalog"></span>
            </div>
            <div id="alert-catalog" hidden></div>
            <div class="file-drop" id="drop-catalog">
              <input type="file" id="file-catalog" class="file-drop-input" />
              <span class="file-drop-text" id="drop-catalog-text">클릭하거나 파일을 드래그하세요</span>
            </div>
            <button class="btn btn-primary upload-panel-btn" id="btn-catalog" disabled>업로드</button>
          </div>

          <div class="upload-panel" id="panel-hash">
            <div class="upload-panel-header">
              <span class="upload-panel-title">카탈로그 해시 파일</span>
              <span class="upload-panel-status" id="status-hash"></span>
            </div>
            <div id="alert-hash" hidden></div>
            <div class="file-drop" id="drop-hash">
              <input type="file" id="file-hash" class="file-drop-input" />
              <span class="file-drop-text" id="drop-hash-text">클릭하거나 파일을 드래그하세요</span>
            </div>
            <button class="btn btn-primary upload-panel-btn" id="btn-hash" disabled>업로드</button>
          </div>
        </div>
      </div>
      </div>
    `;

    const preselect = _uploadPreselect;
    _uploadPreselect = null;

    loadUploadGameOptions(preselect);
    setupFileDrop('drop-catalog', 'file-catalog', 'drop-catalog-text');
    setupFileDrop('drop-hash', 'file-hash', 'drop-hash-text');

    document.getElementById('file-catalog').addEventListener('change', () => updateUploadBtnState('catalog'));
    document.getElementById('file-hash').addEventListener('change', () => updateUploadBtnState('hash'));
    document.getElementById('catalog-game').addEventListener('change', () => {
      loadUploadPatchOptions();
      updateUploadBtnState('catalog');
      updateUploadBtnState('hash');
    });
    document.getElementById('catalog-patch').addEventListener('change', () => {
      updateUploadBtnState('catalog');
      updateUploadBtnState('hash');
      loadCurrentPatchStatus();
    });

    // 카탈로그 업로드 버튼
    document.getElementById('btn-catalog').addEventListener('click', async () => {
      const gameId  = Number(document.getElementById('catalog-game').value);
      const patchId = Number(document.getElementById('catalog-patch').value);
      const file    = document.getElementById('file-catalog').files[0];

      if (!gameId || !patchId || !file) return;

      setPanelLoading('catalog', true);
      setPanelAlert('catalog', '');

      try {
        const res = await api.uploadCatalog(patchId, file);
        setPanelAlert('catalog', 'success', '완료 — catalog.json');
        document.getElementById('file-catalog').value = '';
        document.getElementById('drop-catalog-text').textContent = '클릭하거나 파일을 드래그하세요';
        document.getElementById('drop-catalog').classList.remove('file-drop-filled');
        setStatusBadge('catalog', true);
        updateUploadBtnState('catalog');
      } catch (err) {
        setPanelAlert('catalog', 'error', err.message || '업로드 실패');
      } finally {
        setPanelLoading('catalog', false);
      }
    });

    // 해시 업로드 버튼
    document.getElementById('btn-hash').addEventListener('click', async () => {
      const gameId  = Number(document.getElementById('catalog-game').value);
      const patchId = Number(document.getElementById('catalog-patch').value);
      const file    = document.getElementById('file-hash').files[0];

      if (!gameId || !patchId || !file) return;

      setPanelLoading('hash', true);
      setPanelAlert('hash', '');

      try {
        const res = await api.uploadCatalogHash(patchId, file);
        setPanelAlert('hash', 'success', '완료 — catalog.hash');
        document.getElementById('file-hash').value = '';
        document.getElementById('drop-hash-text').textContent = '클릭하거나 파일을 드래그하세요';
        document.getElementById('drop-hash').classList.remove('file-drop-filled');
        setStatusBadge('hash', true);
        updateUploadBtnState('hash');
      } catch (err) {
        setPanelAlert('hash', 'error', err.message || '업로드 실패');
      } finally {
        setPanelLoading('hash', false);
      }
    });

    if (preselect?.target === 'catalog') {
      document.getElementById('panel-catalog').classList.add('upload-panel-highlight');
    } else if (preselect?.target === 'hash') {
      document.getElementById('panel-hash').classList.add('upload-panel-highlight');
    }
  }

  function updateUploadBtnState(type) {
    const gameId  = Number(document.getElementById('catalog-game')?.value);
    const patchId = Number(document.getElementById('catalog-patch')?.value);
    const file    = document.getElementById(`file-${type}`)?.files[0];
    const btn     = document.getElementById(`btn-${type}`);
    if (btn) btn.disabled = !(gameId && patchId && file);
  }

  async function loadCurrentPatchStatus() {
    const patchId = Number(document.getElementById('catalog-patch')?.value);
    if (!patchId) return;

    try {
      const [catalogRes, hashRes] = await Promise.all([
        api.checkCatalogUploaded(patchId),
        api.checkCatalogHashUploaded(patchId),
      ]);
      setStatusBadge('catalog', !!catalogRes.data?.uploaded);
      setStatusBadge('hash', !!hashRes.data?.uploaded);
    } catch { /* ignore */ }
  }

  function setStatusBadge(type, uploaded) {
    const el = document.getElementById(`status-${type}`);
    if (!el) return;
    if (uploaded) {
      el.textContent = '업로드됨';
      el.className = 'upload-panel-status upload-panel-status-ok';
    } else {
      el.textContent = '미업로드';
      el.className = 'upload-panel-status upload-panel-status-none';
    }
  }

  function setPanelAlert(type, level, msg) {
    const el = document.getElementById(`alert-${type}`);
    if (!el) return;
    if (!level) { el.hidden = true; el.textContent = ''; return; }
    el.className = `alert alert-${level}`;
    el.textContent = msg;
    el.hidden = false;
  }

  function setPanelLoading(type, on) {
    const btn = document.getElementById(`btn-${type}`);
    if (!btn) return;
    if (on) {
      btn.disabled = true;
      btn.innerHTML = '<span class="spinner"></span>';
    } else {
      btn.innerHTML = '업로드';
      updateUploadBtnState(type);
    }
  }

  async function loadUploadGameOptions(preselect) {
    const sel = document.getElementById('catalog-game');
    if (!sel) return;
    try {
      const [gamesRes, devsRes] = await Promise.all([api.getGameList(), api.getDeveloperList()]);
      const games  = gamesRes.data ?? [];
      const devMap = Object.fromEntries((devsRes.data ?? []).map(d => [d.id, d.name]));

      if (games.length === 0) {
        sel.innerHTML = `<option value="" disabled selected>등록된 게임이 없습니다</option>`;
        return;
      }

      const groups = {};
      games.forEach(g => {
        const key = devMap[g.developerId] ?? `개발사 #${g.developerId}`;
        (groups[key] ??= []).push(g);
      });

      sel.innerHTML = `<option value="" disabled selected>게임 선택</option>` +
        Object.entries(groups).map(([devName, devGames]) =>
          `<optgroup label="${escapeHtml(devName)}">` +
          devGames.map(g => `<option value="${g.id}">${escapeHtml(g.title)}</option>`).join('') +
          `</optgroup>`
        ).join('');

      if (preselect?.gameId) {
        sel.value = String(preselect.gameId);
        await loadUploadPatchOptions(preselect.patchId);
      }
    } catch {
      sel.innerHTML = `<option value="" disabled selected>불러오기 실패</option>`;
    }
  }

  async function loadUploadPatchOptions(preselectPatchId) {
    const gameId   = Number(document.getElementById('catalog-game').value);
    const patchSel = document.getElementById('catalog-patch');
    if (!patchSel || !gameId) return;

    patchSel.disabled = true;
    patchSel.innerHTML = `<option value="" disabled selected>불러오는 중...</option>`;

    try {
      const res = await api.getPatchList(gameId);
      const patches = res.data ?? [];

      if (patches.length === 0) {
        patchSel.innerHTML = `<option value="" disabled selected>패치가 없습니다</option>`;
        return;
      }

      patchSel.innerHTML = `<option value="" disabled selected>패치 선택</option>` +
        patches.map(p => {
          const hasCat  = p.catalogUploaded ? 'C' : '';
          const hasHash = p.catalogHashUploaded ? 'H' : '';
          const badges  = hasCat || hasHash ? ` [${hasCat}${hasHash}]` : '';
          const label = `${p.version} / ${PLATFORM_LABEL[p.platform] ?? p.platform}${badges}`;
          return `<option value="${p.id}">${escapeHtml(label)}</option>`;
        }).join('');
      patchSel.disabled = false;

      if (preselectPatchId) {
        patchSel.value = String(preselectPatchId);
        loadCurrentPatchStatus();
      }
    } catch {
      patchSel.innerHTML = `<option value="" disabled selected>불러오기 실패</option>`;
    }
  }

  function setupFileDrop(dropId, inputId, textId) {
    const drop  = document.getElementById(dropId);
    const input = document.getElementById(inputId);
    const text  = document.getElementById(textId);
    if (!drop || !input || !text) return;

    drop.addEventListener('click', () => input.click());

    input.addEventListener('change', () => {
      if (input.files.length > 0) {
        text.textContent = input.files[0].name;
        drop.classList.add('file-drop-filled');
      } else {
        text.textContent = '클릭하거나 파일을 드래그하세요';
        drop.classList.remove('file-drop-filled');
      }
    });

    drop.addEventListener('dragover', (e) => { e.preventDefault(); drop.classList.add('file-drop-over'); });
    drop.addEventListener('dragleave', () => { drop.classList.remove('file-drop-over'); });
    drop.addEventListener('drop', (e) => {
      e.preventDefault();
      drop.classList.remove('file-drop-over');
      if (e.dataTransfer.files.length > 0) {
        input.files = e.dataTransfer.files;
        input.dispatchEvent(new Event('change'));
      }
    });
  }

  return { renderList, renderEdit, renderNew, renderUpload };
})();
