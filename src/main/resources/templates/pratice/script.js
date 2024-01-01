function toggleMenu() {
    var navbar = document.getElementById("navbar");
    navbar.style.display = navbar.style.display === "block" ? "none" : "block";
}

// 각 메뉴 항목에 대한 클릭 이벤트 리스너 추가
document.querySelectorAll('#navbar a').forEach(item => {
    item.addEventListener('click', event => {
        // 메뉴 닫기
        var navbar = document.getElementById("navbar");
        navbar.style.display = "none";

        // 해당 섹션으로 스크롤
        const hash = item.getAttribute('href');
        const targetSection = document.querySelector(hash);
        if (targetSection) {
            targetSection.scrollIntoView({behavior: 'smooth'});
        }
    });
});
