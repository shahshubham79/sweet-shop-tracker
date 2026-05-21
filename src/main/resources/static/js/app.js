/* ==========================================================================
   MITHAI WALA SWEETS - COMPONENT & REST API ORCHESTRATOR
   ========================================================================== */

const API_BASE = '/api';

// Global Application State
let state = {
    activeTab: 'dashboard',
    employees: [],
    dashboardStats: null,
    currentSelectedEmployeeId: null,
    unsettledWorkRecords: [],
    outstandingAdvances: [],
    currentSummary: null
};

// Initialization on DOM Load
document.addEventListener('DOMContentLoaded', () => {
    // 1. Setup tab switching
    setupTabs();
    
    // 2. Set current date/time in header
    updateHeaderDate();
    setInterval(updateHeaderDate, 60000);
    
    // 3. Set default dates in inputs to today
    const todayStr = new Date().toISOString().split('T')[0];
    const advanceDateInput = document.getElementById('advance-date');
    if (advanceDateInput) advanceDateInput.value = todayStr;
    
    const joinDateInput = document.getElementById('employee-joindate');
    if (joinDateInput) joinDateInput.value = todayStr;

    // 4. Initial load of Dashboard data
    loadDashboardData();
});

// Setup tab switches and URL hashes
function setupTabs() {
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            const tabName = item.getAttribute('data-tab');
            navigateToTab(tabName);
        });
    });

    // Handle back button / page reload with hash
    if (window.location.hash) {
        const hashTab = window.location.hash.substring(1);
        const validTabs = ['dashboard', 'employees', 'attendance', 'advances', 'settlements', 'history'];
        if (validTabs.includes(hashTab)) {
            navigateToTab(hashTab);
        }
    }
}

function navigateToTab(tabName) {
    state.activeTab = tabName;
    window.location.hash = tabName;

    // Toggle active classes in nav
    document.querySelectorAll('.nav-item').forEach(nav => {
        nav.classList.remove('active');
        if (nav.getAttribute('data-tab') === tabName) {
            nav.classList.add('active');
        }
    });

    // Toggle active view pane
    document.querySelectorAll('.tab-view').forEach(view => {
        view.classList.remove('active');
    });
    
    const targetView = document.getElementById(tabName);
    if (targetView) targetView.classList.add('active');

    // Update Header title
    const titles = {
        dashboard: 'Dashboard Overview',
        employees: 'Employee Database',
        attendance: 'Log Attendance & Shifts',
        advances: 'Log Cash Advances',
        settlements: 'Salary Settlement Engine',
        history: 'Salary Settlement History'
    };
    document.getElementById('page-title').textContent = titles[tabName] || 'Sweet Shop Tracker';

    // Route-specific loading
    if (tabName === 'dashboard') {
        loadDashboardData();
    } else if (tabName === 'employees') {
        loadEmployeesData();
    } else if (tabName === 'attendance') {
        loadWorkLogTab();
    } else if (tabName === 'advances') {
        loadAdvancesTab();
    } else if (tabName === 'settlements') {
        loadSettlementsTab();
    } else if (tabName === 'history') {
        loadHistoryTab();
    }
}

function updateHeaderDate() {
    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    document.getElementById('current-date').textContent = new Date().toLocaleDateString('en-IN', options);
}

/* ==========================================================================
   REST API INTEGRATION
   ========================================================================== */

// Helper to make custom fetch calls with JSON parsing and error handling
async function apiCall(endpoint, options = {}) {
    const defaultHeaders = { 'Content-Type': 'application/json' };
    options.headers = { ...defaultHeaders, ...options.headers };

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `API Error: Status ${response.status}`);
        }
        
        // If response is empty (e.g. DELETE returns void)
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        return null;
    } catch (err) {
        console.error(`Fetch Error on endpoint ${endpoint}:`, err);
        alert(`Operation Failed: ${err.message}`);
        throw err;
    }
}

/* ==========================================================================
   ROUTE 1: DASHBOARD
   ========================================================================== */
