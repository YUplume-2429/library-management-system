import { nextTick, ref, watch } from 'vue'

const STORAGE_KEY = 'library-language'
const ATTRIBUTES = ['placeholder', 'title', 'aria-label']

const translations = {
  zh: {
    'Library System': '图书管理系统',
    'Books': '图书',
    'Dashboard': '仪表盘',
    'My Borrows': '我的借阅',
    'Admin': '管理',
    'my': '我的',
    'Logout': '退出登录',
    'Login': '登录',
    'Language': '语言',
    'English': 'English',
    'Chinese': '中文',
    'Logged out': '已退出登录',

    'Username': '用户名',
    'Password': '密码',
    'Enter username': '请输入用户名',
    'Enter password': '请输入密码',
    'Reset': '重置',
    'Admin: admin / admin123': '管理员：admin / admin123',
    'Student: student1 / student123': '学生：student1 / student123',
    'Please enter username and password': '请输入用户名和密码',
    'Login success': '登录成功',
    'Login failed': '登录失败',

    'Name': '姓名',
    'Role': '角色',
    'Status': '状态',
    'Logged in': '已登录',
    'Open Books': '打开图书',
    'Open Admin': '打开管理',

    'Search by title': '按书名搜索',
    'All categories': '全部分类',
    'Title': '书名',
    'Author': '作者',
    'Publisher': '出版社',
    'Category': '分类',
    'Price': '价格',
    'Stock': '库存',
    'Actions': '操作',
    'Detail': '详情',
    'Borrowed': '已借阅',
    'Borrow': '借阅',
    'Edit': '编辑',
    'Delete': '删除',
    'Delete this book?': '确定删除这本书吗？',
    'New Book': '新增图书',
    'Book Details': '图书详情',
    'Publish Date': '出版日期',
    'Description': '描述',
    'Edit Book': '编辑图书',
    'Cancel': '取消',
    'Save': '保存',
    'N/A': '无',
    'Please fill in required fields': '请填写必填字段',
    'Updated successfully': '更新成功',
    'Created successfully': '创建成功',
    'Save failed': '保存失败',
    'Deleted successfully': '删除成功',
    'Delete failed': '删除失败',
    'Borrowed successfully': '借阅成功',
    'Borrow failed': '借阅失败',
    'Failed to load books': '加载图书失败',
    'Failed to load categories': '加载分类失败',
    'Failed to load borrow records': '加载借阅记录失败',
    'Search failed': '搜索失败',

    'My Borrow Records': '我的借阅记录',
    'Track active loans, due dates and returned books.': '查看当前借阅、到期日期和已归还图书。',
    'Refresh': '刷新',
    'Borrowing': '借阅中',
    'Returned': '已归还',
    'Due Soon': '即将到期',
    'No borrow records yet': '暂无借阅记录',
    'Find Books': '查找图书',
    'Book': '图书',
    'Borrow Date': '借阅日期',
    'Due Date': '到期日期',
    'Return Date': '归还日期',
    'Return': '归还',
    'Done': '完成',
    'Returned successfully': '归还成功',
    'Return failed': '归还失败',

    'My Profile': '我的资料',
    'View your account and update allowed information.': '查看账号信息并更新允许修改的内容。',
    'Active': '启用',
    'Disabled': '禁用',
    'Created At': '创建时间',
    'Email': '邮箱',
    'Current Password': '当前密码',
    'New Password': '新密码',
    'Confirm Password': '确认密码',
    'Required only when changing password': '仅修改密码时需要填写',
    'Leave blank to keep current password': '留空则保持当前密码',
    'Repeat new password': '再次输入新密码',
    'Save Changes': '保存修改',
    'Failed to load profile': '加载个人资料失败',
    'Name cannot be empty': '姓名不能为空',
    'New password confirmation does not match': '两次输入的新密码不一致',
    'Profile updated successfully': '个人资料更新成功',
    'Save profile failed': '保存个人资料失败',

    'Page not found': '页面不存在',
    'Back to books': '返回图书',

    'Admin Panel': '管理面板',
    'Admin only': '仅管理员可用',
    'Import CSV': '导入 CSV',
    'Download Template': '下载模板',
    'Export Books CSV': '导出图书 CSV',
    'Users': '用户',
    'Create User': '创建用户',
    'Refresh Users': '刷新用户',
    'Import Users CSV': '导入用户 CSV',
    'Download User Template': '下载用户模板',
    'Export Users CSV': '导出用户 CSV',
    'Search username or name': '搜索用户名或姓名',
    'All roles': '全部角色',
    'Search': '搜索',
    'Clear': '清空',
    'Close': '关闭',
    'Login username': '登录用户名',
    'Initial password': '初始密码',
    'Display name': '显示名称',
    'Optional email': '可选邮箱',
    'Select role': '选择角色',
    'Administrator': '管理员',
    'Student': '学生',
    'Reset Password': '重置密码',
    'Reset this student\'s password to 123456?': '将该学生密码重置为 123456？',
    'Delete this user?': '确定删除该用户吗？',
    'Stats': '统计',
    'Total Books': '图书总数',
    'Total Stock': '总库存',
    'Available Books': '可借图书',
    'Out of Stock': '无库存',
    'Active Borrows': '当前借阅',
    'Category Distribution': '分类分布',
    'No category data': '暂无分类数据',
    'Borrow Records': '借阅记录',
    'Refresh Records': '刷新记录',
    'Export Records CSV': '导出记录 CSV',
    'Borrowing Now': '当前借阅',
    'Students With Records': '有记录的学生',
    'Borrowed Books': '被借阅图书',
    'Student Borrow Statistics': '学生借阅统计',
    'Book Borrow Statistics': '图书借阅统计',
    'More': '更多',
    'Total': '总数',
    'Details': '详情',
    'Import Books CSV': '导入图书 CSV',
    'Required columns: title, author, publisher, isbn. Optional columns: category, price, stock, publishDate, description.': '必填列：title、author、publisher、isbn。可选列：category、price、stock、publishDate、description。',
    'Import Users CSV': '导入用户 CSV',
    'Required columns: username, password, name, roleName. Optional columns: email, isActive.': '必填列：username、password、name、roleName。可选列：email、isActive。',
    'Yes': '是',
    'No': '否',
    'All Student Borrow Statistics': '全部学生借阅统计',
    'All Book Borrow Statistics': '全部图书借阅统计',
    'Uncategorized': '未分类',
    'Failed to load users': '加载用户失败',
    'Please fill in username, password, name and role': '请填写用户名、密码、姓名和角色',
    'User created successfully': '用户创建成功',
    'Create user failed': '创建用户失败',
    'User deleted successfully': '用户删除成功',
    'Delete user failed': '删除用户失败',
    'Reset password failed': '重置密码失败',
    'This user has active borrow records.': '该用户存在未归还的借阅记录。',
    'If you continue, all borrowed books will be treated as returned, the related borrow records will be deleted, and the user will be removed. Continue?': '如果继续，所有借阅图书将按已归还处理，相关借阅记录会被删除，并删除该用户。是否继续？',
    'User Has Active Borrow Records': '用户存在未归还借阅',
    'Force Delete': '强制删除',
    'User force deleted successfully': '用户已强制删除',
    'Force delete user failed': '强制删除用户失败',
    'CSV must include a header row and at least one data row': 'CSV 必须包含表头和至少一行数据',
    'Missing required column: roleName': '缺少必填列：roleName',
    'Failed to parse CSV': '解析 CSV 失败',
    'Failed to read CSV file': '读取 CSV 文件失败',
    'Import failed': '导入失败',
    'Import users failed': '导入用户失败'
  }
}

