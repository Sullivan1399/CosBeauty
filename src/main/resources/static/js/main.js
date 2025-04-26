/*  ---------------------------------------------------
    Template Name: Ogani
    Description:  Ogani eCommerce  HTML Template
    Author: Colorlib
    Author URI: https://colorlib.com
    Version: 1.0
    Created: Colorlib
---------------------------------------------------------  */

'use strict';

(function ($) {

    /*------------------
        Preloader
    --------------------*/
    $(window).on('load', function () {
        $(".loader").fadeOut();
        $("#preloder").delay(200).fadeOut("slow");

        /*------------------
            Gallery filter
        --------------------*/
        $('.featured__controls li').on('click', function () {
            $('.featured__controls li').removeClass('active');
            $(this).addClass('active');
        });
        if ($('.featured__filter').length > 0) {
            var containerEl = document.querySelector('.featured__filter');
            var mixer = mixitup(containerEl);
        }
    });

    /*------------------
        Background Set
    --------------------*/
    $('.set-bg').each(function () {
        var bg = $(this).data('setbg');
        $(this).css('background-image', 'url(' + bg + ')');
    });

    //Humberger Menu
    $(".humberger__open").on('click', function () {
        $(".humberger__menu__wrapper").addClass("show__humberger__menu__wrapper");
        $(".humberger__menu__overlay").addClass("active");
        $("body").addClass("over_hid");
    });

    $(".humberger__menu__overlay").on('click', function () {
        $(".humberger__menu__wrapper").removeClass("show__humberger__menu__wrapper");
        $(".humberger__menu__overlay").removeClass("active");
        $("body").removeClass("over_hid");
    });

    /*------------------
		Navigation
	--------------------*/
    $(".mobile-menu").slicknav({
        prependTo: '#mobile-menu-wrap',
        allowParentLinks: true
    });

    /*-----------------------
        Categories Slider
    ------------------------*/
    $(".categories__slider").owlCarousel({
        loop: true,
        margin: 0,
        items: 4,
        dots: false,
        nav: true,
        navText: ["<span class='fa fa-angle-left'><span/>", "<span class='fa fa-angle-right'><span/>"],
        animateOut: 'fadeOut',
        animateIn: 'fadeIn',
        smartSpeed: 1200,
        autoHeight: false,
        autoplay: true,
        responsive: {

            0: {
                items: 1,
            },

            480: {
                items: 2,
            },

            768: {
                items: 3,
            },

            992: {
                items: 4,
            }
        }
    });


    $('.hero__categories__all').on('click', function(){
        $('.hero__categories ul').slideToggle(400);
    });

    /*--------------------------
        Latest Product Slider
    ----------------------------*/
    $(".latest-product__slider").owlCarousel({
        loop: true,
        margin: 0,
        items: 1,
        dots: false,
        nav: true,
        navText: ["<span class='fa fa-angle-left'><span/>", "<span class='fa fa-angle-right'><span/>"],
        smartSpeed: 1200,
        autoHeight: false,
        autoplay: true
    });

    /*-----------------------------
        Product Discount Slider
    -------------------------------*/
    $(".product__discount__slider").owlCarousel({
        loop: true,
        margin: 0,
        items: 3,
        dots: true,
        smartSpeed: 1200,
        autoHeight: false,
        autoplay: true,
        responsive: {

            320: {
                items: 1,
            },

            480: {
                items: 2,
            },

            768: {
                items: 2,
            },

            992: {
                items: 3,
            }
        }
    });

    /*---------------------------------
        Product Details Pic Slider
    ----------------------------------*/
    $(".product__details__pic__slider").owlCarousel({
        loop: true,
        margin: 20,
        items: 4,
        dots: true,
        smartSpeed: 1200,
        autoHeight: false,
        autoplay: true
    });

    /*-----------------------
		Price Range Slider
	------------------------ */
    var rangeSlider = $(".price-range"),
        minamount = $("#minamount"),
        maxamount = $("#maxamount"),
        minPrice = rangeSlider.data('min'),
        maxPrice = rangeSlider.data('max');
    rangeSlider.slider({
        range: true,
        min: minPrice,
        max: maxPrice,
        values: [minPrice, maxPrice],
        slide: function (event, ui) {
            minamount.val('$' + ui.values[0]);
            maxamount.val('$' + ui.values[1]);
        }
    });
    minamount.val('$' + rangeSlider.slider("values", 0));
    maxamount.val('$' + rangeSlider.slider("values", 1));

    /*--------------------------
        Select
    ----------------------------*/
    $("select").niceSelect();

    /*------------------
		Single Product
	--------------------*/
    $('.product__details__pic__slider img').on('click', function () {

        var imgurl = $(this).data('imgbigurl');
        var bigImg = $('.product__details__pic__item--large').attr('src');
        if (imgurl != bigImg) {
            $('.product__details__pic__item--large').attr({
                src: imgurl
            });
        }
    });

    /*-------------------
		Register form
	--------------------- */
    $('.form-holder').delegate("input", "focus", function(){
		$('.form-holder').removeClass("active");
		$(this).parent().addClass("active");
	})

    /*-------------------
		Quantity change
	--------------------- */
    // var proQty = $('.pro-qty');
    // proQty.prepend('<span class="dec qtybtn">-</span>');
    // proQty.append('<span class="inc qtybtn">+</span>');
    // proQty.on('click', '.qtybtn', function () {
    //     var $button = $(this);
    //     var oldValue = $button.parent().find('input').val();
    //     if ($button.hasClass('inc')) {
    //         var newVal = parseFloat(oldValue) + 1;
    //     } else {
    //         // Don't allow decrementing below zero
    //         if (oldValue > 0) {
    //             var newVal = parseFloat(oldValue) - 1;
    //         } else {
    //             newVal = 0;
    //         }
    //     }
    //     $button.parent().find('input').val(newVal);
    // });

})(jQuery);


