const loginPage = (() => {
  function init() {
    const form    = document.getElementById('login-form');
    const errBox  = document.getElementById('login-error');
    const nameIn  = document.getElementById('input-name');
    const passIn  = document.getElementById('input-password');
    const btn     = document.getElementById('login-btn');

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      setError('');

      const name     = nameIn.value.trim();
      const password = passIn.value;

      if (!name || !password) {
        setError('계정명과 비밀번호를 입력해주세요.');
        return;
      }

      setLoading(true);

      try {
        const res = await api.login(name, password);
        store.setTokens(res.data.accessToken, res.data.refreshToken);
        const me = await api.getMyAccount();
        store.setAccountName(me.data.name);
        store.setAccountId(me.data.id);
        await permissions.load();
        location.hash = '/';
        router.resolve();
        updateSidebar();
        updateSidebarAccount();
      } catch (err) {
        setError(err.message || '로그인에 실패했습니다.');
        passIn.value = '';
        passIn.focus();
      } finally {
        setLoading(false);
      }
    });

    function setError(msg) {
      if (msg) {
        errBox.textContent = msg;
        errBox.className = 'alert alert-error';
        errBox.hidden = false;
      } else {
        errBox.hidden = true;
        errBox.textContent = '';
      }
    }

    function setLoading(on) {
      btn.disabled = on;
      btn.innerHTML = on
        ? '<span class="spinner"></span>'
        : '로그인';
    }
  }

  return { init };
})();