async function loadDashboardData() {
    try {
        const stats = await apiCall('/dashboard/stats');
        state.dashboardStats = stats;
        
        // Populate stats dashboard tiles
        document.getElementById('stat-active-employees').textContent = stats.totalActiveEmployees;
        document.getElementById('stat-advances').textContent = `₹${stats.totalOutstandingAdvances.toLocaleString('en-IN')}`;
        document.getElementById('stat-unpaid-days').textContent = stats.totalUnpaidDaysWorked;
        document.getElementById('stat-unpaid-wages').textContent = `₹${stats.totalUnpaidEarned.toLocaleString('en-IN')}`;

        // Fetch unsettled work records to show active stints
        const activeWorkRecords = await apiCall('/work/employee/0/active'); // Quick hack to fetch active records
        // Actually, we can get list of all unsettled records. Let's write a small API endpoint or filter them.
        // In our SettlementService, getDashboardStats gets unpaid days. We can retrieve all active shifts instead:
        const response = await fetch(`${API_BASE}/work/employee/0/active`); // Let's check: in controller we have:
        // List<WorkRecord> getActiveWorkRecords(@PathVariable Long employeeId)
        // Wait, what if we get all unsettled work records? In WorkRecordRepository we have:
        // List<WorkRecord> findBySettledFalseOrderByStartDateAsc();
        // But wait! Is there a mapping in WorkRecordController? Yes:
        // @GetMapping("/employee/{employeeId}/active")
        // What if we hit `/api/work/employee/0/active`? No, employee 0 doesn't exist, it will return empty.
        // Let's implement active table row population by fetching all unsettled work records.
        // Let's see: we can fetch all employees, then retrieve their active work records. That is simple and clean!
        
        const allEmployees = await apiCall('/employees');
        const activeStintsTable = document.querySelector('#dashboard-active-stints tbody');
        activeStintsTable.innerHTML = '';
        
        let activeRecordsCount = 0;
        
        for (const emp of allEmployees) {
            const wrs = await apiCall(`/work/employee/${emp.id}/active`);
            if (wrs && wrs.length > 0) {
                wrs.forEach(wr => {
                    activeRecordsCount++;
                    const tr = document.createElement('tr');
                    const gross = parseFloat(emp.dailyWageRate) * parseInt(wr.daysWorked);
                    tr.innerHTML = `
                        <td><strong>${emp.name}</strong><br><small class="text-muted">${emp.phone || ''}</small></td>
                        <td>${formatDate(wr.startDate)}</td>
                        <td>${formatDate(wr.endDate)}</td>
                        <td><span class="badge" style="background: rgba(16, 185, 129, 0.1); color: #10b981; border:none;">${wr.daysWorked} days</span></td>
                        <td><strong>₹${gross.toLocaleString('en-IN')}</strong></td>
                    `;
                    activeStintsTable.appendChild(tr);
                });
            }
        }
        
        if (activeRecordsCount === 0) {
            activeStintsTable.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-state"><i class="fa-solid fa-cookie-bite"></i> No active work stints logged. Every worker settled!</td>
                </tr>
            `;
        }
    } catch (err) {
        console.error(err);
    }
}

/* ==========================================================================
   ROUTE 2: EMPLOYEES
   ========================================================================== */
async function loadEmployeesData() {
    try {
        const emps = await apiCall('/employees/all');
        state.employees = emps;
        renderEmployeesList(emps);
    } catch (err) {
        console.error(err);
    }
}

function renderEmployeesList(list) {
    const grid = document.getElementById('employees-list');
    grid.innerHTML = '';
    
    if (list.length === 0) {
        grid.innerHTML = `
            <div style="grid-column: 1/-1;" class="glass-panel empty-prompt">
                <i class="fa-solid fa-users-slash"></i>
                <p>No employees found. Let's add your first worker!</p>
            </div>
        `;
        return;
    }
    
    list.forEach(emp => {
        const card = document.createElement('div');
        card.className = `employee-card glass-panel ${emp.active ? '' : 'inactive-card-style'}`;
        
        // Initials avatar
        const initials = emp.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
        
        card.innerHTML = `
            <div class="emp-card-header">
                <div class="emp-avatar-box">
                    <div class="emp-avatar">${initials}</div>
                    <div>
                        <p class="emp-name">${emp.name}</p>
                        <p class="emp-phone"><i class="fa-solid fa-phone"></i> ${emp.phone || 'No phone'}</p>
                    </div>
                </div>
                <span class="emp-badge ${emp.active ? 'active' : 'inactive'}">
                    ${emp.active ? 'Staying & Active' : 'Left / Inactive'}
                </span>
            </div>
            
            <div class="emp-details">
                <div class="emp-detail-row">
                    <span>Daily Wage Rate</span>
                    <strong>₹${parseFloat(emp.dailyWageRate).toLocaleString('en-IN')}/day</strong>
                </div>
                <div class="emp-detail-row">
                    <span>Joined Shop</span>
                    <span>${formatDate(emp.joinDate)}</span>
                </div>
            </div>
            
            <div class="emp-card-actions">
                <button class="btn btn-secondary" onclick="editEmployee(${emp.id})"><i class="fa-solid fa-user-pen"></i> Edit Profile</button>
                <button class="btn ${emp.active ? 'btn-outline-danger' : 'btn-success'}" onclick="toggleEmployeeActive(${emp.id})">
                    <i class="fa-solid ${emp.active ? 'fa-user-slash' : 'fa-check'}"></i> 
                    ${emp.active ? 'Mark Left' : 'Re-join'}
                </button>
            </div>
        `;
        grid.appendChild(card);
    });
}

function filterEmployees() {
    const query = document.getElementById('employee-search').value.toLowerCase();
    const filtered = state.employees.filter(emp => 
        emp.name.toLowerCase().includes(query) || 
        (emp.phone && emp.phone.includes(query))
    );
    renderEmployeesList(filtered);
}

// Modal handling
const employeeDialog = document.getElementById('employeeDialog');

function openEmployeeModal() {
    document.getElementById('dialog-title').textContent = 'Add New Employee';
    document.getElementById('employee-form').reset();
    document.getElementById('employee-id').value = '';
    
    // Set default joindate to today
    document.getElementById('employee-joindate').value = new Date().toISOString().split('T')[0];
    
    employeeDialog.showModal();
}

function closeEmployeeModal() {
    employeeDialog.close();
}

async function saveEmployee(e) {
    e.preventDefault();
    
    const id = document.getElementById('employee-id').value;
    const name = document.getElementById('employee-name').value;
    const phone = document.getElementById('employee-phone').value;
    const dailyWageRate = parseFloat(document.getElementById('employee-wage').value);
    const joinDate = document.getElementById('employee-joindate').value;
    
    const payload = { name, phone, dailyWageRate, joinDate };
    
    try {
        if (id) {
            // Update
            await apiCall(`/employees/${id}`, {
                method: 'PUT',
                body: JSON.stringify(payload)
            });
        } else {
            // Create
            await apiCall('/employees', {
                method: 'POST',
                body: JSON.stringify({ ...payload, active: true })
            });
        }
        closeEmployeeModal();
        loadEmployeesData();
    } catch (err) {
        console.error(err);
    }
}

async function editEmployee(id) {
    try {
        const emp = await apiCall(`/employees/${id}`);
        
        document.getElementById('dialog-title').textContent = 'Edit Employee Profile';
        document.getElementById('employee-id').value = emp.id;
        document.getElementById('employee-name').value = emp.name;
        document.getElementById('employee-phone').value = emp.phone || '';
        document.getElementById('employee-wage').value = emp.dailyWageRate;
        document.getElementById('employee-joindate').value = emp.joinDate;
        
        employeeDialog.showModal();
    } catch (err) {
        console.error(err);
    }
}

async function toggleEmployeeActive(id) {
    if (confirm('Are you sure you want to change this employee active/inactive status?')) {
        try {
            await apiCall(`/employees/${id}/toggle-active`, { method: 'PUT' });
            loadEmployeesData();
        } catch (err) {
            console.error(err);
        }
    }
}

/* ==========================================================================
   ROUTE 3: WORK RECORD / ATTENDANCE
   ========================================================================== */
async function loadWorkLogTab() {
    try {
        // Load active employees in selector
        const activeEmps = await apiCall('/employees');
        populateEmployeeSelectors(activeEmps, ['work-employee']);
        
        // Reset form
        document.getElementById('work-log-form').reset();
        document.getElementById('daily-wage-help').textContent = '';
        
        // Set dates
        const today = new Date();
        const startStr = new Date(today.getTime() - 15 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];
        document.getElementById('work-start').value = startStr;
        document.getElementById('work-end').value = today.toISOString().split('T')[0];
        calculateAutoDays();

        // Load all active (unsettled) work records
        loadUnsettledWorkRecords();
    } catch (err) {
        console.error(err);
    }
}

function updateDailyWageHelp() {
    const empId = document.getElementById('work-employee').value;
    const helpEl = document.getElementById('daily-wage-help');
    if (!empId) {
        helpEl.textContent = '';
        return;
    }
    
    // Find rate from employees state
    // Let's query API or use local state
    const emp = state.employees.find(e => e.id == empId);
    if (emp) {
        helpEl.textContent = `Wage Rate: ₹${parseFloat(emp.dailyWageRate)}/day`;
    } else {
        helpEl.textContent = '';
    }
}

function calculateAutoDays() {
    const startVal = document.getElementById('work-start').value;
    const endVal = document.getElementById('work-end').value;
    
    if (startVal && endVal) {
        const start = new Date(startVal);
        const end = new Date(endVal);
        const timeDiff = end.getTime() - start.getTime();
        
        if (timeDiff >= 0) {
            // Days worked is difference in days + 1 (inclusive of start and end days)
            const days = Math.floor(timeDiff / (1000 * 3600 * 24)) + 1;
            document.getElementById('work-days').value = days;
        } else {
            document.getElementById('work-days').value = '';
        }
    }
}

async function loadUnsettledWorkRecords() {
    try {
        // Since there isn't a direct endpoint to list all unsettled work records, 
        // we can fetch active work records for all active employees and merge them
        const allActiveEmps = await apiCall('/employees');
        const tbody = document.querySelector('#work-records-table tbody');
        tbody.innerHTML = '';
        
        let recordsCount = 0;

        for (const emp of allActiveEmps) {
            const records = await apiCall(`/work/employee/${emp.id}/active`);
            if (records && records.length > 0) {
                records.forEach(wr => {
                    recordsCount++;
                    const tr = document.createElement('tr');
                    const gross = parseFloat(emp.dailyWageRate) * parseInt(wr.daysWorked);
                    tr.innerHTML = `
                        <td><strong>${emp.name}</strong><br><small class="text-muted">Rate: ₹${parseFloat(emp.dailyWageRate)}/day</small></td>
                        <td>${formatDate(wr.startDate)} to ${formatDate(wr.endDate)}</td>
                        <td><span class="badge" style="background: rgba(139, 92, 246, 0.15); color: #a78bfa; border:none;">${wr.daysWorked} days</span></td>
                        <td><strong>₹${gross.toLocaleString('en-IN')}</strong></td>
                        <td>${wr.remarks || '-'}</td>
                        <td class="actions-col">
                            <button class="btn btn-outline-danger btn-sm-icon" onclick="deleteWorkRecord(${wr.id})">
                                <i class="fa-solid fa-trash-can"></i>
                            </button>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            }
        }
        
        if (recordsCount === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="empty-state"><i class="fa-solid fa-circle-check"></i> All work stints settled or no shifts logged yet!</td>
                </tr>
            `;
        }
    } catch (err) {
        console.error(err);
    }
}

async function saveWorkLog(e) {
    e.preventDefault();
    
    const employeeId = parseInt(document.getElementById('work-employee').value);
    const startDate = document.getElementById('work-start').value;
    const endDate = document.getElementById('work-end').value;
    const daysWorked = parseInt(document.getElementById('work-days').value);
    const remarks = document.getElementById('work-remarks').value;

    const payload = { employeeId, startDate, endDate, daysWorked, remarks };
    
    try {
        await apiCall('/work', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        loadWorkLogTab();
    } catch (err) {
        console.error(err);
    }
}

async function deleteWorkRecord(id) {
    if (confirm('Are you sure you want to delete this working shift? This action cannot be undone.')) {
        try {
            await apiCall(`/work/${id}`, { method: 'DELETE' });
            loadWorkLogTab();
        } catch (err) {
            console.error(err);
        }
    }
}

/* ==========================================================================
   ROUTE 4: ADVANCES
   ========================================================================== */
async function loadAdvancesTab() {
    try {
        const activeEmps = await apiCall('/employees');
        populateEmployeeSelectors(activeEmps, ['advance-employee']);
        
        document.getElementById('advance-form').reset();
        document.getElementById('advance-date').value = new Date().toISOString().split('T')[0];

        // Load all outstanding advances
        loadOutstandingAdvances();
    } catch (err) {
        console.error(err);
    }
}

async function loadOutstandingAdvances() {
    try {
        const allActiveEmps = await apiCall('/employees');
        const tbody = document.querySelector('#advances-table tbody');
        tbody.innerHTML = '';
        
        let count = 0;

        for (const emp of allActiveEmps) {
            const advances = await apiCall(`/advances/employee/${emp.id}/active`);
            if (advances && advances.length > 0) {
                advances.forEach(ad => {
                    count++;
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td><strong>${emp.name}</strong></td>
                        <td>${formatDate(ad.date)}</td>
                        <td><strong class="red-text">₹${parseFloat(ad.amount).toLocaleString('en-IN')}</strong></td>
                        <td>${ad.remarks || '-'}</td>
                        <td class="actions-col">
                            <button class="btn btn-outline-danger btn-sm-icon" onclick="deleteAdvance(${ad.id})">
                                <i class="fa-solid fa-trash-can"></i>
                            </button>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            }
        }
        
        if (count === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="empty-state"><i class="fa-solid fa-circle-check"></i> No cash advances outstanding. All clear!</td>
                </tr>
            `;
        }
    } catch (err) {
        console.error(err);
    }
}

