/**
 * Employee Directory App Logic
 * Ported from Kotlin/Compose to Vanilla JS
 */

const CSV_URL = 'https://docs.google.com/spreadsheets/d/1C6YbIDQnwW-v1Ha1bcXzrWJCq8ZJxOGRA6N4GMo7bX4/export?format=csv';

let employees = [];
let bookmarks = JSON.parse(localStorage.getItem('bookmarks') || '{}');
let currentTab = 'home';
let searchQuery = '';

// DOM Elements
const employeeListEl = document.getElementById('employeeList');
const searchInput = document.getElementById('searchInput');
const refreshBtn = document.getElementById('refreshBtn');
const menuBtn = document.getElementById('menuBtn');
const navItems = document.querySelectorAll('.nav-item');
const aboutModal = document.getElementById('aboutModal');

/**
 * Initialize App
 */
async function init() {
    setupEventListeners();
    await fetchEmployees();
    render();
}

/**
 * Fetch data from Google Sheets
 */
async function fetchEmployees() {
    try {
        const response = await fetch(CSV_URL);
        const csvText = await response.text();
        const lines = csvText.split('\n');
        
        if (lines.length <= 1) return;

        employees = lines.slice(1)
            .filter(line => line.trim() !== '')
            .map(line => {
                // Handle CSV with potential quotes (similar to Kotlin regex)
                const parts = line.split(/,(?=(?:(?:[^"]*"){2})*[^"]*$)/)
                    .map(part => part.trim().replace(/^"|"$/g, '').replace(/""/g, '"'));

                if (parts.length >= 5) {
                    const name = parts[0] || '';
                    const dept = parts[1] || '';
                    const section = parts[2] || '';
                    const desig = parts[3] || '';
                    const cpf = parts[4] || '';
                    const ext = parts[5] || '';
                    const mobile = parts[6] || '';
                    const imageUrl = parts[8] || null;

                    const id = cpf || `${name}-${dept}-${section}`.replace(/\s+/g, '_');

                    return {
                        id, name, designation: desig, department: dept,
                        cpfNo: cpf, section, extNo: ext, phoneNumber: mobile,
                        imageUrl
                    };
                }
                return null;
            })
            .filter(emp => emp !== null);

        showToast('Updated successfully');
    } catch (error) {
        console.error('Fetch error:', error);
        showToast('Update failed');
    }
}

/**
 * Render Employee Cards
 */
function render() {
    let filtered = employees.filter(emp => {
        const matchesSearch = emp.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                             emp.cpfNo.toLowerCase().includes(searchQuery.toLowerCase()) ||
                             emp.department.toLowerCase().includes(searchQuery.toLowerCase());
        
        if (currentTab === 'bookmarks') {
            return matchesSearch && bookmarks[emp.id];
        }
        return matchesSearch;
    });

    // Sort: Bookmarked first, then alphabetical
    filtered.sort((a, b) => {
        const aBook = bookmarks[a.id] ? 1 : 0;
        const bBook = bookmarks[b.id] ? 1 : 0;
        if (aBook !== bBook) return bBook - aBook;
        return a.name.localeCompare(b.name);
    });

    employeeListEl.innerHTML = '';

    if (filtered.length === 0) {
        employeeListEl.innerHTML = `
            <div class="empty-state">
                <p>${currentTab === 'bookmarks' ? 'No bookmarks found.' : 'No employees found.'}</p>
            </div>
        `;
        return;
    }

    filtered.forEach((emp, index) => {
        const isBookmarked = bookmarks[emp.id];
        const card = document.createElement('div');
        card.className = 'employee-card';
        card.style.animationDelay = `${index * 0.05}s`;
        
        card.innerHTML = `
            <div class="profile-img-wrapper">
                ${emp.imageUrl ? `<img src="${emp.imageUrl}" alt="${emp.name}">` : `
                    <svg viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                `}
            </div>
            <div class="employee-info">
                <div class="employee-name">${emp.name}, ${emp.designation}</div>
                <div class="employee-cpf">${emp.cpfNo}</div>
                <div class="employee-section">${emp.section}</div>
            </div>
            <button class="icon-btn bookmark-btn ${isBookmarked ? 'active' : ''}" data-id="${emp.id}" onclick="event.stopPropagation(); window.toggleBookmark('${emp.id}')">
                <svg viewBox="0 0 24 24"><path d="${isBookmarked ? 'M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2z' : 'M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2zm0 15l-5-2.18L7 18V5h10v13z'}"/></svg>
            </button>
        `;

        card.addEventListener('click', () => showDetails(emp));
        employeeListEl.appendChild(card);
    });
}

/**
 * App Actions
 */
window.toggleBookmark = (id) => {
    if (bookmarks[id]) {
        delete bookmarks[id];
    } else {
        bookmarks[id] = true;
    }
    localStorage.setItem('bookmarks', JSON.stringify(bookmarks));
    render();
};

function showDetails(emp) {
    const detailBody = document.getElementById('detailBody');
    const isBookmarked = bookmarks[emp.id];
    
    detailBody.innerHTML = `
        <div class="detail-image-section">
            ${emp.imageUrl ? `<img src="${emp.imageUrl}" alt="${emp.name}">` : `
                <svg viewBox="0 0 24 24"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
            `}
        </div>
        <div class="detail-info-section">
            <div class="detail-header">
                <div class="detail-name-row">${emp.name}, ${emp.designation}</div>
                <div class="detail-cpf-row">${emp.cpfNo}</div>
                <div class="detail-dept-row">${emp.department}</div>
            </div>

            <div class="call-action-section">
                <a href="tel:${emp.phoneNumber}" class="call-btn">
                    <svg viewBox="0 0 24 24"><path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/></svg>
                </a>
                <span class="call-label">CALL</span>
            </div>

            <div class="detail-grid">
                <div class="detail-item">
                    <div class="detail-item-label">NAME</div>
                    <div class="detail-item-value">${emp.name}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-item-label">DESIGNATION</div>
                    <div class="detail-item-value">${emp.designation}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-item-label">CPF NO</div>
                    <div class="detail-item-value">${emp.cpfNo}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-item-label">SECTION</div>
                    <div class="detail-item-value">${emp.section}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-item-label">DEPARTMENT</div>
                    <div class="detail-item-value">${emp.department}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-item-label">EXT NO</div>
                    <div class="detail-item-value">${emp.extNo}</div>
                </div>
                <div class="detail-item">
                    <div class="detail-item-label">MOBILE</div>
                    <div class="detail-item-value">${emp.phoneNumber}</div>
                </div>
            </div>
        </div>
    `;
    
    document.getElementById('detailModal').classList.add('active');
}

function showToast(msg) {
    const toast = document.getElementById('toast');
    toast.textContent = msg;
    toast.classList.add('active');
    setTimeout(() => toast.classList.remove('active'), 3000);
}

function closeModal(id) {
    document.getElementById(id).classList.remove('active');
}

/**
 * Event Listeners
 */
function setupEventListeners() {
    searchInput.addEventListener('input', (e) => {
        searchQuery = e.target.value;
        render();
    });

    refreshBtn.addEventListener('click', async () => {
        refreshBtn.classList.add('spinning');
        await fetchEmployees();
        render();
        refreshBtn.classList.remove('spinning');
    });

    menuBtn.addEventListener('click', () => {
        aboutModal.classList.add('active');
    });

    navItems.forEach(item => {
        item.addEventListener('click', () => {
            const tab = item.dataset.tab;
            currentTab = tab;
            navItems.forEach(n => n.classList.remove('active'));
            item.classList.add('active');
            
            // UI adjustments for tab switch
            document.querySelector('header h1').textContent = tab.charAt(0).toUpperCase() + tab.slice(1);
            
            render();
        });
    });

    // Close modals on click outside
    window.addEventListener('click', (e) => {
        if (e.target.classList.contains('modal')) {
            e.target.classList.remove('active');
        }
    });
}

// Global functions for inline HTML event handlers
window.closeModal = closeModal;

// Start the app
init();

// Register Service Worker
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('sw.js').then(reg => {
            console.log('SW registered:', reg.scope);
        }).catch(err => {
            console.log('SW failed:', err);
        });
    });
}
