function logout() {
    var str = location.pathname;
    if (str.search("Deposit.htm") != -1) {
        var applet = document.getElementById('atril-capture-applet');
        applet.rollback();
    }
}

