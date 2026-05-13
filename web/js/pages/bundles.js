const bundlesPage = (() => {

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

  function render(container) {
    setPageTitle('번들 관리');
    container.innerHTML = `
      <div class="content-narrow">
        <div class="card">
          <div class="card-title">번들 관리</div>
          <p style="font-size:12px;color:var(--text-sub);margin-bottom:16px">게임을 선택하면 플랫폼별 번들 파일을 확인하고 업로드할 수 있습니다.</p>
          <div class="form-row">
            <div class="form-group" style="flex:1">
              <label class="form-label">게임</label>
              <select id="bundle-game" class="form-input">
                <option value="" disabled selected>불러오는 중...</option>
              </select>
            </div>
            <div class="form-group" style="flex:1">
              <label class="form-label">플랫폼</label>
              <select id="bundle-platform" class="form-input">
                <option value="" disabled selected>플랫폼 선택</option>
                <option value="ANDROID">Android</option>
                <option value="IOS">iOS</option>
                <option value="STANDALONE_OSX">StandaloneOSX</option>
              </select>
            </div>
          </div>
        </div>

        ${permissions.canUploadBundle() ? `
        <div class="card" style="margin-top:12px">
          <div class="card-title">번들 업로드</div>
          <div id="alert-bundle" hidden></div>
          <div class="file-drop" id="drop-bundle">
            <input type="file" id="file-bundle" class="file-drop-input" multiple />
            <span class="file-drop-text" id="drop-bundle-text">클릭하거나 파일을 드래그하세요 (여러 파일 선택 가능)</span>
          </div>
          <div id="bundle-file-list-preview" hidden></div>
          <button class="btn btn-primary upload-panel-btn" id="btn-bundle" disabled style="margin-top:10px">업로드</button>
          <div id="bundle-upload-progress" hidden></div>
        </div>
        ` : ''}

        <div id="bundle-result"></div>
      </div>
    `;

    loadGameOptions();

    const canUpload = permissions.canUploadBundle();
    if (canUpload) {
      setupFileDrop('drop-bundle', 'file-bundle', 'drop-bundle-text');
      document.getElementById('file-bundle').addEventListener('change', () => {
        updateBtnState();
        renderFilePreview();
      });
      document.getElementById('btn-bundle').addEventListener('click', uploadBundles);
    }

    document.getElementById('bundle-game').addEventListener('change', () => {
      loadBundles();
      if (canUpload) updateBtnState();
    });
    if (canUpload) document.getElementById('bundle-platform').addEventListener('change', updateBtnState);
  }

  function updateBtnState() {
    const gameId   = Number(document.getElementById('bundle-game')?.value);
    const platform = document.getElementById('bundle-platform')?.value;
    const files    = document.getElementById('file-bundle')?.files;
    const btn      = document.getElementById('btn-bundle');
    if (btn) btn.disabled = !(gameId && platform && files && files.length > 0);
  }

  function renderFilePreview() {
    const input   = document.getElementById('file-bundle');
    const preview = document.getElementById('bundle-file-list-preview');
    if (!input || !preview) return;

    const files = [...input.files];
    if (files.length === 0) {
      preview.hidden = true;
      preview.innerHTML = '';
      return;
    }

    preview.hidden = false;
    preview.innerHTML = `
      <div class="bundle-preview-header">${files.length}개 파일 선택됨</div>
      <div class="bundle-preview-list">
        ${files.map((f, i) => `<div class="bundle-preview-item" data-idx="${i}">
          <span class="bundle-preview-name">${escapeHtml(f.name)}</span>
          <span class="bundle-preview-size">${formatFileSize(f.size)}</span>
          <button class="bundle-preview-remove" data-idx="${i}">&times;</button>
        </div>`).join('')}
      </div>
    `;

    preview.querySelectorAll('.bundle-preview-remove').forEach(btn => {
      btn.addEventListener('click', () => {
        const idx = Number(btn.dataset.idx);
        removeFileAt(idx);
      });
    });
  }

  function removeFileAt(idx) {
    const input = document.getElementById('file-bundle');
    if (!input) return;
    const dt = new DataTransfer();
    [...input.files].forEach((f, i) => { if (i !== idx) dt.items.add(f); });
    input.files = dt.files;
    input.dispatchEvent(new Event('change'));
  }

  function formatFileSize(bytes) {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  async function loadGameOptions() {
    const sel = document.getElementById('bundle-game');
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
          devGames.map(g => `<option value="${g.id}" data-uuid="${g.uuid}">${escapeHtml(g.title)}</option>`).join('') +
          `</optgroup>`
        ).join('');
    } catch {
      sel.innerHTML = `<option value="" disabled selected>불러오기 실패</option>`;
    }
  }

  async function uploadBundles() {
    const gameId   = Number(document.getElementById('bundle-game').value);
    const platform = document.getElementById('bundle-platform').value;
    const files    = [...document.getElementById('file-bundle').files];
    if (!gameId || !platform || !files.length) return;

    const btn        = document.getElementById('btn-bundle');
    const alertEl    = document.getElementById('alert-bundle');
    const progressEl = document.getElementById('bundle-upload-progress');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span>';
    if (alertEl) { alertEl.hidden = true; alertEl.innerHTML = ''; }

    const total = files.length;
    let succeeded = 0;
    let failed = 0;
    const errors = [];

    progressEl.hidden = false;

    for (let i = 0; i < total; i++) {
      const file = files[i];
      progressEl.innerHTML = `<div class="bundle-progress-bar">
        <div class="bundle-progress-fill" style="width:${Math.round((i / total) * 100)}%"></div>
      </div>
      <div class="bundle-progress-text">${i + 1} / ${total} 업로드 중… <strong>${escapeHtml(file.name)}</strong></div>`;

      try {
        await api.uploadBundle(gameId, platform, file);
        succeeded++;
      } catch (err) {
        failed++;
        errors.push(`${file.name}: ${err.message || '실패'}`);
      }
    }

    progressEl.innerHTML = `<div class="bundle-progress-bar">
      <div class="bundle-progress-fill" style="width:100%"></div>
    </div>
    <div class="bundle-progress-text">완료</div>`;
    setTimeout(() => { progressEl.hidden = true; }, 2000);

    if (alertEl) {
      if (failed === 0) {
        alertEl.className = 'alert alert-success';
        alertEl.textContent = `${succeeded}개 파일 업로드 완료`;
      } else if (succeeded === 0) {
        alertEl.className = 'alert alert-error';
        alertEl.innerHTML = `전체 실패<br>${errors.map(e => escapeHtml(e)).join('<br>')}`;
      } else {
        alertEl.className = 'alert alert-error';
        alertEl.innerHTML = `${succeeded}개 성공, ${failed}개 실패<br>${errors.map(e => escapeHtml(e)).join('<br>')}`;
      }
      alertEl.hidden = false;
    }

    document.getElementById('file-bundle').value = '';
    document.getElementById('drop-bundle-text').textContent = '클릭하거나 파일을 드래그하세요 (여러 파일 선택 가능)';
    document.getElementById('drop-bundle').classList.remove('file-drop-filled');
    document.getElementById('bundle-file-list-preview').hidden = true;
    document.getElementById('bundle-file-list-preview').innerHTML = '';
    btn.innerHTML = '업로드';
    updateBtnState();
    loadBundles();
  }

  async function loadBundles() {
    const sel = document.getElementById('bundle-game');
    const result = document.getElementById('bundle-result');
    if (!sel || !result) return;

    const gameId = Number(sel.value);
    const selectedOption = sel.options[sel.selectedIndex];
    const gameUuid = selectedOption?.dataset.uuid ?? '';
    if (!gameId) {
      result.innerHTML = '';
      return;
    }

    result.innerHTML = `<div class="card" style="margin-top:12px"><p class="patch-empty-msg"><span class="spinner" style="border-color:var(--border-dark);border-top-color:var(--text)"></span></p></div>`;

    try {
      const res = await api.getGameBundleList(gameId);
      const platforms = res.data?.platforms ?? [];

      if (platforms.every(p => p.filenames.length === 0)) {
        result.innerHTML = `<div class="card" style="margin-top:12px"><p class="patch-empty-msg">업로드된 번들 파일이 없습니다.</p></div>`;
        return;
      }

      const apiBase = `${location.protocol}//${location.hostname}:8080`;

      result.innerHTML = platforms.map(p => {
        const count = p.filenames.length;
        const countLabel = count > 0
          ? `<span style="color:var(--accent);font-weight:600">${count}개 파일</span>`
          : `<span style="color:var(--text-sub)">파일 없음</span>`;

        const fileRows = count > 0
          ? p.filenames.map(name => {
              const dlPath = `${apiBase}/bundles/${gameUuid}/${escapeHtml(p.platform)}/bundles/${escapeHtml(name)}`;
              return `<div class="bundle-file-row">
                <span class="bundle-file-name">${escapeHtml(name)}</span>
                <span class="catalog-url-box"><span class="catalog-method-badge">GET</span><span class="bundle-file-path">${escapeHtml(dlPath)}</span></span>
                <button class="catalog-copy-btn catalog-download-btn" data-url="${escapeHtml(dlPath)}" data-filename="${escapeHtml(name)}">다운로드</button>
                <button class="catalog-copy-btn bundle-copy-btn" data-path="${escapeHtml(dlPath)}">경로 복사</button>
              </div>`;
            }).join('')
          : `<p class="patch-empty-msg" style="margin:8px 0">이 플랫폼에 업로드된 번들이 없습니다.</p>`;

        return `<div class="card" style="margin-top:12px">
          <div class="card-title" style="display:flex;align-items:center;gap:10px">
            ${escapeHtml(p.platform)}
            ${countLabel}
          </div>
          <div class="bundle-list-body">${fileRows}</div>
        </div>`;
      }).join('');

      result.querySelectorAll('.bundle-copy-btn').forEach(btn => {
        btn.addEventListener('click', () => {
          navigator.clipboard.writeText(btn.dataset.path).then(() => {
            const orig = btn.textContent;
            btn.textContent = '복사됨';
            btn.disabled = true;
            setTimeout(() => { btn.textContent = orig; btn.disabled = false; }, 1500);
          });
        });
      });

      result.querySelectorAll('.catalog-download-btn').forEach(btn => {
        btn.addEventListener('click', () => downloadFile(btn.dataset.url, btn.dataset.filename, btn));
      });
    } catch (err) {
      result.innerHTML = `<div class="card" style="margin-top:12px"><p class="patch-empty-msg" style="color:var(--error)">${escapeHtml(err.message || '번들 목록을 불러오지 못했습니다.')}</p></div>`;
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
        text.textContent = input.files.length === 1
          ? input.files[0].name
          : `${input.files.length}개 파일 선택됨`;
        drop.classList.add('file-drop-filled');
      } else {
        text.textContent = input.multiple
          ? '클릭하거나 파일을 드래그하세요 (여러 파일 선택 가능)'
          : '클릭하거나 파일을 드래그하세요';
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

  return { render };
})();