async function saveAdvance(e) {
    e.preventDefault();
    
    const employeeId = parseInt(document.getElementById('advance-employee').value);
    const date = document.getElementById('advance-date').value;
    const amount = parseFloat(document.getElementById('advance-amount').value);
    const remarks = document.getElementById('advance-remarks').value;

    const payload = { employeeId, date, amount, remarks };
    
    try {
        await apiCall('/advances', {
            method: 'POST',
            body: JSON.stringify(payload)
        });
        loadAdvancesTab();
    } catch (err) {
        console.error(err);
    }
}

async function deleteAdvance(id) {
    if (confirm('Are you sure you want to delete this advance transaction? This cannot be undone.')) {
        try {
            await apiCall(`/advances/${id}`, { method: 'DELETE' });
            loadAdvancesTab();
        } catch (err) {
            console.error(err);
        }
    }
}

/* ==========================================================================
   ROUTE 5: SETTLEMENTS CALCULATOR
   ========================================================================== */
async function loadSettlementsTab() {
    try {
        const activeEmps = await apiCall('/employees');
        populateEmployeeSelectors(activeEmps, ['settle-employee-select']);
        
        // Hide ledger and show prompt
        document.getElementById('settlement-ledger-area').style.display = 'none';
        document.getElementById('settlement-empty-prompt').style.display = 'block';
        document.getElementById('settle-employee-select').value = '';
    } catch (err) {
        console.error(err);
    }
}