//     (function($){
//     'use strict';
//
//     // 1) Thêm nút +/– vào mỗi .pro-qty (cái bạn đã có sẵn)
//     var proQty = $('.pro-qty');
//     proQty.prepend('<span class="dec qtybtn">-</span>');
//     proQty.append('<span class="inc qtybtn">+</span>');
//
//     // 2) Hàm định dạng VND
//     function formatVND(amount) {
//     return amount.toLocaleString('vi-VN') + '₫';
// }

    // 3) Hàm tính lại tổng giỏ hàng

(function($){
    'use strict';

    // Hàm format tiền VND
    function formatVND(n){
        return n.toLocaleString('vi-VN') + '₫';
    }

    // Hàm tính lại tổng giỏ hàng
    function recalcCart(){
        let total = 0;
        $('tbody tr').each(function(){
            const $row   = $(this);
            const price  = parseFloat($row.find('.shoping__cart__price').data('price')) || 0;
            const qty    = parseInt($row.find('input.quantity-input').val(), 10) || 0;
            const line   = price * qty;
            total += line;
            $row.find('.shoping__cart__total').text(formatVND(line));
        });
        $('#checkout-total').text(formatVND(total));
    }

    // Thêm nút + – nếu chưa có
    $('.pro-qty').each(function(){
        const $el = $(this);
        if ($el.find('.dec').length === 0) {
            $el.prepend('<span class="dec qtybtn">-</span>');
            $el.append('<span class="inc qtybtn">+</span>');
        }
    });

    // Bắt sự kiện click nút +/–
    $('.pro-qty').on('click', '.qtybtn', function () {
        const $btn = $(this),
            $row = $btn.closest('tr'),
            $inp = $row.find('input.quantity-input'),
            oldV = parseInt($inp.val(), 10) || 1,
            maxV = parseInt($inp.data('max'), 10) || 999,
            newV = $btn.hasClass('inc') ? Math.min(maxV, oldV + 1) : Math.max(1, oldV - 1);

        $inp.val(newV);

        recalcCart($row, newV);
    });


    // Tính tổng ban đầu khi trang vừa load
    $(document).ready(recalcCart);

})(jQuery);

// Luu thay doi product trong cart to mysql
$('#update-cart-form').on('submit', function(e){
    e.preventDefault();

    const $form = $(this);
    // Xóa các input cũ để tránh trùng
    $form.find('input[name="quantities"]').remove();

    $('tbody tr').each(function(){
        const $row = $(this);
        const productId = $row.find('input.quantity-input').attr('id').split('-')[1];
        const quantity  = $row.find('input.quantity-input').val();

        $('<input>')
            .attr('type', 'hidden')
            .attr('name', 'quantities[' + productId + ']')
            .val(quantity)
            .appendTo($form);
    });

    $form.off('submit').submit(); // Submit thật sự sau khi đã gắn input
});

