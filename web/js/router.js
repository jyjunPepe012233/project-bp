const router = (() => {
  // PUBLIC_ROUTES: 로그인 없이 접근 가능한 경로
  const PUBLIC_ROUTES = ['/developers/new'];

  const routes = {};

  function register(path, handler) {
    routes[path] = handler;
  }

  function currentPath() {
    return location.hash.replace(/^#/, '') || '/';
  }

  function showView(viewId) {
    ['login-view', 'public-view', 'app'].forEach(id => {
      document.getElementById(id).hidden = (id !== viewId);
    });
  }

  function resolve() {
    const path = currentPath();

    if (!store.isLoggedIn()) {
      if (PUBLIC_ROUTES.includes(path)) {
        showView('public-view');
        const handler = routes[path];
        if (handler) handler(document.getElementById('public-content'));
      } else {
        showView('login-view');
      }
      return;
    }

    showView('app');
    updateActiveNav(path);

    const handler = routes[path] || routes['/'];
    if (handler) handler(document.getElementById('content'));
  }

  function updateActiveNav(path) {
    document.querySelectorAll('.nav-link').forEach(a => {
      a.classList.toggle('active', a.dataset.path === path);
    });
  }

  window.addEventListener('hashchange', resolve);

  return { resolve, register };
})();
