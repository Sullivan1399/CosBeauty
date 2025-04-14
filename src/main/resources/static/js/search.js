// search.js

document.getElementById('searchInput').addEventListener('input', function () {
    const keyword = this.value.trim();

    if (!keyword) {
        document.getElementById('searchResults').innerHTML = '';
        return;
    }

    fetch(`/api/cart/search?keyword=${encodeURIComponent(keyword)}`)
        .then(response => response.json())
        .then(data => {
            let html = '';
            if (data.length > 0) {
                data.forEach(product => {
                    html += `
            <div class="product-item" data-id="${product.id}" style="display: flex; align-items: center; padding: 10px; border-bottom: 1px solid #eee;">
              <img src="${product.imageUrl}" style="width: 40px; height: 40px; margin-right: 10px;">
              <div style="flex: 1;">
                <strong>${product.productName}</strong><br>
                <span>${product.price}</span>
              </div>
            </div>
          `;
                });
            } else {
                html = '<div style="padding: 10px;">Không tìm thấy kết quả.</div>';
            }

            document.getElementById('searchResults').innerHTML = html;
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('searchResults').innerHTML = '<div style="padding: 10px;">Đã có lỗi xảy ra.</div>';
        });
});

// Event delegation for click handling
document.getElementById('searchResults').addEventListener('click', function(event) {
    const productItem = event.target.closest('.product-item');
    if (productItem) {
        const productId = productItem.getAttribute('data-id');
        window.location.href = `/shoping-cart.html?productId=${productId}`;
    }
});

document.addEventListener('DOMContentLoaded', function () {
    // Lắng nghe sự kiện click vào dấu "+"
    document.querySelector('.tab-add').addEventListener('click', function () {
        // Gọi hàm để tạo một tab mới giống như "Hóa đơn 1"
        createNewTab();
    });

    // Hàm tạo tab mới
    function createNewTab() {
        // Lấy phần tử mẫu của tab "Hóa đơn 1"
        const tabTemplate = document.querySelector('.tab.active');

        // Tạo phần tử tab mới từ mẫu
        const newTab = tabTemplate.cloneNode(true);


        // Thêm tab mới vào phần tử chứa các tab
        const tabsContainer = document.querySelector('.tabs');
        tabsContainer.insertBefore(newTab, document.querySelector('.tab-add'));

        // Lắng nghe sự kiện đóng tab mới
        newTab.querySelector('.close-btn').addEventListener('click', function () {
            tabsContainer.removeChild(newTab);
        });

        // Đảm bảo tab mới sẽ được hiển thị khi hover vào (tab có màu nền như "Hóa đơn 1")
        newTab.classList.add('active');

        // Tự động chuyển thành active tab khi được click
        newTab.addEventListener('click', function () {
            // Xóa class active khỏi tất cả các tab
            document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
            // Thêm class active vào tab vừa click
            newTab.classList.add('active');
        });
    }
});