// phi ship
// document.addEventListener("DOMContentLoaded", function () {
//     var addressInput = document.getElementById("address-input");
//     var shippingFeeElement = document.getElementById("shipping-fee");
//
//     function updateShippingFee() {
//         var address = addressInput.value.toLowerCase();
//         if (address.includes("tp. hồ chí minh") || address.includes("tp. ho chi minh") || address.includes("tp.hcm")) {
//             shippingFeeElement.textContent = "0 đ";
//         } else if (address.trim() === "") {
//             shippingFeeElement.textContent = "0 đ"; // Trường hợp trống
//         } else {
//             shippingFeeElement.textContent = "25.000 đ";
//         }
//     }
//
//     // Cập nhật phí ship khi người dùng gõ địa chỉ
//     addressInput.addEventListener("input", updateShippingFee);
//
//     // Gọi ngay khi trang tải để kiểm tra giá trị ban đầu
//     updateShippingFee();
// });
//
// // Tong tien hang
// function calculateTotal() {
//     let total = 0;
//     const items = document.querySelectorAll(".cart-item"); // Truy xuất tất cả các mục giỏ hàng
//
//     console.log('Total items:', items.length); // Kiểm tra số lượng phần tử
//
//     items.forEach(item => {
//         // Lấy giá trị từ các thuộc tính data-price-check và data-quantity-check
//         const price = parseInt(item.querySelector('.item-price').getAttribute('data-price-check'));
//         const quantity = parseInt(item.querySelector('.item-quantity').getAttribute('data-quantity-check'));
//
//         console.log(`Item:`, item);
//         console.log(`Price: ${price}, Quantity: ${quantity}`);
//
//         // Kiểm tra nếu giá trị hợp lệ
//         if (isNaN(price) || isNaN(quantity)) {
//             console.log("Skipping invalid item.");
//             return; // Bỏ qua sản phẩm không hợp lệ
//         }
//
//         total += price * quantity;
//     });
//
//     console.log(`Total calculated: ${total}`); // Kiểm tra tổng tính được
//
//     // Hiển thị tổng tiền hàng
//     document.getElementById("checkout-total-check").textContent = total.toLocaleString() + ' đ';
// }
//
// // Tính tổng ngay khi trang tải
// window.onload = function() {
//     console.log("Window loaded");
//     calculateTotal();
// };
// // tih finish total
// document.addEventListener('DOMContentLoaded', function() {
//     // Hàm định dạng giá tiền sang định dạng VND
//     function formatPrice(price) {
//         return price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
//     }
//
//     // Lấy các phần tử HTML
//     const checkoutTotalCheck = document.getElementById('checkout-total-check');
//     const shippingFeeElement = document.getElementById('shipping-fee');
//     const grandTotalElement = document.getElementById('grand-total');
//
//     // Giả sử tổng tiền hàng được lấy từ một nguồn nào đó (ví dụ: từ server hoặc giỏ hàng)
//     // Ở đây, tôi sẽ dùng một giá trị mẫu, bạn có thể thay bằng dữ liệu thực tế
//     let totalCheck = 1000000; // Tổng tiền hàng mẫu (1,000,000 VND)
//     let shippingFee = parseFloat(shippingFeeElement.textContent.replace(' đ', '').replace('.', '')) || 0;
//
//     // Cập nhật tổng tiền hàng
//     checkoutTotalCheck.textContent = formatPrice(totalCheck);
//
//     // Tính tổng cộng (grand total)
//     let grandTotal = totalCheck + shippingFee;
//
//     // Cập nhật tổng cộng
//     grandTotalElement.textContent = formatPrice(grandTotal);
//
//     // Hàm cập nhật phí ship (nếu cần thay đổi động)
//     function updateShippingFee(newFee) {
//         shippingFee = newFee;
//         shippingFeeElement.textContent = formatPrice(newFee);
//         grandTotal = totalCheck + shippingFee;
//         grandTotalElement.textContent = formatPrice(grandTotal);
//     }
//
//     // Ví dụ: Gọi hàm updateShippingFee khi phí ship thay đổi
//     // updateShippingFee(30000); // Cập nhật phí ship mới nếu cần
// });
//

// full full
// Đảm bảo mã chỉ chạy một lần
let isInitialized = false;

