const validation = (() => {
  const RULES = {
    ACCOUNT_NAME: /^[a-zA-Z0-9_.\-]{3,50}$/,
    PASSWORD:     /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z0-9!@#$%^&*()_+=\-~]{8,72}$/,
    DISPLAY_NAME: /^[a-zA-Z0-9가-힣 _.\-]{2,100}$/,
    VERSION:      /^[a-zA-Z0-9][a-zA-Z0-9._\-]{0,49}$/,
  };

  const MESSAGES = {
    ACCOUNT_NAME: '계정명은 영문, 숫자, 특수문자(_ . -)만 사용할 수 있습니다. (3~50자)',
    PASSWORD:     '비밀번호는 영문과 숫자를 각각 1자 이상 포함해야 합니다. (8~72자)',
    DISPLAY_NAME: '이름에 사용할 수 없는 문자가 포함되어 있습니다. (2~100자)',
    VERSION:      '버전 형식이 올바르지 않습니다. (영문, 숫자, . _ - 사용 가능, 최대 50자)',
  };

  function required(value, message) {
    if (!value || !value.trim()) return message;
    return null;
  }

  function maxLength(value, max, message) {
    if (value && value.length > max) return message;
    return null;
  }

  function pattern(value, ruleName, customMessage) {
    if (!value) return null;
    const regex = RULES[ruleName];
    if (!regex) return null;
    if (!regex.test(value)) return customMessage || MESSAGES[ruleName];
    return null;
  }

  // checks: array of error strings (null = pass)
  // returns first error or null
  function check(...checks) {
    for (const err of checks) {
      if (err) return err;
    }
    return null;
  }

  return { RULES, MESSAGES, required, maxLength, pattern, check };
})();
