// document.addEventListener("DOMContentLoaded", function() {
//     var diffAccCheckbox = document.getElementById("diff-acc");
//     var diffAddressSection = document.getElementById("diff-address-section");
//
//     // Hiển thị hoặc ẩn phần nhập địa chỉ khi checkbox thay đổi
//     diffAccCheckbox.addEventListener("change", function() {
//         if (diffAccCheckbox.checked) {
//             diffAddressSection.style.display = "block";
//         } else {
//             diffAddressSection.style.display = "none";
//         }
//     });
//
//     // Khởi tạo các phần tử select
//     var cities = document.getElementById("cities");
//     var districts = document.getElementById("districts");
//     var wards = document.getElementById("wards");
//     var Parameter =
//         {
//             url: "/js/address.json",
//             method: "GET",
//             responseType: "json",
//         };
//     var promise = axios(Parameter);
//     promise.then(function(result)
//     {
//         renderCity(result.data);
//     });
//     function renderCity(data)
//     {
//         let cityName = document.getElementById("cityName").value;
//         let districtName = document.getElementById("districtName").value;
//         let wardName = document.getElementById("wardName").value;
//         for (const c of data)
//         {
//             cities.options[cities.options.length] = new Option(c.Name, c.Id);
//         }
//         let selectedCity = data.find(city => city.Name === cityName);
//         if (selectedCity)
//         {
//             cities.value = selectedCity.Id;
//             for (const d of selectedCity.Districts)
//             {
//                 districts.options[districts.options.length] = new Option(d.Name, d.Id);
//             }
//             let selectedDistrict = selectedCity.Districts.find(district => district.Name === districtName);
//             if (selectedDistrict)
//             {
//                 districts.value = selectedDistrict.Id;
//                 for (const w of selectedDistrict.Wards)
//                 {
//                     wards.options[wards.options.length] = new Option(w.Name, w.Id);
//                 }
//                 let selectedWard = selectedDistrict.Wards.find(w => w.Name === wardName);
//                 if (selectedWard)
//                 {
//                     wards.value = selectedWard.Id;
//                 }
//             }
//         }
//         cities.onchange = function()
//         {
//             districts.length = 1;
//             wards.length = 1;
//             document.getElementById("cityName").value = cities.options[cities.selectedIndex].text;
//
//             if (this.value !== "")
//             {
//                 const selectedCity = data.find(n => n.Id === this.value);
//                 for (const d of selectedCity.Districts)
//                 {
//                     districts.options[districts.options.length] = new Option(d.Name, d.Id);
//                 }
//             }
//         };
//         districts.onchange = function()
//         {
//             wards.length = 1;
//             document.getElementById("districtName").value = districts.options[districts.selectedIndex].text;
//
//             const selectedCity = data.find(n => n.Id === cities.value);
//             if (this.value !== "")
//             {
//                 const selectedDistrict = selectedCity.Districts.find(n => n.Id === this.value);
//
//                 for (const w of selectedDistrict.Wards)
//                 {
//                     wards.options[wards.options.length] = new Option(w.Name, w.Id);
//                 }
//             }
//         };
//         wards.onchange = function()
//         {
//             document.getElementById("wardName").value = wards.options[wards.selectedIndex].text;
//         };
//     }
// });