document.addEventListener('DOMContentLoaded', function() {
    if (isInitialized) return;
    isInitialized = true;

    // Hàm định dạng giá tiền sang định dạng VND
    function formatPrice(price) {
        return price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    }

    // Lấy các phần tử HTML
    const checkoutTotalCheck = document.getElementById('checkout-total-check');
    const shippingFeeElement = document.getElementById('shipping-fee');
    const grandTotalElement = document.getElementById('grand-total');
    const addressInput = document.getElementById('address-input');

    // Lấy các input ẩn để gửi dữ liệu
    const inputSubtotal = document.getElementById('input-subtotal');
    const inputShippingFee = document.getElementById('input-shipping-fee');
    const inputGrandTotal = document.getElementById('input-grand-total');

    // Hàm tính tổng tiền hàng
    function calculateTotal() {
        let total = 0;
        const items = document.querySelectorAll('.cart-item');

        items.forEach(item => {
            const priceElement = item.querySelector('.item-price');
            const quantityElement = item.querySelector('.item-quantity');

            if (!priceElement || !quantityElement) return;

            const price = parseInt(priceElement.getAttribute('data-price-check')) || 0;
            const quantity = parseInt(quantityElement.getAttribute('data-quantity-check')) || 0;

            if (isNaN(price) || isNaN(quantity)) return;

            total += price * quantity;
        });

        return total;
    }

    // Hàm cập nhật phí ship dựa trên địa chỉ
    function updateShippingFee() {
        const address = addressInput.value.toLowerCase();
        let shippingFee = 0;

        if (address.includes('tp. hồ chí minh') || address.includes('tp. ho chi minh') || address.includes('tp.hcm')) {
            shippingFee = 0;
        } else if (address.trim() === '') {
            shippingFee = 0;
        } else {
            shippingFee = 25000; // Phí ship 25,000 VND
        }

        shippingFeeElement.textContent = formatPrice(shippingFee);
        return shippingFee;
    }

    // Hàm cập nhật tổng cộng
    function updateGrandTotal(totalCheck, shippingFee) {
        const grandTotal = totalCheck + shippingFee;
        grandTotalElement.textContent = formatPrice(grandTotal);
    }

    // Cập nhật các input ẩn
    function updateHiddenInputs(subtotal, shippingFee, grandTotal) {
        inputSubtotal.value = subtotal.toFixed(2);  // Đảm bảo có 2 chữ số thập phân
        inputShippingFee.value = shippingFee.toFixed(2);  // Đảm bảo có 2 chữ số thập phân
        inputGrandTotal.value = grandTotal.toFixed(2);  // Đảm bảo có 2 chữ số thập phân
    }

    // Khởi tạo khi trang tải
    function initializeCheckout() {
        const totalCheck = calculateTotal();
        checkoutTotalCheck.textContent = formatPrice(totalCheck);

        const shippingFee = updateShippingFee();
        updateGrandTotal(totalCheck, shippingFee);

        // Cập nhật input ẩn khi khởi tạo
        updateHiddenInputs(totalCheck, shippingFee, totalCheck + shippingFee);
    }

    // Gọi khởi tạo
    initializeCheckout();

    // Cập nhật phí ship và tổng cộng khi người dùng nhập địa chỉ
    addressInput.addEventListener('input', function() {
        const totalCheck = parseFloat(checkoutTotalCheck.textContent.replace(/[^\d]/g, '')) || 0;
        const shippingFee = updateShippingFee();
        const grandTotal = totalCheck + shippingFee;
        updateGrandTotal(totalCheck, shippingFee);

        // Cập nhật input ẩn
        updateHiddenInputs(totalCheck, shippingFee, grandTotal);
    });

    // Cập nhật khi có sự thay đổi trong giỏ hàng
    document.addEventListener('cartUpdated', function() {
        const totalCheck = calculateTotal();
        checkoutTotalCheck.textContent = formatPrice(totalCheck);

        const shippingFee = updateShippingFee();
        const grandTotal = totalCheck + shippingFee;
        updateGrandTotal(totalCheck, shippingFee);
        // Update the "Tổng tiền hàng" element with totalCheck
        const totalPriceElement = document.getElementById('totalPrice');
        if (totalPriceElement) {
            totalPriceElement.textContent = `Tổng tiền hàng: ${formatPrice(totalCheck)}`;
        }
        // Cập nhật input ẩn
        updateHiddenInputs(totalCheck, shippingFee, grandTotal);
    });
});
//check quantities

    // Lấy tất cả các input có class "quantity-input"
    document.querySelectorAll('.quantity-input').forEach(input => {
    input.addEventListener('input', function() {
        // Lấy giá trị min và max từ thuộc tính HTML
        const min = parseInt(this.min) || 1; // Nếu không có min, mặc định là 1
        const max = parseInt(this.max) || Infinity; // Nếu không có max, cho phép bất kỳ giá trị lớn
        let value = parseInt(this.value); // Giá trị hiện tại của input

        // Kiểm tra giá trị có hợp lệ không
        if (isNaN(value) || value < min) {
            this.value = min; // Nếu nhỏ hơn min, đặt lại thành min
            alert(`Số lượng phải lớn hơn hoặc bằng ${min}`);
        } else if (value > max) {
            this.value = max; // Nếu lớn hơn max, đặt lại thành max
            alert(`Số lượng không được vượt quá ${max}`);
        }
    });
});

    // (Tùy chọn) Kiểm tra khi submit form
    document.querySelector('form').addEventListener('submit', function(event) {
    let isValid = true;
    document.querySelectorAll('.quantity-input').forEach(input => {
    const min = parseInt(input.min) || 1;
    const max = parseInt(input.max) || Infinity;
    const value = parseInt(input.value);

    if (isNaN(value) || value < min || value > max) {
    isValid = false;
    alert(`Vui lòng kiểm tra lại số lượng cho sản phẩm ${input.id}`);
}
});

    if (!isValid) {
    event.preventDefault(); // Ngăn form submit nếu có lỗi
}
});