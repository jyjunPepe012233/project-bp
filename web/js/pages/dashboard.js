const dashboardPage = (() => {
  function render(container) {
    setPageTitle('대시보드');
    container.innerHTML = `
      <div class="card">
        <div class="card-title">BP Admin Console</div>
        <p style="color:var(--text-sub)">왼쪽 메뉴에서 작업을 선택하세요.</p>
      </div>
    `;
  }

  return { render };
})();