const textNodeOriginals = new WeakMap()
const attributeOriginals = new WeakMap()
const language = ref(localStorage.getItem(STORAGE_KEY) || 'en')
let observer

const preserveWhitespace = (original, translated) => {
  const leading = original.match(/^\s*/)?.[0] || ''
  const trailing = original.match(/\s*$/)?.[0] || ''
  return `${leading}${translated}${trailing}`
}

export const translate = (text) => {
  if (language.value !== 'zh' || !text) {
    return text
  }

  const trimmed = String(text).trim()
  const dictionary = translations.zh
  if (dictionary[trimmed]) {
    return preserveWhitespace(String(text), dictionary[trimmed])
  }

  const importBooksMatch = trimmed.match(/^Import (\d+) Books$/)
  if (importBooksMatch) {
    return preserveWhitespace(String(text), `导入 ${importBooksMatch[1]} 本图书`)
  }

  const importUsersMatch = trimmed.match(/^Import (\d+) Users$/)
  if (importUsersMatch) {
    return preserveWhitespace(String(text), `导入 ${importUsersMatch[1]} 个用户`)
  }

  const importedBooksMatch = trimmed.match(/^Imported (\d+) books$/)
  if (importedBooksMatch) {
    return preserveWhitespace(String(text), `已导入 ${importedBooksMatch[1]} 本图书`)
  }

  const importedUsersMatch = trimmed.match(/^Imported (\d+) users$/)
  if (importedUsersMatch) {
    return preserveWhitespace(String(text), `已导入 ${importedUsersMatch[1]} 个用户`)
  }

  const passwordResetMatch = trimmed.match(/^(.+)'s password has been reset to 123456$/)
  if (passwordResetMatch) {
    return preserveWhitespace(String(text), `${passwordResetMatch[1]} 的密码已重置为 123456`)
  }

  const rowMissingMatch = trimmed.match(/^Row (\d+) is missing (.+)$/)
  if (rowMissingMatch) {
    return preserveWhitespace(String(text), `第 ${rowMissingMatch[1]} 行缺少 ${rowMissingMatch[2]}`)
  }

  const rowInvalidRoleMatch = trimmed.match(/^Row (\d+) has invalid roleName: (.+)$/)
  if (rowInvalidRoleMatch) {
    return preserveWhitespace(String(text), `第 ${rowInvalidRoleMatch[1]} 行 roleName 无效：${rowInvalidRoleMatch[2]}`)
  }

  const rowInvalidNumberMatch = trimmed.match(/^Row (\d+) has invalid price or stock$/)
  if (rowInvalidNumberMatch) {
    return preserveWhitespace(String(text), `第 ${rowInvalidNumberMatch[1]} 行价格或库存无效`)
  }

  const missingColumnsMatch = trimmed.match(/^Missing required columns: (.+)$/)
  if (missingColumnsMatch) {
    return preserveWhitespace(String(text), `缺少必填列：${missingColumnsMatch[1]}`)
  }

  const studentRecordsMatch = trimmed.match(/^(.+) Borrow Records$/)
  if (studentRecordsMatch) {
    return preserveWhitespace(String(text), `${studentRecordsMatch[1]} 的借阅记录`)
  }

  return text
}

export const setLanguage = (value) => {
  language.value = value
  localStorage.setItem(STORAGE_KEY, value)
}

export const useI18n = () => {
  return {
    language,
    setLanguage,
    t: translate
  }
}

const translateTextNode = (node) => {
  const value = node.nodeValue || ''
  if (!value.trim()) {
    return
  }
  if (!textNodeOriginals.has(node)) {
    textNodeOriginals.set(node, value)
  }
  node.nodeValue = translate(textNodeOriginals.get(node))
}

const translateAttributes = (element) => {
  for (const attribute of ATTRIBUTES) {
    if (!element.hasAttribute(attribute)) {
      continue
    }

    let originals = attributeOriginals.get(element)
    if (!originals) {
      originals = {}
      attributeOriginals.set(element, originals)
    }

    if (!originals[attribute]) {
      originals[attribute] = element.getAttribute(attribute)
    }

    const translated = translate(originals[attribute])
    if (element.getAttribute(attribute) !== translated) {
      element.setAttribute(attribute, translated)
    }
  }
}

const translateNode = (node) => {
  if (node.nodeType === Node.TEXT_NODE) {
    translateTextNode(node)
    return
  }

  if (node.nodeType !== Node.ELEMENT_NODE) {
    return
  }

  const tagName = node.tagName?.toLowerCase()
  if (tagName === 'script' || tagName === 'style') {
    return
  }

  translateAttributes(node)
  for (const child of node.childNodes) {
    translateNode(child)
  }
}

export const translateDocument = () => {
  nextTick(() => {
    translateNode(document.body)
  })
}

export const installDomI18n = () => {
  translateDocument()

  observer = new MutationObserver((mutations) => {
    for (const mutation of mutations) {
      for (const node of mutation.addedNodes) {
        translateNode(node)
      }

      if (mutation.type === 'attributes') {
        translateAttributes(mutation.target)
      }
    }
  })

  observer.observe(document.body, {
    childList: true,
    subtree: true,
    attributes: true,
    attributeFilter: ATTRIBUTES
  })

  watch(language, translateDocument)
}
