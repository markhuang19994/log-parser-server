(function loading() {
    const loading = document.querySelector('.loading');
    const defaultLoadingText = 'Loading';

    window.startLoading = function (text) {
        loading.querySelector('.loadingTxt').innerText = text || defaultLoadingText;
        loading.classList.add('on');
    };

    window.endLoading = function () {
        loading.querySelector('.loadingTxt').innerText = defaultLoadingText;
        loading.classList.remove('on');
    };
})();