async function loadEmployeeSettlementSummary(empId) {
    if (!empId) {
        document.getElementById('settlement-ledger-area').style.display = 'none';
        document.getElementById('settlement-empty-prompt').style.display = 'block';
        return;
    }

    try {
        const summary = await apiCall(`/settlements/employee/${empId}/summary`);
        state.currentSummary = summary;
        
        // Unhide ledger UI
        document.getElementById('settlement-ledger-area').style.display = 'block';
        document.getElementById('settlement-empty-prompt').style.display = 'none';
        
        // Header
        document.getElementById('ledger-emp-name').textContent = summary.employeeName;
        document.getElementById('ledger-emp-rate').textContent = parseFloat(summary.dailyWageRate);

        // Reset Remarks input
        document.getElementById('settlement-remarks').value = '';

        // Earnings lines
        const workTbody = document.querySelector('#ledger-work-table tbody');
        workTbody.innerHTML = '';
        if (summary.activeWorkRecords.length === 0) {
            workTbody.innerHTML = `<tr><td colspan="3" class="empty-state">No unsettled work stints logged.</td></tr>`;
        } else {
            summary.activeWorkRecords.forEach(wr => {
                const tr = document.createElement('tr');
                const lineEarned = parseFloat(summary.dailyWageRate) * parseInt(wr.daysWorked);
                tr.innerHTML = `
                    <td>${formatDate(wr.startDate)} to ${formatDate(wr.endDate)}</td>
                    <td><strong>${wr.daysWorked} days</strong></td>
                    <td class="text-right font-weight-bold">₹${lineEarned.toLocaleString('en-IN')}</td>
                `;
                workTbody.appendChild(tr);
            });
        }

        // Advances lines
        const advTbody = document.querySelector('#ledger-advances-table tbody');
        advTbody.innerHTML = '';
        if (summary.activeAdvanceRecords.length === 0) {
            advTbody.innerHTML = `<tr><td colspan="3" class="empty-state">No active cash advances.</td></tr>`;
        } else {
            summary.activeAdvanceRecords.forEach(ad => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${formatDate(ad.date)}</td>
                    <td>${ad.remarks || 'Advance'}</td>
                    <td class="text-right red-text">₹${parseFloat(ad.amount).toLocaleString('en-IN')}</td>
                `;
                advTbody.appendChild(tr);
            });
        }

        // Calculations summary box
        document.getElementById('ledger-summary-days').textContent = `${summary.totalDaysWorked} Days`;
        document.getElementById('ledger-summary-earned').textContent = parseFloat(summary.totalEarned).toLocaleString('en-IN');
        document.getElementById('ledger-summary-advances').textContent = parseFloat(summary.totalAdvances).toLocaleString('en-IN');
        
        const net = parseFloat(summary.netPayable);
        const netSpan = document.getElementById('ledger-summary-net');
        netSpan.textContent = net.toLocaleString('en-IN');
        
        if (net < 0) {
            netSpan.className = 'red-text';
        } else {
            netSpan.className = 'green-text';
        }

    } catch (err) {
        console.error(err);
    }
}

async function processSettlement() {
    const summary = state.currentSummary;
    if (!summary) return;

    if (summary.activeWorkRecords.length === 0 && summary.activeAdvanceRecords.length === 0) {
        alert('There is nothing outstanding to settle for this employee.');
        return;
    }

    const remarks = document.getElementById('settlement-remarks').value;
    
    if (confirm(`Do you want to finalize this salary settlement for ${summary.employeeName}?\nNet Payable Amount: ₹${parseFloat(summary.netPayable).toLocaleString('en-IN')}`)) {
        try {
            const settlement = await apiCall(`/settlements/employee/${summary.employeeId}`, {
                method: 'POST',
                body: JSON.stringify({ remarks })
            });
            
            // Settlement recorded successfully!
            alert('Settlement Recorded Successfully! Opening Printable Slip...');
            
            // Reload settlements list
            loadSettlementsTab();
            
            // Trigger receipt view popup modal
            openReceiptModal(settlement.id);
        } catch (err) {
            console.error(err);
        }
    }
}

/* ==========================================================================
   ROUTE 6: HISTORICAL RECEIPTS
   ========================================================================== */
async function loadHistoryTab() {
    try {
        const settlements = await apiCall('/settlements/history');
        const tbody = document.querySelector('#history-receipts-table tbody');
        tbody.innerHTML = '';

        if (settlements.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="empty-state"><i class="fa-solid fa-folder-open"></i> No settlement history found. Settle accounts under the Settlement Tab first!</td>
                </tr>
            `;
            return;
        }

        settlements.forEach(set => {
            const tr = document.createElement('tr');
            const periodStr = set.startDate ? `${formatDate(set.startDate)} to ${formatDate(set.endDate)}` : 'Advances Only';
            tr.innerHTML = `
                <td><strong>${formatDate(set.settlementDate)}</strong></td>
                <td><strong>${set.employee.name}</strong></td>
                <td>${periodStr}</td>
                <td>${set.totalDaysWorked} days</td>
                <td>₹${parseFloat(set.totalEarned).toLocaleString('en-IN')}</td>
                <td class="red-text">₹${parseFloat(set.totalAdvanceSubtracted).toLocaleString('en-IN')}</td>
                <td><strong class="green-text">₹${parseFloat(set.netPaid).toLocaleString('en-IN')}</strong></td>
                <td class="actions-col">
                    <button class="btn btn-secondary btn-sm-icon" onclick="openReceiptModal(${set.id})" title="Print/View Invoice Slip">
                        <i class="fa-solid fa-receipt"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error(err);
    }
}

/* ==========================================================================
   THERMAL RECEIPT POPUP ENGINE
   ========================================================================== */
const receiptDialog = document.getElementById('receiptDialog');

async function openReceiptModal(settlementId) {
    try {
        const set = await apiCall(`/settlements/${settlementId}`);
        
        // Set dynamic text values
        document.getElementById('rec-id').textContent = `MWS-${set.id.toString().padStart(6, '0')}`;
        document.getElementById('rec-date').textContent = formatDate(set.settlementDate);
        document.getElementById('rec-name').textContent = set.employee.name;
        document.getElementById('rec-rate').textContent = parseFloat(set.employee.dailyWageRate);
        
        const periodRow = document.getElementById('rec-period-row');
        if (set.startDate && set.endDate) {
            periodRow.style.display = 'block';
            document.getElementById('rec-period').textContent = `${formatDate(set.startDate)} to ${formatDate(set.endDate)}`;
        } else {
            periodRow.style.display = 'none';
        }

        // Render Work Lines
        const workContainer = document.getElementById('rec-work-lines');
        workContainer.innerHTML = '';
        if (!set.workRecords || set.workRecords.length === 0) {
            workContainer.innerHTML = `<p class="receipt-line"><span>No shifts logged</span><span>₹0.00</span></p>`;
        } else {
            set.workRecords.forEach(wr => {
                const lineEarned = parseFloat(set.employee.dailyWageRate) * parseInt(wr.daysWorked);
                const el = document.createElement('div');
                el.className = 'receipt-line';
                el.innerHTML = `
                    <span>Stint: ${formatDate(wr.startDate)}-${formatDate(wr.endDate)}<br>
                    <small>(${wr.daysWorked} days x ₹${parseFloat(set.employee.dailyWageRate)})</small></span>
                    <span class="pull-right">₹${lineEarned.toLocaleString('en-IN')}</span>
                `;
                workContainer.appendChild(el);
            });
        }

        // Render Advance Lines
        const advanceContainer = document.getElementById('rec-advance-lines');
        const advanceSec = document.getElementById('rec-advances-section');
        advanceContainer.innerHTML = '';
        
        if (!set.advanceRecords || set.advanceRecords.length === 0) {
            advanceSec.style.display = 'none';
        } else {
            advanceSec.style.display = 'block';
            set.advanceRecords.forEach(ad => {
                const el = document.createElement('div');
                el.className = 'receipt-line';
                el.innerHTML = `
                    <span>Adv: ${formatDate(ad.date)}<br>
                    <small>(${ad.remarks || 'Cash'})</small></span>
                    <span class="pull-right">- ₹${parseFloat(ad.amount).toLocaleString('en-IN')}</span>
                `;
                advanceContainer.appendChild(el);
            });
        }

        // Populate math sums
        document.getElementById('rec-gross').textContent = parseFloat(set.totalEarned).toLocaleString('en-IN');
        document.getElementById('rec-deductions').textContent = parseFloat(set.totalAdvanceSubtracted).toLocaleString('en-IN');
        document.getElementById('rec-net').textContent = parseFloat(set.netPaid).toLocaleString('en-IN');
        document.getElementById('rec-remarks').textContent = set.remarks || 'None';

        receiptDialog.showModal();
    } catch (err) {
        console.error(err);
    }
}

function closeReceiptModal() {
    receiptDialog.close();
}

function printReceipt() {
    window.print();
}

/* ==========================================================================
   GLOBAL UTILITIES & DYNAMIC HELPERS
   ========================================================================== */

function populateEmployeeSelectors(employeesList, selectIds) {
    selectIds.forEach(id => {
        const select = document.getElementById(id);
        if (!select) return;
        
        const currentValue = select.value;
        
        // Reset and populate
        select.innerHTML = '<option value="">-- Choose Employee --</option>';
        
        employeesList.forEach(emp => {
            if (emp.active || emp.id == currentValue) {
                const opt = document.createElement('option');
                opt.value = emp.id;
                opt.textContent = `${emp.name} (₹${parseFloat(emp.dailyWageRate)}/day)`;
                select.appendChild(opt);
            }
        });
        
        // Restore previous value if active
        select.value = currentValue;
    });
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const options = { day: '2-digit', month: 'short', year: 'numeric' };
    return date.toLocaleDateString('en-IN', options);
}
